package dev.cafe.cache.guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** SQLite implementation for managing guild-specific settings. */
@Singleton
public class SqliteGuildSettingsRepository implements GuildSettingsRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqliteGuildSettingsRepository.class);

  private final DataSource dataSource;

  @Inject
  public SqliteGuildSettingsRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Optional<GuildSettings> findById(long guildId) {
    final String sql =
        "SELECT channelId, queueMsgId, nowPlayingMsgId FROM guild_settings WHERE guildId = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, guildId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(
              new GuildSettings(
                  guildId,
                  rs.getLong("channelId"),
                  rs.getLong("queueMsgId"),
                  rs.getLong("nowPlayingMsgId")));
        }
      }
    } catch (SQLException e) {
      LOGGER.error("Failed to find guild settings for guildId: {}", guildId, e);
    }
    return Optional.empty();
  }

  @Override
  public void save(GuildSettings settings) {
    final String sql =
        "INSERT INTO guild_settings (guildId, channelId, queueMsgId, nowPlayingMsgId) VALUES (?, ?, ?, ?) "
            + "ON CONFLICT(guildId) DO UPDATE SET "
            + "channelId = excluded.channelId, "
            + "queueMsgId = excluded.queueMsgId, "
            + "nowPlayingMsgId = excluded.nowPlayingMsgId";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, settings.guildId());
      pstmt.setLong(2, settings.channelId());
      pstmt.setLong(3, settings.queueMsgId());
      pstmt.setLong(4, settings.nowPlayingMsgId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.error("Failed to save guild settings for guildId: {}", settings.guildId(), e);
    }
  }

  @Override
  public void createTable() {
    final String sql =
        "CREATE TABLE IF NOT EXISTS guild_settings ("
            + "guildId INTEGER PRIMARY KEY,"
            + "channelId INTEGER NOT NULL,"
            + "queueMsgId INTEGER NOT NULL,"
            + "nowPlayingMsgId INTEGER NOT NULL"
            + ");";
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      LOGGER.error("Failed to create guild_settings table", e);
      throw new RuntimeException("Failed to create guild_settings table", e);
    }
  }
}
