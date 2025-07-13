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
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
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
  public void onReady(ReadyEvent event) {
    event
        .getJDA()
        .updateCommands()
        .addCommands(
            Commands.slash("setup", "Set up the bot for this server.")
                .setDefaultPermissions(
                    DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true))
        .queue();
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
                      "Please link a text channel for me to post the queue and now-playing messages in. Just mention the channel, like #music.")
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
      Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(event.getMessage().getContentRaw());

      if (matcher.find()) {
        String channelIdStr = matcher.group(1);
        long channelId = Long.parseLong(channelIdStr);
        var guild = event.getJDA().getGuildById(guildId);
        if (guild == null) {
          event
              .getChannel()
              .sendMessage("Something went wrong, I can't find the server anymore.")
              .queue();
          pendingSetups.remove(userId);
          return;
        }

        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
          event
              .getChannel()
              .sendMessage(
                  "That channel doesn't seem to exist in the server. Please mention a valid text channel.")
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

        guildSettingsRepository.save(new GuildSettings(guildId, channelId, 0, 0));
        event
            .getChannel()
            .sendMessage(
                "Great! I've linked to "
                    + channel.getAsMention()
                    + ". I will post the queue and now-playing messages there.")
            .queue();
        pendingSetups.remove(userId);

      } else {
        event
            .getChannel()
            .sendMessage(
                "That doesn't look like a channel mention. Please mention a text channel from the server, for example: #music.")
            .queue();
      }
    }
  }
}
