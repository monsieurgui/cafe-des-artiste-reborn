package dev.cafe.bot;

import dev.cafe.cache.guild.GuildSettings;
import dev.cafe.cache.guild.GuildSettingsRepository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles the /setup command for guild-specific configuration. */
@Singleton
public class SetupCommands extends ListenerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupCommands.class);
  private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");
  private static final Pattern CHANNEL_NAME_PATTERN = Pattern.compile("#([a-z0-9-]+)");

  private final GuildSettingsRepository guildSettingsRepository;
  private final ConcurrentHashMap<Long, Long> pendingSetups =
      new ConcurrentHashMap<>(); // userId -> guildId
  private final ScheduledExecutorService timeoutExecutor =
      Executors.newSingleThreadScheduledExecutor();

  @Inject
  public SetupCommands(GuildSettingsRepository guildSettingsRepository) {
    this.guildSettingsRepository = guildSettingsRepository;
  }


  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("setup")) {
      return;
    }

    if (event.getGuild() == null) {
      event.reply("This command can only be used in a server.").setEphemeral(true).queue();
      return;
    }

    event.deferReply(true).queue();

    long userId = event.getUser().getIdLong();
    long guildId = event.getGuild().getIdLong();

    event
        .getUser()
        .openPrivateChannel()
        .queue(
            privateChannel -> {
              privateChannel
                  .sendMessage(
                      "Please link a text channel for me to post the queue and now-playing messages in. Just type the channel name with a #, like #music or #test-bot-playground.")
                  .queue(
                      success -> {
                        pendingSetups.put(userId, guildId);
                        timeoutExecutor.schedule(
                            () -> {
                              if (pendingSetups.remove(userId, guildId)) {
                                privateChannel
                                    .sendMessage("Your setup session has timed out.")
                                    .queue();
                              }
                            },
                            2,
                            TimeUnit.MINUTES);
                        event.getHook().editOriginal("Check your DMs to continue setup.").queue();
                      },
                      failure -> {
                        LOGGER.warn("Could not send DM to user {}", userId, failure);
                        event
                            .getHook()
                            .editOriginal(
                                "I couldn't send you a DM. Please check your privacy settings.")
                            .queue();
                      });
            },
            failure -> {
              LOGGER.warn("Could not open DM with user {}", userId, failure);
              event
                  .getHook()
                  .editOriginal(
                      "I couldn't open a DM with you. Please check your privacy settings.")
                  .queue();
            });
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (!event.isFromType(ChannelType.PRIVATE) || event.getAuthor().isBot()) {
      return;
    }

    long userId = event.getAuthor().getIdLong();
    if (pendingSetups.containsKey(userId)) {
      long guildId = pendingSetups.get(userId);
      String messageContent = event.getMessage().getContentRaw();
      
      var guild = event.getJDA().getGuildById(guildId);
      if (guild == null) {
        event
            .getChannel()
            .sendMessage("Something went wrong, I can't find the server anymore.")
            .queue();
        pendingSetups.remove(userId);
        return;
      }

      final TextChannel channel;
      
      // Try to match channel mention format first: <#123456789>
      Matcher mentionMatcher = CHANNEL_MENTION_PATTERN.matcher(messageContent);
      if (mentionMatcher.find()) {
        String channelIdStr = mentionMatcher.group(1);
        long channelId = Long.parseLong(channelIdStr);
        channel = guild.getTextChannelById(channelId);
      } else {
        // Try to match channel name format: #channel-name
        Matcher nameMatcher = CHANNEL_NAME_PATTERN.matcher(messageContent);
        if (nameMatcher.find()) {
          String channelName = nameMatcher.group(1);
          channel = guild.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);
        } else {
          channel = null;
        }
      }

      if (channel == null) {
        event
            .getChannel()
            .sendMessage(
                "That channel doesn't seem to exist in the server. Please mention a valid text channel or use the format #channel-name.")
            .queue();
        return;
      }

      var selfMember = guild.getSelfMember();
      if (!selfMember.hasPermission(
          channel, Permission.MESSAGE_SEND, Permission.MESSAGE_MANAGE)) {
        event
            .getChannel()
            .sendMessage(
                "I don't have the required permissions (`Send Messages`, `Manage Messages`) in that channel. Please grant them and try again.")
            .queue();
        return;
      }

      channel
          .sendMessage("🎶 **Queue** (auto-updated)")
          .queue(
              queueMsg -> {
                channel
                    .sendMessage("▶️ **Now playing…**")
                    .queue(
                        nowPlayingMsg -> {
                          queueMsg.pin().queue();
                          nowPlayingMsg.pin().queue();

                          GuildSettings settings =
                              new GuildSettings(
                                  guildId,
                                  channel.getIdLong(),
                                  queueMsg.getIdLong(),
                                  nowPlayingMsg.getIdLong());
                          guildSettingsRepository.save(settings);

                          event
                              .getChannel()
                              .sendMessage(
                                  "Great! I've linked to "
                                      + channel.getAsMention()
                                      + ". I've created and pinned the queue and now-playing messages there.")
                              .queue();
                          pendingSetups.remove(userId);
                        });
              });
    }
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    long guildId = event.getGuild().getIdLong();
    
    // Check if guild is already configured
    if (guildSettingsRepository.findById(guildId).isPresent()) {
      LOGGER.info("Guild {} is already configured, skipping setup notification", guildId);
      return;
    }

    // Find a member with administrator permissions to send setup message
    event.getGuild().loadMembers().onSuccess(members -> {
      for (Member member : members) {
        if (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR)) {
          sendSetupNotification(member, event.getGuild().getName());
          break;
        }
      }
    }).onError(error -> {
      LOGGER.warn("Failed to load members for guild {}: {}", guildId, error.getMessage());
    });
  }

  private void sendSetupNotification(Member member, String guildName) {
    member.getUser().openPrivateChannel().queue(
        privateChannel -> {
          String message = String.format(
              "👋 Hi! I've just joined **%s** and I need to be set up before I can work properly.\n\n" +
              "Please run `/setup` in your server to configure me. This will let me create pinned messages " +
              "for the queue and now-playing status in a channel of your choice.\n\n" +
              "Without setup, I won't be able to show queue updates or track what's currently playing!",
              guildName);
          
          privateChannel.sendMessage(message).queue(
              success -> {
                LOGGER.info("Sent setup notification to {} for guild {}", member.getUser().getAsTag(), guildName);
              },
              failure -> {
                LOGGER.warn("Could not send setup notification to {} for guild {}: {}", 
                    member.getUser().getAsTag(), guildName, failure.getMessage());
              });
        },
        failure -> {
          LOGGER.warn("Could not open DM with {} for guild {}: {}", 
              member.getUser().getAsTag(), guildName, failure.getMessage());
        });
  }
}
