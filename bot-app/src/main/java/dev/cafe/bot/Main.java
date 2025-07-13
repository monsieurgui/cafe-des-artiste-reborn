package dev.cafe.bot;

import dev.cafe.audio.lavaplayer.LavaplayerPlaybackStrategy;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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

      JDA jda =
          JDABuilder.createDefault(token)
              .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
              .addEventListeners(new AudioCommands(audioController, playlistManager), cacheCommands)
              .build();

      // Set JDA on playback strategy after JDA is built
      if (component.playbackStrategy() instanceof LavaplayerPlaybackStrategy) {
        ((LavaplayerPlaybackStrategy) component.playbackStrategy()).setJDA(jda);
      }

      jda.awaitReady();
      logger.info("Bot is ready!");

    } catch (Exception e) {
      logger.error("Failed to start bot", e);
      System.exit(1);
    }
  }
}
