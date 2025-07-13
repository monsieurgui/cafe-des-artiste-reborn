package dev.cafe.audio;

/** Strategy for handling audio playback lifecycle. */
public interface PlaybackStrategy {
  void startPlayback(long guildId, AudioTrack track);

  void stopPlayback(long guildId);

  void pausePlayback(long guildId);

  void resumePlayback(long guildId);

  void setVolume(long guildId, int volume);

  boolean isPlaying(long guildId);

  boolean isPaused(long guildId);
}
