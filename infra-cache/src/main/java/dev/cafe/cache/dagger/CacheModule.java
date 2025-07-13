package dev.cafe.cache.dagger;

import dagger.Module;
import dagger.Provides;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import java.sql.SQLException;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Module
public class CacheModule {
  private static final Logger logger = LoggerFactory.getLogger(CacheModule.class);
  private static final String DATABASE_PATH = "cache.db";

  @Provides
  @Singleton
  MostPlayedService provideMostPlayedService() {
    try {
      return new MostPlayedService(DATABASE_PATH);
    } catch (SQLException e) {
      logger.error("Failed to initialize MostPlayedService", e);
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
