package dev.cafe.audio.lavaplayer;

import dev.cafe.audio.AudioTrack;
import java.time.Duration;

/** Lavaplayer implementation of AudioTrack. */
public class LavaplayerAudioTrack implements AudioTrack {
  private final com.sedmelluq.discord.lavaplayer.track.AudioTrack lavaTrack;

  public LavaplayerAudioTrack(com.sedmelluq.discord.lavaplayer.track.AudioTrack lavaTrack) {
    this.lavaTrack = lavaTrack;
  }

  @Override
  public String getTitle() {
    return lavaTrack.getInfo().title;
  }

  @Override
  public String getAuthor() {
    return lavaTrack.getInfo().author;
  }

  @Override
  public String getUrl() {
    return lavaTrack.getInfo().uri;
  }

  @Override
  public String getVideoId() {
    return lavaTrack.getIdentifier();
  }

  @Override
  public Duration getDuration() {
    return Duration.ofMillis(lavaTrack.getDuration());
  }

  @Override
  public String getArtworkUrl() {
    return lavaTrack.getInfo().artworkUrl;
  }

  @Override
  public boolean isSeekable() {
    return lavaTrack.isSeekable();
  }

  @Override
  public Object getSourceTrack() {
    return lavaTrack;
  }

  public com.sedmelluq.discord.lavaplayer.track.AudioTrack getLavaTrack() {
    return lavaTrack;
  }
}
