package com.cafedesartistes.botcommon.model;

import java.time.Duration;
import java.util.Objects;

/**
 * Plain Old Java Object representing a song in the music bot system.
 * Contains all necessary metadata for song identification, display, and management.
 */
public class Song {
    private final String title;
    private final String url;
    private final String thumbnailUrl;
    private final Duration duration;
    private final String requesterId;
    private final String guildId;

    /**
     * Constructs a new Song instance.
     *
     * @param title the song title
     * @param url the song URL
     * @param thumbnailUrl the song thumbnail URL
     * @param duration the song duration
     * @param requesterId the Discord user ID who requested the song
     * @param guildId the Discord guild ID where the song was requested
     */
    public Song(String title, String url, String thumbnailUrl, Duration duration, String requesterId, String guildId) {
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.requesterId = requesterId;
        this.guildId = guildId;
    }

    /**
     * Gets the song title.
     *
     * @return the song title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the song URL.
     *
     * @return the song URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the song thumbnail URL.
     *
     * @return the song thumbnail URL
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Gets the song duration.
     *
     * @return the song duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Gets the ID of the user who requested the song.
     *
     * @return the requester Discord user ID
     */
    public String getRequesterId() {
        return requesterId;
    }

    /**
     * Gets the ID of the guild where the song was requested.
     *
     * @return the Discord guild ID
     */
    public String getGuildId() {
        return guildId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) &&
               Objects.equals(url, song.url) &&
               Objects.equals(thumbnailUrl, song.thumbnailUrl) &&
               Objects.equals(duration, song.duration) &&
               Objects.equals(requesterId, song.requesterId) &&
               Objects.equals(guildId, song.guildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, thumbnailUrl, duration, requesterId, guildId);
    }

    @Override
    public String toString() {
        return "Song{" +
               "title='" + title + '\'' +
               ", url='" + url + '\'' +
               ", thumbnailUrl='" + thumbnailUrl + '\'' +
               ", duration=" + duration +
               ", requesterId='" + requesterId + '\'' +
               ", guildId='" + guildId + '\'' +
               '}';
    }

    /**
     * Builder for creating Song instances.
     */
    public static class Builder {
        private String title;
        private String url;
        private String thumbnailUrl;
        private Duration duration;
        private String requesterId;
        private String guildId;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder requesterId(String requesterId) {
            this.requesterId = requesterId;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public Song build() {
            return new Song(title, url, thumbnailUrl, duration, requesterId, guildId);
        }
    }
}