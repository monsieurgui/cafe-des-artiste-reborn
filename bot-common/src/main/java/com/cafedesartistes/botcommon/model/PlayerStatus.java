package com.cafedesartistes.botcommon.model;

/**
 * Enumeration representing the various states of the music player.
 * Used to track and communicate the current playback status across services.
 */
public enum PlayerStatus {
    /**
     * The player is currently playing a track.
     */
    PLAYING("Playing"),

    /**
     * The player is paused and can be resumed.
     */
    PAUSED("Paused"),

    /**
     * The player is stopped and no track is loaded.
     */
    STOPPED("Stopped");

    private final String displayName;

    PlayerStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the human-readable display name for this status.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}