package dev.cafe.cache.guild;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteDataSource;

class SqliteGuildSettingsRepositoryTest {

  private DataSource dataSource;
  private GuildSettingsRepository repository;
  private Connection connection;

  @BeforeEach
  void setUp() throws SQLException {
    SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
    sqliteDataSource.setUrl("jdbc:sqlite:file::memory:?cache=shared");
    dataSource = sqliteDataSource;
    connection = dataSource.getConnection();
    repository = new SqliteGuildSettingsRepository(dataSource);
    repository.createTable();
  }

  @AfterEach
  void tearDown() throws SQLException {
    if (connection != null) {
      connection.close();
    }
  }

  @Test
  void findById_whenNotExists_returnsEmpty() {
    Optional<GuildSettings> result = repository.findById(123L);
    assertTrue(result.isEmpty());
  }

  @Test
  void saveAndFindById_whenNew_insertsAndRetrieves() {
    GuildSettings settings = new GuildSettings(1L, 2L, 3L, 4L);
    repository.save(settings);

    Optional<GuildSettings> result = repository.findById(1L);
    assertTrue(result.isPresent());
    assertEquals(settings, result.get());
  }

  @Test
  void saveAndFindById_whenExists_updatesAndRetrieves() {
    GuildSettings originalSettings = new GuildSettings(1L, 2L, 3L, 4L);
    repository.save(originalSettings);

    GuildSettings updatedSettings = new GuildSettings(1L, 12L, 13L, 14L);
    repository.save(updatedSettings);

    Optional<GuildSettings> result = repository.findById(1L);
    assertTrue(result.isPresent());
    assertEquals(updatedSettings, result.get());
  }
}
