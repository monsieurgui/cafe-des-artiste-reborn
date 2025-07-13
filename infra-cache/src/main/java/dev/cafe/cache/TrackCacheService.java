package dev.cafe.cache;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for caching tracks. */
@Singleton
public class TrackCacheService {

  private static final Logger logger = LoggerFactory.getLogger(TrackCacheService.class);

  @Inject
  public TrackCacheService() {}

  /**
   * Caches a track.
   *
   * @param videoId the ID of the video to cache.
   */
  public void cacheTrack(String videoId) {
    logger.info("Caching track: {}", videoId);
    // Download logic will be implemented here in a future step.
  }
}
