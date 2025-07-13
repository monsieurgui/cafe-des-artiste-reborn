package dev.cafe.bot;

import dev.cafe.config.ConfigLoader;
import dev.cafe.core.AudioController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Cafe des Artistes Discord bot.
 */
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
      AudioController audioController = component.audioController();

      JDA jda = JDABuilder.createDefault(token)
          .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
          .addEventListeners(new AudioCommands(audioController))
          .build();

      jda.awaitReady();
      logger.info("Bot is ready!");

    } catch (Exception e) {
      logger.error("Failed to start bot", e);
      System.exit(1);
    }
  }
}