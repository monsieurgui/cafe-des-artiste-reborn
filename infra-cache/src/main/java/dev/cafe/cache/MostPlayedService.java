package dev.cafe.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Service to track the play count of songs. */
public class MostPlayedService implements AutoCloseable {

  private final Connection connection;

  /**
   * Creates a new MostPlayedService.
   *
   * @param databasePath the path to the SQLite database file.
   * @throws SQLException if a database access error occurs.
   */
  public MostPlayedService(String databasePath) throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    initializeDatabase();
  }

  private void initializeDatabase() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(
          "CREATE TABLE IF NOT EXISTS play_counts ("
              + "videoId TEXT PRIMARY KEY, "
              + "count INTEGER NOT NULL DEFAULT 0, "
              + "cached BOOLEAN NOT NULL DEFAULT FALSE"
              + ")");
    }
    // This is a simple way to handle schema migration. For a real application, a more robust
    // migration library would be better.
    try (Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("PRAGMA table_info(play_counts)")) {
      boolean cachedColumnExists = false;
      while (rs.next()) {
        if ("cached".equalsIgnoreCase(rs.getString("name"))) {
          cachedColumnExists = true;
          break;
        }
      }
      if (!cachedColumnExists) {
        try (Statement alterStatement = connection.createStatement()) {
          alterStatement.execute(
              "ALTER TABLE play_counts ADD COLUMN cached BOOLEAN NOT NULL DEFAULT FALSE");
        }
      }
    }
  }

  /**
   * Increments the play count for a given video ID.
   *
   * @param videoId the ID of the video.
   * @return the new play count.
   * @throws SQLException if a database access error occurs.
   */
  public int incrementPlayCount(String videoId) throws SQLException {
    String sql =
        "INSERT INTO play_counts (videoId, count) VALUES (?, 1) "
            + "ON CONFLICT(videoId) DO UPDATE SET count = count + 1 "
            + "RETURNING count";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, videoId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    }
    // This should not be reached in normal operation
    return getPlayCount(videoId);
  }

  /**
   * Gets the play count for a given video ID.
   *
   * @param videoId the ID of the video.
   * @return the play count, or 0 if the video ID is not found.
   * @throws SQLException if a database access error occurs.
   */
  public int getPlayCount(String videoId) throws SQLException {
    String sql = "SELECT count FROM play_counts WHERE videoId = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, videoId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("count");
      }
    }
    return 0;
  }

  /**
   * Checks if a track is marked as cached.
   *
   * @param videoId the ID of the video.
   * @return true if the track is cached, false otherwise.
   * @throws SQLException if a database access error occurs.
   */
  public boolean isCached(String videoId) throws SQLException {
    String sql = "SELECT cached FROM play_counts WHERE videoId = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, videoId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getBoolean("cached");
      }
    }
    return false;
  }

  /**
   * Marks a track as cached.
   *
   * @param videoId the ID of the video.
   * @throws SQLException if a database access error occurs.
   */
  public void setCached(String videoId) throws SQLException {
    setCached(videoId, true);
  }

  /**
   * Sets the cached status of a track.
   *
   * @param videoId the ID of the video.
   * @param cached the cached status.
   * @throws SQLException if a database access error occurs.
   */
  public void setCached(String videoId, boolean cached) throws SQLException {
    String sql = "UPDATE play_counts SET cached = ? WHERE videoId = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setBoolean(1, cached);
      pstmt.setString(2, videoId);
      pstmt.executeUpdate();
    }
  }

  @Override
  public void close() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
