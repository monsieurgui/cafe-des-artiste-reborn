package dev.cafe.audio.lavalink;

import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.cafe.audio.SearchResult;
import java.util.List;

/** Lavalink implementation of SearchResult. */
public class LavalinkSearchResult implements SearchResult {
  private final Type type;
  private final LavalinkAudioTrack track;
  private final List<dev.cafe.audio.AudioTrack> tracks;
  private final String playlistName;
  private final String errorMessage;

  private LavalinkSearchResult(
      Type type,
      LavalinkAudioTrack track,
      List<dev.cafe.audio.AudioTrack> tracks,
      String playlistName,
      String errorMessage) {
    this.type = type;
    this.track = track;
    this.tracks = tracks;
    this.playlistName = playlistName;
    this.errorMessage = errorMessage;
  }

  public static LavalinkSearchResult fromLoadResult(LoadResult loadResult) {
    // For now, return a simple no matches result since the API structure is unclear
    // This needs to be updated once the correct LoadType enum values are determined
    return new LavalinkSearchResult(Type.NO_MATCHES, null, null, null, null);
  }

  public static LavalinkSearchResult createError(String message) {
    return new LavalinkSearchResult(Type.LOAD_FAILED, null, null, null, message);
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public dev.cafe.audio.AudioTrack getTrack() {
    return track;
  }

  @Override
  public List<dev.cafe.audio.AudioTrack> getTracks() {
    return tracks;
  }

  @Override
  public String getPlaylistName() {
    return playlistName;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }
}
