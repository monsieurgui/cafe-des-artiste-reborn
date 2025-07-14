package dev.cafe.bot;

import dev.cafe.audio.lavaplayer.LavaplayerPlaybackStrategy;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main entry point for the Cafe des Artistes Discord bot. */
public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    try {
      String token = System.getenv("BOT_TOKEN");
      if (token == null || token.isEmpty()) {
        logger.error("BOT_TOKEN environment variable is required");
        System.exit(1);
      }

      // Initialize dependency injection
      BotComponent component = DaggerBotComponent.create();

      // Initialize database tables
      GuildSettingsRepository guildSettingsRepository = component.guildSettingsRepository();
      guildSettingsRepository.createTable();

      AudioController audioController = component.audioController();
      PlaylistManager playlistManager = component.playlistManager();
      CacheCommands cacheCommands = component.cacheCommands();
      SetupCommands setupCommands = component.setupCommands();
      PostUpdater postUpdater = component.postUpdater();

      JDA jda =
          JDABuilder.createDefault(token)
              .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
              .addEventListeners(
                  new AudioCommands(audioController, playlistManager, guildSettingsRepository), cacheCommands, setupCommands)
              .build();

      // Set JDA on playback strategy after JDA is built
      if (component.playbackStrategy() instanceof LavaplayerPlaybackStrategy) {
        ((LavaplayerPlaybackStrategy) component.playbackStrategy()).setJDA(jda);
      }
      postUpdater.setJda(jda);
      postUpdater.startNowPlayingUpdater();

      jda.awaitReady();
      
      // Register all commands centrally after JDA is ready
      jda.updateCommands()
          .addCommands(
              // Audio commands
              Commands.slash("leave", "Leave the voice channel"),
              Commands.slash("play", "Play audio")
                  .addSubcommands(
                      new SubcommandData("url", "Play a single song from a URL")
                          .addOption(OptionType.STRING, "url", "The URL of the song", true),
                      new SubcommandData("playlist", "Play a playlist from a URL")
                          .addOption(OptionType.STRING, "url", "The URL of the playlist", true),
                      new SubcommandData("search", "Search for a song and pick from the results")
                          .addOption(OptionType.STRING, "query", "The search query", true)),
              Commands.slash("p", "Play a song, playlist, or search for a song")
                  .addOption(OptionType.STRING, "query", "URL or search term", true, false),
              Commands.slash("playlist", "Manage playlists")
                  .addOption(OptionType.STRING, "action", "Action to perform", true)
                  .addOption(OptionType.STRING, "name", "Playlist name", false)
                  .addOption(OptionType.STRING, "id", "Playlist ID", false),
              Commands.slash("skip", "Skip the current song"),
              Commands.slash("stop", "Stop playback and clear queue"),
              Commands.slash("queue", "Show the current queue"),
              Commands.slash("ping", "Check if the bot is responsive"),
              // Cache commands
              Commands.slash("cache", "Manage the cache")
                  .addSubcommands(
                      new SubcommandData("stats", "Show cache statistics"),
                      new SubcommandData("clear", "Clear the entire cache")),
              // Setup commands
              Commands.slash("setup", "Set up the bot for this server.")
                  .setDefaultPermissions(
                      DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                  .setGuildOnly(true))
          .queue();
      
      logger.info("Bot is ready!");

    } catch (Exception e) {
      logger.error("Failed to start bot", e);
      System.exit(1);
    }
  }
}
