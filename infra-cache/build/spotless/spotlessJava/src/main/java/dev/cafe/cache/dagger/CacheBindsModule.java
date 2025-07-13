package dev.cafe.cache.dagger;

import dagger.Binds;
import dagger.Module;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.FileCachePlaybackStrategy;

@Module
public abstract class CacheBindsModule {

  @Binds
  abstract PlaybackStrategy bindPlaybackStrategy(FileCachePlaybackStrategy strategy);
}
