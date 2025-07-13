package dev.cafe.cache;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for caching tracks. */
@Singleton
public class TrackCacheService {

  private static final Logger logger = LoggerFactory.getLogger(TrackCacheService.class);
  private static final File CACHE_DIR = new File("cache");

  @Inject
  public TrackCacheService() {
    if (!CACHE_DIR.exists()) {
      CACHE_DIR.mkdirs();
    }
  }

  /**
   * Caches a track.
   *
   * @param videoId the ID of the video to cache.
   */
  public void cacheTrack(String videoId) {
    logger.info("Caching track: {}", videoId);
    // Download logic will be implemented here in a future step.
  }

  /**
   * Gets the cache file for a given video ID.
   *
   * @param videoId the ID of the video.
   * @return the cache file.
   */
  public File getCacheFile(String videoId) {
    return new File(CACHE_DIR, videoId + ".opus");
  }
}
