package dev.cafe.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MostPlayedServiceTest {

  private MostPlayedService mostPlayedService;

  @BeforeEach
  void setUp() throws SQLException {
    // Use in-memory SQLite database for testing
    mostPlayedService = new MostPlayedService(":memory:");
  }

  @AfterEach
  void tearDown() throws SQLException {
    mostPlayedService.close();
  }

  @Test
  void getPlayCount_forNewVideo_returnsZero() throws SQLException {
    assertEquals(0, mostPlayedService.getPlayCount("new-video-id"));
  }

  @Test
  void incrementPlayCount_forNewVideo_returnsOne() throws SQLException {
    assertEquals(1, mostPlayedService.incrementPlayCount("video1"));
    assertEquals(1, mostPlayedService.getPlayCount("video1"));
  }

  @Test
  void incrementPlayCount_forExistingVideo_incrementsCount() throws SQLException {
    mostPlayedService.incrementPlayCount("video1");
    assertEquals(2, mostPlayedService.incrementPlayCount("video1"));
    assertEquals(2, mostPlayedService.getPlayCount("video1"));
  }

  @Test
  void getPlayCount_withMultipleVideos() throws SQLException {
    mostPlayedService.incrementPlayCount("video1");
    mostPlayedService.incrementPlayCount("video2");
    mostPlayedService.incrementPlayCount("video1");

    assertEquals(2, mostPlayedService.getPlayCount("video1"));
    assertEquals(1, mostPlayedService.getPlayCount("video2"));
    assertEquals(0, mostPlayedService.getPlayCount("video3"));
  }

  @Test
  void isCached_forNewVideo_isFalse() throws SQLException {
    mostPlayedService.incrementPlayCount("video1");
    assertFalse(mostPlayedService.isCached("video1"));
  }

  @Test
  void setCached_setsFlagToTrue() throws SQLException {
    mostPlayedService.incrementPlayCount("video1");
    assertFalse(mostPlayedService.isCached("video1")); // Sanity check
    mostPlayedService.setCached("video1");
    assertTrue(mostPlayedService.isCached("video1"));
  }

  @Test
  void serviceClosed_throwsException() throws SQLException {
    mostPlayedService.close();
    assertThrows(SQLException.class, () -> mostPlayedService.getPlayCount("any-id"));
  }
}
