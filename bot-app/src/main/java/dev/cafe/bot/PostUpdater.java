package dev.cafe.bot;

import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.dagger.Streaming;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.core.QueueChangeListener;
import dev.cafe.core.TrackQueue;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Updates the queue and now-playing messages. */
@Singleton
public class PostUpdater implements QueueChangeListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostUpdater.class);
  private final GuildSettingsRepository guildSettingsRepository;
  private final PlaybackStrategy playbackStrategy;
  private final ScheduledExecutorService nowPlayingExecutor =
      Executors.newSingleThreadScheduledExecutor();
  private JDA jda;

  @Inject
  public PostUpdater(
      GuildSettingsRepository guildSettingsRepository,
      @Streaming PlaybackStrategy playbackStrategy) {
    this.guildSettingsRepository = guildSettingsRepository;
    this.playbackStrategy = playbackStrategy;
  }

  public void setJda(JDA jda) {
    this.jda = jda;
  }

  public void startNowPlayingUpdater() {
    nowPlayingExecutor.scheduleAtFixedRate(
        this::updateAllNowPlayingMessages, 0, 15, TimeUnit.SECONDS);
  }

  private void updateAllNowPlayingMessages() {
    if (jda == null) return;
    for (Guild guild : jda.getGuilds()) {
      if (playbackStrategy.isPlaying(guild.getIdLong())
          && !playbackStrategy.isPaused(guild.getIdLong())) {
        updateNowPlayingMessage(guild.getIdLong());
      }
    }
  }

  private void updateNowPlayingMessage(long guildId) {
    guildSettingsRepository
        .findById(guildId)
        .ifPresent(
            settings -> {
              AudioTrack currentTrack = playbackStrategy.getCurrentTrack(guildId);
              if (currentTrack == null) return;

              TextChannel channel = jda.getTextChannelById(settings.channelId());
              if (channel == null) return;

              channel
                  .retrieveMessageById(settings.nowPlayingMsgId())
                  .queue(
                      message ->
                          message
                              .editMessageEmbeds(buildNowPlayingEmbed(currentTrack, guildId))
                              .queue(),
                      failure ->
                          LOGGER.warn("Could not find now-playing message for guild {}", guildId));
            });
  }

  private MessageEmbed buildNowPlayingEmbed(AudioTrack track, long guildId) {
    long position = playbackStrategy.getPosition(guildId);
    long duration = track.getDuration().toMillis();
    return new EmbedBuilder()
        .setTitle("▶️ Now Playing")
        .setDescription(
            String.format("[%s](%s) by %s", track.getTitle(), track.getUrl(), track.getAuthor()))
        .addField("Progress", buildProgressBar(position, duration), false)
        .setColor(new Color(0x1DB954))
        .build();
  }

  private String buildProgressBar(long position, long duration) {
    if (duration <= 0) return "∞";
    int barLength = 20;
    int progress = (int) ((double) position / duration * barLength);
    StringBuilder sb = new StringBuilder();
    sb.append("`");
    for (int i = 0; i < barLength; i++) {
      sb.append(i < progress ? '█' : '░');
    }
    sb.append("` ");
    sb.append(formatDuration(position)).append(" / ").append(formatDuration(duration));
    return sb.toString();
  }

  private String formatDuration(long millis) {
    long seconds = millis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    seconds %= 60;
    minutes %= 60;
    return hours > 0
        ? String.format("%d:%02d:%02d", hours, minutes, seconds)
        : String.format("%02d:%02d", minutes, seconds);
  }

  @Override
  public void onQueueChanged(long guildId, TrackQueue queue) {
    if (jda == null) {
      LOGGER.warn("JDA is not yet available for PostUpdater.");
      return;
    }

    guildSettingsRepository
        .findById(guildId)
        .ifPresent(
            settings -> {
              TextChannel channel = jda.getTextChannelById(settings.channelId());
              if (channel == null) {
                LOGGER.warn(
                    "Could not find channel with ID {} for guild {}",
                    settings.channelId(),
                    guildId);
                return;
              }

              channel
                  .retrieveMessageById(settings.queueMsgId())
                  .queue(
                      message -> message.editMessageEmbeds(buildQueueEmbed(queue)).queue(),
                      failure ->
                          LOGGER.warn(
                              "Could not find queue message with ID {} for guild {}",
                              settings.queueMsgId(),
                              guildId));
            });
  }

  private MessageEmbed buildQueueEmbed(TrackQueue queue) {
    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("🎶 Queue");
    embed.setColor(new Color(0x1DB954));

    if (queue.isEmpty()) {
      embed.setDescription("The queue is empty.");
    } else {
      StringBuilder description = new StringBuilder();
      queue
          .current()
          .ifPresent(
              current -> {
                description.append("**Now Playing:**\n");
                description.append(formatTrack(current)).append("\n\n");
              });

      List<AudioTrack> upcoming = queue.getTracks();
      if (!upcoming.isEmpty()) {
        description.append("**Up Next:**\n");
        int limit = Math.min(upcoming.size(), 10);
        for (int i = 0; i < limit; i++) {
          description.append(i + 1).append(". ").append(formatTrack(upcoming.get(i))).append("\n");
        }
        if (upcoming.size() > limit) {
          description.append("...and ").append(upcoming.size() - limit).append(" more.");
        }
      }
      embed.setDescription(description.toString());
    }
    return embed.build();
  }

  private String formatTrack(AudioTrack track) {
    return String.format("[%s](%s) by %s", track.getTitle(), track.getUrl(), track.getAuthor());
  }
}
