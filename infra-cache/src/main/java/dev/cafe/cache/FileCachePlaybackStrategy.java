package dev.cafe.cache;

import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.dagger.Streaming;
import java.io.File;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A playback strategy that attempts to play from a local cache first. */
@Singleton
public class FileCachePlaybackStrategy implements PlaybackStrategy {
  private static final Logger logger = LoggerFactory.getLogger(FileCachePlaybackStrategy.class);

  private final PlaybackStrategy streamingStrategy;
  private final MostPlayedService mostPlayedService;
  private final TrackCacheService trackCacheService;

  @Inject
  public FileCachePlaybackStrategy(
      @Streaming PlaybackStrategy streamingStrategy,
      MostPlayedService mostPlayedService,
      TrackCacheService trackCacheService) {
    this.streamingStrategy = streamingStrategy;
    this.mostPlayedService = mostPlayedService;
    this.trackCacheService = trackCacheService;
  }

  @Override
  public void startPlayback(long guildId, AudioTrack track) {
    try {
      if (mostPlayedService.isCached(track.getVideoId())) {
        File cachedFile = trackCacheService.getCacheFile(track.getVideoId());
        if (cachedFile.exists()) {
          logger.info("Playing from cache: {}", track.getVideoId());
          // We would need to update the PlaybackStrategy to be able to play a local file.
          // For now, we'll just log and fall back to streaming.
          streamingStrategy.startPlayback(guildId, track);
        } else {
          logger.warn(
              "Track {} was marked as cached, but file not found. Streaming instead.",
              track.getVideoId());
          mostPlayedService.setCached(track.getVideoId(), false);
          streamingStrategy.startPlayback(guildId, track);
        }
      } else {
        streamingStrategy.startPlayback(guildId, track);
      }
    } catch (SQLException e) {
      logger.error("Failed to check cache status for track {}", track.getVideoId(), e);
      streamingStrategy.startPlayback(guildId, track);
    }
  }

  @Override
  public void stopPlayback(long guildId) {
    streamingStrategy.stopPlayback(guildId);
  }

  @Override
  public void pausePlayback(long guildId) {
    streamingStrategy.pausePlayback(guildId);
  }

  @Override
  public void resumePlayback(long guildId) {
    streamingStrategy.resumePlayback(guildId);
  }

  @Override
  public void setVolume(long guildId, int volume) {
    streamingStrategy.setVolume(guildId, volume);
  }

  @Override
  public boolean isPlaying(long guildId) {
    return streamingStrategy.isPlaying(guildId);
  }

  @Override
  public boolean isPaused(long guildId) {
    return streamingStrategy.isPaused(guildId);
  }

  @Override
  public AudioTrack getCurrentTrack(long guildId) {
    return streamingStrategy.getCurrentTrack(guildId);
  }

  @Override
  public long getPosition(long guildId) {
    return streamingStrategy.getPosition(guildId);
  }
}
