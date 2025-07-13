package dev.cafe.core;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.metrics.MetricsBinder;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class AudioController_Factory implements Factory<AudioController> {
  private final Provider<AudioSearchService> searchServiceProvider;

  private final Provider<PlaybackStrategy> playbackStrategyProvider;

  private final Provider<MetricsBinder> metricsProvider;

  private final Provider<MostPlayedService> mostPlayedServiceProvider;

  private final Provider<TrackCacheService> trackCacheServiceProvider;

  public AudioController_Factory(Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider, Provider<MetricsBinder> metricsProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    this.searchServiceProvider = searchServiceProvider;
    this.playbackStrategyProvider = playbackStrategyProvider;
    this.metricsProvider = metricsProvider;
    this.mostPlayedServiceProvider = mostPlayedServiceProvider;
    this.trackCacheServiceProvider = trackCacheServiceProvider;
  }

  @Override
  public AudioController get() {
    return newInstance(searchServiceProvider.get(), playbackStrategyProvider.get(), metricsProvider.get(), mostPlayedServiceProvider.get(), trackCacheServiceProvider.get());
  }

  public static AudioController_Factory create(Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider, Provider<MetricsBinder> metricsProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    return new AudioController_Factory(searchServiceProvider, playbackStrategyProvider, metricsProvider, mostPlayedServiceProvider, trackCacheServiceProvider);
  }

  public static AudioController newInstance(AudioSearchService searchService,
      PlaybackStrategy playbackStrategy, MetricsBinder metrics, MostPlayedService mostPlayedService,
      TrackCacheService trackCacheService) {
    return new AudioController(searchService, playbackStrategy, metrics, mostPlayedService, trackCacheService);
  }
}
