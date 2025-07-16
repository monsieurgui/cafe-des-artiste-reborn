package com.cafedesartistes.botapp;

import com.cafedesartistes.botapp.listeners.GuildSetupListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entrypoint for the Discord Music Bot.
 * Initializes JDA and registers event listeners.
 */
public class BotAppApplication {
    private static final Logger logger = LoggerFactory.getLogger(BotAppApplication.class);

    public static void main(String[] args) {
        System.out.println("=== Bot App starting ===");
        logger.info("Bot App starting...");
        
        String botToken = System.getenv("DISCORD_BOT_TOKEN");
        if (botToken == null || botToken.isEmpty()) {
            System.out.println("ERROR: DISCORD_BOT_TOKEN environment variable is not set");
            logger.error("DISCORD_BOT_TOKEN environment variable is not set");
            System.exit(1);
        }
        System.out.println("Bot token found, length: " + botToken.length());
        
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=RUNSCRIPT FROM 'classpath:init.sql'";
            logger.warn("DATABASE_URL not set, using in-memory H2 database: {}", databaseUrl);
        }
        logger.info("Using database URL: {}", databaseUrl.replaceAll("password=[^;]*", "password=***"));
        
        try {
            System.out.println("Creating JDA instance...");
            JDA jda = JDABuilder.createDefault(botToken)
                .enableIntents(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(new GuildSetupListener(databaseUrl))
                .build()
                .awaitReady();
            
            System.out.println("Bot successfully started! Logged in as: " + jda.getSelfUser().getAsTag());
            logger.info("Bot successfully started and ready! Logged in as: {}", 
                       jda.getSelfUser().getAsTag());
            
            // Keep the application running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.out.println("Failed to start bot: " + e.getMessage());
            e.printStackTrace();
            logger.error("Failed to start bot", e);
            System.exit(1);
        }
    }
}