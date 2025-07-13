package dev.cafe.core;

/** Listener for changes in a TrackQueue. */
@FunctionalInterface
public interface QueueChangeListener {

  /**
   * Called when the queue has changed.
   *
   * @param guildId The ID of the guild where the queue changed.
   * @param queue The updated queue.
   */
  void onQueueChanged(long guildId, TrackQueue queue);
}
