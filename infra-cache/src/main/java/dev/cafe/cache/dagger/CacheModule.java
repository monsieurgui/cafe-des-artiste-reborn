package dev.cafe.cache.dagger;

import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.config.ConfigLoader;
import java.sql.SQLException;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

@Module
public class CacheModule {
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheModule.class);

  @Provides
  @Singleton
  @Named("databaseUrl")
  String provideDatabaseUrl(ConfigLoader configLoader) {
    Config config = configLoader.getConfig();
    if (config.hasPath("database.path")) {
      return "jdbc:sqlite:" + config.getString("database.path");
    }
    // Default to a file in the current working directory
    return "jdbc:sqlite:cafe-bot.db";
  }

  @Provides
  @Singleton
  DataSource provideDataSource(@Named("databaseUrl") String databaseUrl) {
    LOGGER.info("Initializing database at {}", databaseUrl);
    SQLiteDataSource dataSource = new SQLiteDataSource();
    dataSource.setUrl(databaseUrl);
    return dataSource;
  }

  @Provides
  @Singleton
  MostPlayedService provideMostPlayedService() {
    // This service seems to use its own persistence, leaving as is.
    try {
      return new MostPlayedService("cache.db");
    } catch (SQLException e) {
      LOGGER.error("Failed to initialize MostPlayedService", e);
      // Allow the app to start, but caching will not work.
      // A more robust solution might be to prevent startup.
      return null;
    }
  }

  @Provides
  @Singleton
  TrackCacheService provideTrackCacheService() {
    return new TrackCacheService();
  }
}
