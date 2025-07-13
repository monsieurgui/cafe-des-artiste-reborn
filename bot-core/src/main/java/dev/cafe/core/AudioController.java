package dev.cafe.core;

import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.audio.SearchResult;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates audio playback across guilds with queue management.
 */
@Singleton
public class AudioController {
  private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
  
  private final AudioSearchService searchService;
  private final PlaybackStrategy playbackStrategy;
  private final ConcurrentMap<Long, TrackQueue> guildQueues = new ConcurrentHashMap<>();

  @Inject
  public AudioController(AudioSearchService searchService, PlaybackStrategy playbackStrategy) {
    this.searchService = searchService;
    this.playbackStrategy = playbackStrategy;
  }

  public CompletableFuture<String> play(long guildId, String query) {
    return searchService.search(query)
        .thenApply(result -> handleSearchResult(guildId, result));
  }

  public CompletableFuture<String> playUrl(long guildId, String url) {
    return searchService.loadTrack(url)
        .thenApply(result -> handleSearchResult(guildId, result));
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
          return "Playing playlist: " + result.getPlaylistName() + " (" + tracks.size() + " tracks)";
        } else {
          tracks.forEach(queue::add);
          return "Added playlist to queue: " + result.getPlaylistName() + " (" + tracks.size() + " tracks)";
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
        return "No matches found for: " + query;
        
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
    } else {
      logger.info("Queue is empty for guild {}", guildId);
    }
  }

  private void startPlayback(long guildId, AudioTrack track) {
    playbackStrategy.startPlayback(guildId, track);
    logger.info("Started playback for guild {}: {}", guildId, track.getTitle());
  }

  private TrackQueue getOrCreateQueue(long guildId) {
    return guildQueues.computeIfAbsent(guildId, k -> new TrackQueue());
  }
}