package dev.cafe.audio.lavalink;

import dev.arbjerg.lavalink.protocol.v4.Track;
import dev.cafe.audio.AudioTrack;
import java.time.Duration;

/**
 * Lavalink implementation of AudioTrack.
 */
public class LavalinkAudioTrack implements AudioTrack {
  private final Track track;

  public LavalinkAudioTrack(Track track) {
    this.track = track;
  }

  @Override
  public String getTitle() {
    return track.getInfo().getTitle();
  }

  @Override
  public String getAuthor() {
    return track.getInfo().getAuthor();
  }

  @Override
  public String getUrl() {
    return track.getInfo().getUri();
  }

  @Override
  public Duration getDuration() {
    return Duration.ofMillis(track.getInfo().getLength());
  }

  @Override
  public String getArtworkUrl() {
    return track.getInfo().getArtworkUrl();
  }

  @Override
  public boolean isSeekable() {
    return track.getInfo().getIsSeekable();
  }

  @Override
  public Object getSourceTrack() {
    return track;
  }

  public Track getLavalinkTrack() {
    return track;
  }

  public String getTrackData() {
    return track.getEncoded();
  }
}