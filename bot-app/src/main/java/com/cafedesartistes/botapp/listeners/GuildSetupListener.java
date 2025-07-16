package com.cafedesartistes.botapp.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles guild setup process when the bot joins a new server.
 * Guides the server owner through configuring a designated text channel for the bot.
 */
public class GuildSetupListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GuildSetupListener.class);
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");
    
    // Track users who are in the setup process
    private final Set<Long> usersInSetup = ConcurrentHashMap.newKeySet();
    
    private final String databaseUrl;
    
    public GuildSetupListener(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        initializeDatabase();
    }
    
    /**
     * Initialize the database with the guild_config table if it doesn't exist.
     */
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            String createTable = """
                CREATE TABLE IF NOT EXISTS guild_config (
                    guild_id BIGINT PRIMARY KEY,
                    channel_id BIGINT NOT NULL,
                    queue_message_id BIGINT,
                    now_playing_message_id BIGINT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            try (PreparedStatement stmt = conn.prepareStatement(createTable)) {
                stmt.executeUpdate();
                logger.info("Database initialized successfully");
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
        }
    }
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        
        logger.info("Bot joined guild: {} (ID: {}), Owner: {}", 
                   guild.getName(), guild.getId(), owner.getAsTag());
        
        // Check if guild is already configured
        if (isGuildConfigured(guild.getIdLong())) {
            logger.info("Guild {} is already configured, skipping setup", guild.getId());
            return;
        }
        
        // Open DM with the guild owner
        owner.openPrivateChannel().queue(
            privateChannel -> {
                usersInSetup.add(owner.getIdLong());
                sendSetupMessage(privateChannel, guild);
            },
            error -> logger.error("Failed to open DM with guild owner {}", owner.getAsTag(), error)
        );
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Only process DM messages from users in setup process
        if (!event.isFromGuild() && 
            event.getAuthor().isBot() == false && 
            usersInSetup.contains(event.getAuthor().getIdLong())) {
            
            handleSetupResponse(event);
        }
    }
    
    /**
     * Send the initial setup message to the guild owner.
     */
    private void sendSetupMessage(PrivateChannel channel, Guild guild) {
        String message = String.format("""
            👋 Hello! I've just joined your server **%s**.
            
            To get started, I need you to designate a text channel where I'll manage the music queue and display the "Now Playing" status.
            
            Please mention the channel you'd like me to use by typing its name with a # (for example: #music-commands).
            
            You can create a new channel specifically for me, or use an existing one. I'll set up my queue and status embeds there.
            """, guild.getName());
        
        channel.sendMessage(message).queue(
            success -> logger.info("Setup message sent to {} for guild {}", 
                                 channel.getUser().getAsTag(), guild.getName()),
            error -> {
                logger.error("Failed to send setup message", error);
                usersInSetup.remove(channel.getUser().getIdLong());
            }
        );
    }
    
    /**
     * Handle the user's response to the setup message.
     */
    private void handleSetupResponse(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw().trim();
        Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(content);
        
        if (matcher.find()) {
            String channelId = matcher.group(1);
            processChannelSelection(event, channelId);
        } else {
            // Invalid response, ask again
            event.getChannel().sendMessage("""
                ❌ I didn't recognize a valid channel mention.
                
                Please mention a text channel using the # symbol (for example: #music-commands).
                You can also type the channel name and Discord will suggest channels for you to select.
                """).queue();
        }
    }
    
    /**
     * Process the selected channel and save the configuration.
     */
    private void processChannelSelection(MessageReceivedEvent event, String channelId) {
        User user = event.getAuthor();
        
        // Find the guild where this user is the owner
        Guild targetGuild = null;
        for (Guild guild : event.getJDA().getGuilds()) {
            if (guild.getOwner() != null && 
                guild.getOwner().getUser().getIdLong() == user.getIdLong() &&
                !isGuildConfigured(guild.getIdLong())) {
                targetGuild = guild;
                break;
            }
        }
        
        if (targetGuild == null) {
            event.getChannel().sendMessage("❌ I couldn't find a guild that needs configuration for your account.").queue();
            usersInSetup.remove(user.getIdLong());
            return;
        }
        
        // Verify the channel exists and is accessible
        TextChannel textChannel = targetGuild.getTextChannelById(channelId);
        if (textChannel == null) {
            event.getChannel().sendMessage("""
                ❌ I couldn't find that channel in your server. 
                Please make sure you're mentioning a text channel that exists in **%s**.
                """.formatted(targetGuild.getName())).queue();
            return;
        }
        
        // Check if bot has necessary permissions
        if (!textChannel.canTalk()) {
            event.getChannel().sendMessage("""
                ❌ I don't have permission to send messages in %s.
                Please make sure I have the "Send Messages" and "Embed Links" permissions in that channel.
                """.formatted(textChannel.getAsMention())).queue();
            return;
        }
        
        // Save configuration to database
        if (saveGuildConfiguration(targetGuild.getIdLong(), textChannel.getIdLong())) {
            usersInSetup.remove(user.getIdLong());
            
            event.getChannel().sendMessage("""
                ✅ Perfect! I've configured %s as my designated channel in **%s**.
                
                I'll now set up my queue and "Now Playing" embeds there. You're all set to start using music commands!
                """.formatted(textChannel.getAsMention(), targetGuild.getName())).queue();
            
            // TODO: Trigger embed creation in the configured channel
            // This will be implemented when the embed system is ready
            logger.info("Guild {} configured with channel {}", 
                       targetGuild.getId(), textChannel.getId());
        } else {
            event.getChannel().sendMessage("""
                ❌ There was an error saving your configuration. Please try again or contact support.
                """).queue();
        }
    }
    
    /**
     * Check if a guild is already configured in the database.
     */
    private boolean isGuildConfigured(long guildId) {
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            String query = "SELECT 1 FROM guild_config WHERE guild_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, guildId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to check guild configuration for {}", guildId, e);
            return false;
        }
    }
    
    /**
     * Save guild configuration to the database.
     */
    private boolean saveGuildConfiguration(long guildId, long channelId) {
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            String insert = "INSERT INTO guild_config (guild_id, channel_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                stmt.setLong(1, guildId);
                stmt.setLong(2, channelId);
                int rows = stmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            logger.error("Failed to save guild configuration for guild {} channel {}", 
                        guildId, channelId, e);
            return false;
        }
    }
}