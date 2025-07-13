package dev.cafe.core;

import dev.cafe.audio.AudioTrack;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Thread-safe queue for managing audio tracks.
 */
public class TrackQueue {
  private final BlockingDeque<AudioTrack> queue = new LinkedBlockingDeque<>();
  private final AtomicBoolean looping = new AtomicBoolean(false);
  private volatile AudioTrack currentTrack;

  public void add(AudioTrack track) {
    queue.offer(track);
  }

  public void addFirst(AudioTrack track) {
    queue.addFirst(track);
  }

  public Optional<AudioTrack> next() {
    if (looping.get() && currentTrack != null) {
      return Optional.of(currentTrack);
    }
    
    AudioTrack next = queue.poll();
    currentTrack = next;
    return Optional.ofNullable(next);
  }

  public Optional<AudioTrack> current() {
    return Optional.ofNullable(currentTrack);
  }

  public void skip() {
    if (!queue.isEmpty()) {
      currentTrack = queue.poll();
    } else {
      currentTrack = null;
    }
  }

  public void clear() {
    queue.clear();
    currentTrack = null;
  }

  public List<AudioTrack> getTracks() {
    return queue.stream().collect(Collectors.toList());
  }

  public int size() {
    return queue.size();
  }

  public boolean isEmpty() {
    return queue.isEmpty() && currentTrack == null;
  }

  public void setLooping(boolean looping) {
    this.looping.set(looping);
  }

  public boolean isLooping() {
    return looping.get();
  }
}