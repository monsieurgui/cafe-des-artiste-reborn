package dev.cafe.cache;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.PlaybackStrategy;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dev.cafe.cache.dagger.Streaming")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class FileCachePlaybackStrategy_Factory implements Factory<FileCachePlaybackStrategy> {
  private final Provider<PlaybackStrategy> streamingStrategyProvider;

  private final Provider<MostPlayedService> mostPlayedServiceProvider;

  private final Provider<TrackCacheService> trackCacheServiceProvider;

  public FileCachePlaybackStrategy_Factory(Provider<PlaybackStrategy> streamingStrategyProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    this.streamingStrategyProvider = streamingStrategyProvider;
    this.mostPlayedServiceProvider = mostPlayedServiceProvider;
    this.trackCacheServiceProvider = trackCacheServiceProvider;
  }

  @Override
  public FileCachePlaybackStrategy get() {
    return newInstance(streamingStrategyProvider.get(), mostPlayedServiceProvider.get(), trackCacheServiceProvider.get());
  }

  public static FileCachePlaybackStrategy_Factory create(
      Provider<PlaybackStrategy> streamingStrategyProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    return new FileCachePlaybackStrategy_Factory(streamingStrategyProvider, mostPlayedServiceProvider, trackCacheServiceProvider);
  }

  public static FileCachePlaybackStrategy newInstance(PlaybackStrategy streamingStrategy,
      MostPlayedService mostPlayedService, TrackCacheService trackCacheService) {
    return new FileCachePlaybackStrategy(streamingStrategy, mostPlayedService, trackCacheService);
  }
}
