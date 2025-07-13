package dev.cafe.audio.lavalink;

import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.arbjerg.lavalink.protocol.v4.PlaylistInfo;
import dev.arbjerg.lavalink.protocol.v4.Track;
import dev.cafe.audio.SearchResult;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lavalink implementation of SearchResult.
 */
public class LavalinkSearchResult implements SearchResult {
  private final Type type;
  private final LavalinkAudioTrack track;
  private final List<dev.cafe.audio.AudioTrack> tracks;
  private final String playlistName;
  private final String errorMessage;

  private LavalinkSearchResult(Type type, LavalinkAudioTrack track,
                               List<dev.cafe.audio.AudioTrack> tracks,
                               String playlistName, String errorMessage) {
    this.type = type;
    this.track = track;
    this.tracks = tracks;
    this.playlistName = playlistName;
    this.errorMessage = errorMessage;
  }

  public static LavalinkSearchResult fromLoadResult(LoadResult loadResult) {
    switch (loadResult.getLoadType()) {
      case TRACK:
        Track track = loadResult.getData().as(Track.class);
        return new LavalinkSearchResult(Type.TRACK_LOADED,
            new LavalinkAudioTrack(track), null, null, null);

      case PLAYLIST:
        var playlistData = loadResult.getData().as(LoadResult.PlaylistData.class);
        PlaylistInfo info = playlistData.getInfo();
        List<dev.cafe.audio.AudioTrack> tracks = playlistData.getTracks().stream()
            .map(LavalinkAudioTrack::new)
            .collect(Collectors.toList());
        return new LavalinkSearchResult(Type.PLAYLIST_LOADED, null, tracks,
            info.getName(), null);

      case SEARCH:
        List<Track> searchTracks = loadResult.getData().as(List.class);
        List<dev.cafe.audio.AudioTrack> wrappedTracks = searchTracks.stream()
            .map(LavalinkAudioTrack::new)
            .collect(Collectors.toList());
        return new LavalinkSearchResult(Type.SEARCH_RESULT, null, wrappedTracks,
            null, null);

      case EMPTY:
        return new LavalinkSearchResult(Type.NO_MATCHES, null, null, null, null);

      case ERROR:
        var errorData = loadResult.getData().as(LoadResult.ErrorData.class);
        return new LavalinkSearchResult(Type.LOAD_FAILED, null, null, null,
            errorData.getMessage());

      default:
        return new LavalinkSearchResult(Type.LOAD_FAILED, null, null, null,
            "Unknown load result type: " + loadResult.getLoadType());
    }
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