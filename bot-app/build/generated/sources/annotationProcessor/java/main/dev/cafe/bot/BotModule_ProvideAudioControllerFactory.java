package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.core.AudioController;
import dev.cafe.core.QueueChangeListener;
import dev.cafe.metrics.MetricsBinder;
import java.util.Optional;
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
public final class BotModule_ProvideAudioControllerFactory implements Factory<AudioController> {
  private final BotModule module;

  private final Provider<AudioSearchService> searchServiceProvider;

  private final Provider<PlaybackStrategy> playbackStrategyProvider;

  private final Provider<MetricsBinder> metricsProvider;

  private final Provider<MostPlayedService> mostPlayedServiceProvider;

  private final Provider<TrackCacheService> trackCacheServiceProvider;

  private final Provider<Optional<QueueChangeListener>> queueChangeListenerProvider;

  public BotModule_ProvideAudioControllerFactory(BotModule module,
      Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider, Provider<MetricsBinder> metricsProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider,
      Provider<Optional<QueueChangeListener>> queueChangeListenerProvider) {
    this.module = module;
    this.searchServiceProvider = searchServiceProvider;
    this.playbackStrategyProvider = playbackStrategyProvider;
    this.metricsProvider = metricsProvider;
    this.mostPlayedServiceProvider = mostPlayedServiceProvider;
    this.trackCacheServiceProvider = trackCacheServiceProvider;
    this.queueChangeListenerProvider = queueChangeListenerProvider;
  }

  @Override
  public AudioController get() {
    return provideAudioController(module, searchServiceProvider.get(), playbackStrategyProvider.get(), metricsProvider.get(), mostPlayedServiceProvider.get(), trackCacheServiceProvider.get(), queueChangeListenerProvider.get());
  }

  public static BotModule_ProvideAudioControllerFactory create(BotModule module,
      Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider, Provider<MetricsBinder> metricsProvider,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider,
      Provider<Optional<QueueChangeListener>> queueChangeListenerProvider) {
    return new BotModule_ProvideAudioControllerFactory(module, searchServiceProvider, playbackStrategyProvider, metricsProvider, mostPlayedServiceProvider, trackCacheServiceProvider, queueChangeListenerProvider);
  }

  public static AudioController provideAudioController(BotModule instance,
      AudioSearchService searchService, PlaybackStrategy playbackStrategy, MetricsBinder metrics,
      MostPlayedService mostPlayedService, TrackCacheService trackCacheService,
      Optional<QueueChangeListener> queueChangeListener) {
    return Preconditions.checkNotNullFromProvides(instance.provideAudioController(searchService, playbackStrategy, metrics, mostPlayedService, trackCacheService, queueChangeListener));
  }
}
