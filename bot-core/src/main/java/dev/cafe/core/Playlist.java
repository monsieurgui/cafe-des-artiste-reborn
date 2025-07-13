package dev.cafe.core;

import dev.cafe.audio.AudioTrack;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Represents a user playlist with tracks and metadata. */
public class Playlist {
  private final String id;
  private final String name;
  private final long ownerId;
  private final List<AudioTrack> tracks;
  private final Instant createdAt;
  private Instant modifiedAt;
  private String description;
  private boolean isPublic;

  public Playlist(String id, String name, long ownerId) {
    this.id = id;
    this.name = name;
    this.ownerId = ownerId;
    this.tracks = new ArrayList<>();
    this.createdAt = Instant.now();
    this.modifiedAt = Instant.now();
    this.isPublic = false;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public List<AudioTrack> getTracks() {
    return new ArrayList<>(tracks); // Return copy to prevent external modification
  }

  public void addTrack(AudioTrack track) {
    tracks.add(track);
    updateModifiedTime();
  }

  public boolean removeTrack(int index) {
    if (index >= 0 && index < tracks.size()) {
      tracks.remove(index);
      updateModifiedTime();
      return true;
    }
    return false;
  }

  public boolean removeTrack(AudioTrack track) {
    boolean removed = tracks.remove(track);
    if (removed) {
      updateModifiedTime();
    }
    return removed;
  }

  public void clearTracks() {
    tracks.clear();
    updateModifiedTime();
  }

  public int size() {
    return tracks.size();
  }

  public boolean isEmpty() {
    return tracks.isEmpty();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    updateModifiedTime();
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
    updateModifiedTime();
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getModifiedAt() {
    return modifiedAt;
  }

  private void updateModifiedTime() {
    this.modifiedAt = Instant.now();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Playlist playlist = (Playlist) obj;
    return Objects.equals(id, playlist.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format("Playlist{id='%s', name='%s', tracks=%d}", id, name, tracks.size());
  }
}
