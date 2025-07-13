package dev.cafe.core;

import dev.cafe.audio.AudioTrack;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages user playlists with create, read, update, delete operations. */
@Singleton
public class PlaylistManager {
  private static final Logger logger = LoggerFactory.getLogger(PlaylistManager.class);

  private final ConcurrentMap<String, Playlist> playlists = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, List<String>> userPlaylists = new ConcurrentHashMap<>();

  @Inject
  public PlaylistManager() {}

  public Playlist createPlaylist(long userId, String name) {
    String playlistId = UUID.randomUUID().toString();
    Playlist playlist = new Playlist(playlistId, name, userId);

    playlists.put(playlistId, playlist);
    userPlaylists.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(playlistId);

    logger.info("Created playlist '{}' for user {}", name, userId);
    return playlist;
  }

  public Optional<Playlist> getPlaylist(String playlistId) {
    return Optional.ofNullable(playlists.get(playlistId));
  }

  public List<Playlist> getUserPlaylists(long userId) {
    return userPlaylists.getOrDefault(userId, List.of()).stream()
        .map(playlists::get)
        .filter(playlist -> playlist != null)
        .collect(Collectors.toList());
  }

  public List<Playlist> getPublicPlaylists() {
    return playlists.values().stream().filter(Playlist::isPublic).collect(Collectors.toList());
  }

  public boolean deletePlaylist(String playlistId, long userId) {
    Playlist playlist = playlists.get(playlistId);
    if (playlist == null || playlist.getOwnerId() != userId) {
      return false;
    }

    playlists.remove(playlistId);
    List<String> userPlaylistIds = userPlaylists.get(userId);
    if (userPlaylistIds != null) {
      userPlaylistIds.remove(playlistId);
    }

    logger.info("Deleted playlist '{}' for user {}", playlist.getName(), userId);
    return true;
  }

  public boolean addTrackToPlaylist(String playlistId, AudioTrack track, long userId) {
    Playlist playlist = playlists.get(playlistId);
    if (playlist == null || playlist.getOwnerId() != userId) {
      return false;
    }

    playlist.addTrack(track);
    logger.debug("Added track '{}' to playlist '{}'", track.getTitle(), playlist.getName());
    return true;
  }

  public boolean removeTrackFromPlaylist(String playlistId, int trackIndex, long userId) {
    Playlist playlist = playlists.get(playlistId);
    if (playlist == null || playlist.getOwnerId() != userId) {
      return false;
    }

    boolean removed = playlist.removeTrack(trackIndex);
    if (removed) {
      logger.debug("Removed track at index {} from playlist '{}'", trackIndex, playlist.getName());
    }
    return removed;
  }

  public String loadPlaylistToQueue(
      String playlistId, AudioController audioController, long guildId) {
    Playlist playlist = playlists.get(playlistId);
    if (playlist == null) {
      return "Playlist not found.";
    }

    if (playlist.isEmpty()) {
      return "Playlist '" + playlist.getName() + "' is empty.";
    }

    List<AudioTrack> tracks = playlist.getTracks();
    for (AudioTrack track : tracks) {
      audioController.playSelectedTrack(guildId, track);
    }

    return String.format(
        "Loaded %d tracks from playlist '%s' to queue.", tracks.size(), playlist.getName());
  }

  public int getTotalPlaylists() {
    return playlists.size();
  }

  public int getTotalTracks() {
    return playlists.values().stream().mapToInt(Playlist::size).sum();
  }
}
