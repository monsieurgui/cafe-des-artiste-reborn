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
              + "count INTEGER NOT NULL DEFAULT 0"
              + ")");
    }
  }

  /**
   * Increments the play count for a given video ID.
   *
   * @param videoId the ID of the video.
   * @throws SQLException if a database access error occurs.
   */
  public void incrementPlayCount(String videoId) throws SQLException {
    String sql =
        "INSERT INTO play_counts (videoId, count) VALUES (?, 1) "
            + "ON CONFLICT(videoId) DO UPDATE SET count = count + 1";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, videoId);
      pstmt.executeUpdate();
    }
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

  @Override
  public void close() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
