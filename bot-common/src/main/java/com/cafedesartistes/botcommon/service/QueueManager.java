package com.cafedesartistes.botcommon.service;

import com.cafedesartistes.botcommon.model.Song;
import java.util.List;

/**
 * Interface for managing the song queue in the music bot system.
 * Provides operations for adding, removing, and retrieving songs from the queue.
 */
public interface QueueManager {

    /**
     * Adds a song to the end of the queue.
     *
     * @param song the song to add to the queue
     * @return true if the song was successfully added, false otherwise
     */
    boolean addSong(Song song);

    /**
     * Retrieves and removes the next song from the front of the queue.
     *
     * @return the next song in the queue, or null if the queue is empty
     */
    Song getNextSong();

    /**
     * Removes a song at the specified index from the queue.
     *
     * @param index the zero-based index of the song to remove
     * @return the removed song, or null if the index is invalid
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    Song removeSong(int index);

    /**
     * Retrieves a copy of the current queue without modifying it.
     *
     * @return an immutable list of songs currently in the queue
     */
    List<Song> getQueue();

    /**
     * Removes all songs from the queue.
     */
    void clearQueue();

    /**
     * Gets the current size of the queue.
     *
     * @return the number of songs currently in the queue
     */
    int size();

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue contains no songs, false otherwise
     */
    boolean isEmpty();
}