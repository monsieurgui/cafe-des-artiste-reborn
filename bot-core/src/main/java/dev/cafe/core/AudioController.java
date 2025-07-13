package dev.cafe.core;

import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.audio.SearchResult;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.metrics.MetricsBinder;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Coordinates audio playback across guilds with queue management. */
@Singleton
public class AudioController {
  private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
  private static final int CACHE_THRESHOLD = 5;

  private final AudioSearchService searchService;
  private final PlaybackStrategy playbackStrategy;
  private final MetricsBinder metrics;
  private final MostPlayedService mostPlayedService;
  private final TrackCacheService trackCacheService;
  private final ConcurrentMap<Long, TrackQueue> guildQueues = new ConcurrentHashMap<>();
  private final ScheduledExecutorService prefetchExecutor = Executors.newScheduledThreadPool(2);

  @Inject
  public AudioController(
      AudioSearchService searchService,
      PlaybackStrategy playbackStrategy,
      MetricsBinder metrics,
      MostPlayedService mostPlayedService,
      TrackCacheService trackCacheService) {
    this.searchService = searchService;
    this.playbackStrategy = playbackStrategy;
    this.metrics = metrics;
    this.mostPlayedService = mostPlayedService;
    this.trackCacheService = trackCacheService;
  }

  public CompletableFuture<String> play(long guildId, String query) {
    return searchService.search(query).thenApply(result -> handleSearchResult(guildId, result));
  }

  public CompletableFuture<String> playUrl(long guildId, String url) {
    return searchService.loadTrack(url).thenApply(result -> handleSearchResult(guildId, result));
  }

  public CompletableFuture<SearchResult> searchOnly(String query) {
    return searchService.search(query);
  }

  public String playSelectedTrack(long guildId, dev.cafe.audio.AudioTrack track) {
    TrackQueue queue = getOrCreateQueue(guildId);

    if (!playbackStrategy.isPlaying(guildId)) {
      startPlayback(guildId, track);
      return "Now playing: " + track.getTitle();
    } else {
      queue.add(track);
      return "Added to queue: " + track.getTitle();
    }
  }

  private String handleSearchResult(long guildId, SearchResult result) {
    TrackQueue queue = getOrCreateQueue(guildId);

    switch (result.getType()) {
      case TRACK_LOADED:
        AudioTrack track = result.getTrack();
        if (!playbackStrategy.isPlaying(guildId)) {
          startPlayback(guildId, track);
          return "Now playing: " + track.getTitle();
        } else {
          queue.add(track);
          return "Added to queue: " + track.getTitle();
        }

      case PLAYLIST_LOADED:
        List<AudioTrack> tracks = result.getTracks();
        if (tracks.isEmpty()) {
          return "Playlist is empty";
        }

        AudioTrack firstTrack = tracks.get(0);
        if (!playbackStrategy.isPlaying(guildId)) {
          startPlayback(guildId, firstTrack);
          tracks.stream().skip(1).forEach(queue::add);
          return "Playing playlist: "
              + result.getPlaylistName()
              + " ("
              + tracks.size()
              + " tracks)";
        } else {
          tracks.forEach(queue::add);
          return "Added playlist to queue: "
              + result.getPlaylistName()
              + " ("
              + tracks.size()
              + " tracks)";
        }

      case SEARCH_RESULT:
        List<AudioTrack> searchTracks = result.getTracks();
        if (searchTracks.isEmpty()) {
          return "No tracks found";
        }

        AudioTrack searchTrack = searchTracks.get(0);
        if (!playbackStrategy.isPlaying(guildId)) {
          startPlayback(guildId, searchTrack);
          return "Now playing: " + searchTrack.getTitle();
        } else {
          queue.add(searchTrack);
          return "Added to queue: " + searchTrack.getTitle();
        }

      case NO_MATCHES:
        return "No matches found";

      case LOAD_FAILED:
        return "Failed to load track: " + result.getErrorMessage();

      default:
        return "Unknown search result type";
    }
  }

  public String skip(long guildId) {
    TrackQueue queue = getOrCreateQueue(guildId);
    Optional<AudioTrack> current = queue.current();

    if (!current.isPresent()) {
      return "Nothing is currently playing";
    }

    Optional<AudioTrack> next = queue.next();
    if (next.isPresent()) {
      startPlayback(guildId, next.get());
      return "Skipped to: " + next.get().getTitle();
    } else {
      playbackStrategy.stopPlayback(guildId);
      return "Skipped. Queue is empty.";
    }
  }

  public String stop(long guildId) {
    TrackQueue queue = getOrCreateQueue(guildId);
    queue.clear();
    playbackStrategy.stopPlayback(guildId);
    return "Playback stopped and queue cleared";
  }

  public String pause(long guildId) {
    if (!playbackStrategy.isPlaying(guildId)) {
      return "Nothing is currently playing";
    }

    playbackStrategy.pausePlayback(guildId);
    return "Playback paused";
  }

  public String resume(long guildId) {
    if (!playbackStrategy.isPaused(guildId)) {
      return "Playback is not paused";
    }

    playbackStrategy.resumePlayback(guildId);
    return "Playback resumed";
  }

  public String getQueueInfo(long guildId) {
    TrackQueue queue = getOrCreateQueue(guildId);
    Optional<AudioTrack> current = queue.current();

    if (!current.isPresent()) {
      return "Nothing is currently playing";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Now playing: ").append(current.get().getTitle()).append("\n");

    List<AudioTrack> upcoming = queue.getTracks();
    if (upcoming.isEmpty()) {
      sb.append("Queue is empty");
    } else {
      sb.append("Queue (").append(upcoming.size()).append(" tracks):\n");
      for (int i = 0; i < Math.min(upcoming.size(), 10); i++) {
        sb.append((i + 1)).append(". ").append(upcoming.get(i).getTitle()).append("\n");
      }
      if (upcoming.size() > 10) {
        sb.append("... and ").append(upcoming.size() - 10).append(" more");
      }
    }

    return sb.toString();
  }

  public void onTrackEnd(long guildId) {
    TrackQueue queue = getOrCreateQueue(guildId);
    Optional<AudioTrack> next = queue.next();

    if (next.isPresent()) {
      startPlayback(guildId, next.get());
      logger.info("Auto-playing next track: {}", next.get().getTitle());

      // Prefetch next track in background
      prefetchNextTrack(guildId);
    } else {
      logger.info("Queue is empty for guild {}", guildId);
    }
  }

  private void prefetchNextTrack(long guildId) {
    prefetchExecutor.submit(
        () -> {
          try {
            TrackQueue queue = getOrCreateQueue(guildId);
            List<AudioTrack> upcomingTracks = queue.getTracks();

            if (!upcomingTracks.isEmpty()) {
              AudioTrack nextTrack = upcomingTracks.get(0);
              // For Lavaplayer, tracks are already loaded, but we could trigger buffering here
              logger.debug("Prefetched next track for guild {}: {}", guildId, nextTrack.getTitle());
            }
          } catch (Exception e) {
            logger.warn("Failed to prefetch next track for guild {}", guildId, e);
          }
        });
  }

  private void startPlayback(long guildId, AudioTrack track) {
    playbackStrategy.startPlayback(guildId, track);
    metrics.recordTrackPlayed();
    logger.info("Started playback for guild {}: {}", guildId, track.getTitle());
    handleCaching(track);
  }

  private void handleCaching(AudioTrack track) {
    if (mostPlayedService == null) {
      return; // Service failed to initialize
    }
    try {
      int playCount = mostPlayedService.incrementPlayCount(track.getVideoId());
      if (playCount >= CACHE_THRESHOLD && !mostPlayedService.isCached(track.getVideoId())) {
        trackCacheService.cacheTrack(track.getVideoId());
        mostPlayedService.setCached(track.getVideoId());
      }
    } catch (SQLException e) {
      logger.error("Failed to update play count for track {}", track.getVideoId(), e);
    }
  }

  private TrackQueue getOrCreateQueue(long guildId) {
    return guildQueues.computeIfAbsent(guildId, k -> new TrackQueue());
  }
}
