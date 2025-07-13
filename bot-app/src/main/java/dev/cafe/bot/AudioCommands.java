package dev.cafe.bot;

import dev.cafe.core.AudioController;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Audio slash commands for music playback.
 */
public class AudioCommands extends ListenerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AudioCommands.class);
  
  private final AudioController audioController;

  public AudioCommands(AudioController audioController) {
    this.audioController = audioController;
  }

  @Override
  public void onReady(ReadyEvent event) {
    event.getJDA().updateCommands().addCommands(
        Commands.slash("play", "Play a song from YouTube or URL")
            .addOption(OptionType.STRING, "query", "Song name or URL", true),
        Commands.slash("skip", "Skip the current song"),
        Commands.slash("stop", "Stop playback and clear queue"),
        Commands.slash("pause", "Pause the current song"),
        Commands.slash("resume", "Resume the paused song"),
        Commands.slash("queue", "Show the current queue"),
        Commands.slash("ping", "Check if the bot is responsive")
    ).queue();
    logger.info("Audio commands registered");
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getGuild() == null) {
      event.reply("This command can only be used in servers!").setEphemeral(true).queue();
      return;
    }

    long guildId = event.getGuild().getIdLong();
    
    switch (event.getName()) {
      case "play":
        handlePlay(event, guildId);
        break;
      case "skip":
        handleSkip(event, guildId);
        break;
      case "stop":
        handleStop(event, guildId);
        break;
      case "pause":
        handlePause(event, guildId);
        break;
      case "resume":
        handleResume(event, guildId);
        break;
      case "queue":
        handleQueue(event, guildId);
        break;
      case "ping":
        handlePing(event);
        break;
    }
  }

  private void handlePlay(SlashCommandInteractionEvent event, long guildId) {
    String query = event.getOption("query").getAsString();
    event.deferReply().queue();
    
    audioController.play(guildId, query)
        .thenAccept(result -> event.getHook().editOriginal(result).queue())
        .exceptionally(throwable -> {
          logger.error("Error playing track", throwable);
          event.getHook().editOriginal("Error playing track: " + throwable.getMessage()).queue();
          return null;
        });
  }

  private void handleSkip(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.skip(guildId);
    event.reply(result).queue();
  }

  private void handleStop(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.stop(guildId);
    event.reply(result).queue();
  }

  private void handlePause(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.pause(guildId);
    event.reply(result).queue();
  }

  private void handleResume(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.resume(guildId);
    event.reply(result).queue();
  }

  private void handleQueue(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.getQueueInfo(guildId);
    event.reply(result).queue();
  }

  private void handlePing(SlashCommandInteractionEvent event) {
    long gatewayPing = event.getJDA().getGatewayPing();
    event.reply("Pong! Gateway ping: " + gatewayPing + "ms").queue();
  }
}