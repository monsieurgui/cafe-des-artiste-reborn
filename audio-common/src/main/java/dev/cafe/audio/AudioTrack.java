package dev.cafe.audio;

import java.time.Duration;

/**
 * Represents an audio track with metadata.
 */
public interface AudioTrack {
  String getTitle();
  
  String getAuthor();
  
  String getUrl();
  
  Duration getDuration();
  
  String getArtworkUrl();
  
  boolean isSeekable();
  
  Object getSourceTrack();
}