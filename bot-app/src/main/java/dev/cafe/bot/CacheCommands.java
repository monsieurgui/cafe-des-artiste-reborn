package dev.cafe.bot;

import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Cache management slash commands. */
public class CacheCommands extends ListenerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(CacheCommands.class);

  private final MostPlayedService mostPlayedService;
  private final TrackCacheService trackCacheService;

  @Inject
  public CacheCommands(MostPlayedService mostPlayedService, TrackCacheService trackCacheService) {
    this.mostPlayedService = mostPlayedService;
    this.trackCacheService = trackCacheService;
  }


  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("cache")) {
      return;
    }
    switch (event.getSubcommandName()) {
      case "stats":
        handleStats(event);
        break;
      case "clear":
        handleClear(event);
        break;
    }
  }

  private void handleStats(SlashCommandInteractionEvent event) {
    if (mostPlayedService == null) {
      event.reply("Cache service is not available.").setEphemeral(true).queue();
      return;
    }
    try {
      List<MostPlayedService.Pair<String, Integer>> topTracks =
          mostPlayedService.getTopCachedTracks(10);
      if (topTracks.isEmpty()) {
        event.reply("No tracks are cached yet.").setEphemeral(true).queue();
        return;
      }
      EmbedBuilder embed = new EmbedBuilder().setTitle("📊 Cache Statistics").setColor(0x1DB954);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < topTracks.size(); i++) {
        MostPlayedService.Pair<String, Integer> track = topTracks.get(i);
        sb.append(
            String.format("%d. **%s** - %d plays\n", i + 1, track.getKey(), track.getValue()));
      }
      embed.setDescription(sb.toString());
      event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    } catch (SQLException e) {
      logger.error("Failed to get cache stats", e);
      event.reply("Failed to get cache stats.").setEphemeral(true).queue();
    }
  }

  private void handleClear(SlashCommandInteractionEvent event) {
    if (mostPlayedService == null || trackCacheService == null) {
      event.reply("Cache service is not available.").setEphemeral(true).queue();
      return;
    }
    try {
      int deletedFiles = trackCacheService.clearCache();
      mostPlayedService.clearCacheFlags();
      event
          .reply(String.format("Cleared %d files from the cache.", deletedFiles))
          .setEphemeral(true)
          .queue();
    } catch (SQLException e) {
      logger.error("Failed to clear cache", e);
      event.reply("Failed to clear cache.").setEphemeral(true).queue();
    }
  }
}
