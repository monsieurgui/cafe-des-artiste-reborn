package com.cafedesartistes.botcommon.model;

import org.junit.Test;
import java.time.Duration;
import static org.junit.Assert.*;

/**
 * Unit tests for the Song POJO.
 */
public class SongTest {

    @Test
    public void testSongConstructor() {
        Duration duration = Duration.ofMinutes(3).plusSeconds(45);
        Song song = new Song("Test Song", "https://example.com/song", "https://example.com/thumb.jpg", 
                            duration, "user123", "guild456");

        assertEquals("Test Song", song.getTitle());
        assertEquals("https://example.com/song", song.getUrl());
        assertEquals("https://example.com/thumb.jpg", song.getThumbnailUrl());
        assertEquals(duration, song.getDuration());
        assertEquals("user123", song.getRequesterId());
        assertEquals("guild456", song.getGuildId());
    }

    @Test
    public void testSongBuilder() {
        Duration duration = Duration.ofMinutes(4).plusSeconds(20);
        Song song = new Song.Builder()
                .title("Builder Song")
                .url("https://example.com/builder")
                .thumbnailUrl("https://example.com/builder-thumb.jpg")
                .duration(duration)
                .requesterId("user789")
                .guildId("guild101")
                .build();

        assertEquals("Builder Song", song.getTitle());
        assertEquals("https://example.com/builder", song.getUrl());
        assertEquals("https://example.com/builder-thumb.jpg", song.getThumbnailUrl());
        assertEquals(duration, song.getDuration());
        assertEquals("user789", song.getRequesterId());
        assertEquals("guild101", song.getGuildId());
    }

    @Test
    public void testSongEquality() {
        Duration duration = Duration.ofMinutes(2).plusSeconds(30);
        Song song1 = new Song("Equal Song", "https://example.com/equal", "https://example.com/equal-thumb.jpg",
                             duration, "user456", "guild789");
        Song song2 = new Song("Equal Song", "https://example.com/equal", "https://example.com/equal-thumb.jpg",
                             duration, "user456", "guild789");
        Song song3 = new Song("Different Song", "https://example.com/equal", "https://example.com/equal-thumb.jpg",
                             duration, "user456", "guild789");

        assertEquals(song1, song2);
        assertNotEquals(song1, song3);
        assertEquals(song1.hashCode(), song2.hashCode());
    }

    @Test
    public void testSongToString() {
        Duration duration = Duration.ofMinutes(1).plusSeconds(15);
        Song song = new Song("ToString Song", "https://example.com/tostring", "https://example.com/tostring-thumb.jpg",
                            duration, "user999", "guild888");

        String result = song.toString();
        assertTrue(result.contains("ToString Song"));
        assertTrue(result.contains("https://example.com/tostring"));
        assertTrue(result.contains("user999"));
        assertTrue(result.contains("guild888"));
    }

    @Test
    public void testBuilderChaining() {
        Duration duration = Duration.ofSeconds(180);
        Song song = new Song.Builder()
                .title("Chain Song")
                .url("https://example.com/chain")
                .thumbnailUrl("https://example.com/chain-thumb.jpg")
                .duration(duration)
                .requesterId("chain-user")
                .guildId("chain-guild")
                .build();

        assertNotNull(song);
        assertEquals("Chain Song", song.getTitle());
    }

    @Test
    public void testNullValues() {
        Song song = new Song(null, null, null, null, null, null);
        
        assertNull(song.getTitle());
        assertNull(song.getUrl());
        assertNull(song.getThumbnailUrl());
        assertNull(song.getDuration());
        assertNull(song.getRequesterId());
        assertNull(song.getGuildId());
    }
}