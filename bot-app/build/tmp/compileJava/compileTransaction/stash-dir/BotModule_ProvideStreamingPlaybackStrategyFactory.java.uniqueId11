package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.config.ConfigLoader;
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
public final class BotModule_ProvideStreamingPlaybackStrategyFactory implements Factory<PlaybackStrategy> {
  private final BotModule module;

  private final Provider<ConfigLoader> configProvider;

  private final Provider<AudioSearchService> searchServiceProvider;

  public BotModule_ProvideStreamingPlaybackStrategyFactory(BotModule module,
      Provider<ConfigLoader> configProvider, Provider<AudioSearchService> searchServiceProvider) {
    this.module = module;
    this.configProvider = configProvider;
    this.searchServiceProvider = searchServiceProvider;
  }

  @Override
  public PlaybackStrategy get() {
    return provideStreamingPlaybackStrategy(module, configProvider.get(), searchServiceProvider.get());
  }

  public static BotModule_ProvideStreamingPlaybackStrategyFactory create(BotModule module,
      Provider<ConfigLoader> configProvider, Provider<AudioSearchService> searchServiceProvider) {
    return new BotModule_ProvideStreamingPlaybackStrategyFactory(module, configProvider, searchServiceProvider);
  }

  public static PlaybackStrategy provideStreamingPlaybackStrategy(BotModule instance,
      ConfigLoader config, AudioSearchService searchService) {
    return Preconditions.checkNotNullFromProvides(instance.provideStreamingPlaybackStrategy(config, searchService));
  }
}
