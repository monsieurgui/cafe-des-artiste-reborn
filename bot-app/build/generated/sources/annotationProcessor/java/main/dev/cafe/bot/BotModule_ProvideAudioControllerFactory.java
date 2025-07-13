package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.core.AudioController;
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
public final class BotModule_ProvideAudioControllerFactory implements Factory<AudioController> {
  private final BotModule module;

  private final Provider<AudioSearchService> searchServiceProvider;

  private final Provider<PlaybackStrategy> playbackStrategyProvider;

  private final Provider<MetricsBinder> metricsProvider;

  public BotModule_ProvideAudioControllerFactory(BotModule module,
      Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider,
      Provider<MetricsBinder> metricsProvider) {
    this.module = module;
    this.searchServiceProvider = searchServiceProvider;
    this.playbackStrategyProvider = playbackStrategyProvider;
    this.metricsProvider = metricsProvider;
  }

  @Override
  public AudioController get() {
    return provideAudioController(module, searchServiceProvider.get(), playbackStrategyProvider.get(), metricsProvider.get());
  }

  public static BotModule_ProvideAudioControllerFactory create(BotModule module,
      Provider<AudioSearchService> searchServiceProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider,
      Provider<MetricsBinder> metricsProvider) {
    return new BotModule_ProvideAudioControllerFactory(module, searchServiceProvider, playbackStrategyProvider, metricsProvider);
  }

  public static AudioController provideAudioController(BotModule instance,
      AudioSearchService searchService, PlaybackStrategy playbackStrategy, MetricsBinder metrics) {
    return Preconditions.checkNotNullFromProvides(instance.provideAudioController(searchService, playbackStrategy, metrics));
  }
}
