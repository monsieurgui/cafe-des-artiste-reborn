package dev.cafe.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.cafe.audio.SearchResult;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lavaplayer implementation of SearchResult.
 */
public class LavaplayerSearchResult implements SearchResult {
  private final Type type;
  private final LavaplayerAudioTrack track;
  private final List<dev.cafe.audio.AudioTrack> tracks;
  private final String playlistName;
  private final String errorMessage;

  private LavaplayerSearchResult(Type type, LavaplayerAudioTrack track, 
                                 List<dev.cafe.audio.AudioTrack> tracks, 
                                 String playlistName, String errorMessage) {
    this.type = type;
    this.track = track;
    this.tracks = tracks;
    this.playlistName = playlistName;
    this.errorMessage = errorMessage;
  }

  public static LavaplayerSearchResult trackLoaded(AudioTrack track) {
    return new LavaplayerSearchResult(Type.TRACK_LOADED, 
        new LavaplayerAudioTrack(track), null, null, null);
  }

  public static LavaplayerSearchResult playlistLoaded(AudioPlaylist playlist) {
    List<dev.cafe.audio.AudioTrack> tracks = playlist.getTracks().stream()
        .map(LavaplayerAudioTrack::new)
        .collect(Collectors.toList());
    
    return new LavaplayerSearchResult(Type.PLAYLIST_LOADED, null, tracks, 
        playlist.getName(), null);
  }

  public static LavaplayerSearchResult searchResult(List<AudioTrack> tracks) {
    List<dev.cafe.audio.AudioTrack> wrappedTracks = tracks.stream()
        .map(LavaplayerAudioTrack::new)
        .collect(Collectors.toList());
    
    return new LavaplayerSearchResult(Type.SEARCH_RESULT, null, wrappedTracks, 
        null, null);
  }

  public static LavaplayerSearchResult noMatches() {
    return new LavaplayerSearchResult(Type.NO_MATCHES, null, null, null, null);
  }

  public static LavaplayerSearchResult loadFailed(FriendlyException exception) {
    return new LavaplayerSearchResult(Type.LOAD_FAILED, null, null, null, 
        exception.getMessage());
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