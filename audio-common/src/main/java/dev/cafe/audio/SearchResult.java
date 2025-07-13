package dev.cafe.audio;

import java.util.List;

/**
 * Result of an audio search operation.
 */
public interface SearchResult {
  enum Type {
    TRACK_LOADED,
    PLAYLIST_LOADED,
    SEARCH_RESULT,
    NO_MATCHES,
    LOAD_FAILED
  }
  
  Type getType();
  
  AudioTrack getTrack();
  
  List<AudioTrack> getTracks();
  
  String getPlaylistName();
  
  String getErrorMessage();
}