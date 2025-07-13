package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.cache.dagger.CacheModule;
import dev.cafe.cache.dagger.CacheModule_ProvideMostPlayedServiceFactory;
import dev.cafe.cache.dagger.CacheModule_ProvideTrackCacheServiceFactory;
import dev.cafe.config.ConfigLoader;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import dev.cafe.metrics.MetricsBinder;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DaggerBotComponent {
  private DaggerBotComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static BotComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private BotModule botModule;

    private CacheModule cacheModule;

    private Builder() {
    }

    public Builder botModule(BotModule botModule) {
      this.botModule = Preconditions.checkNotNull(botModule);
      return this;
    }

    public Builder cacheModule(CacheModule cacheModule) {
      this.cacheModule = Preconditions.checkNotNull(cacheModule);
      return this;
    }

    public BotComponent build() {
      if (botModule == null) {
        this.botModule = new BotModule();
      }
      if (cacheModule == null) {
        this.cacheModule = new CacheModule();
      }
      return new BotComponentImpl(botModule, cacheModule);
    }
  }

  private static final class BotComponentImpl implements BotComponent {
    private final BotComponentImpl botComponentImpl = this;

    private Provider<ConfigLoader> provideConfigLoaderProvider;

    private Provider<AudioSearchService> provideAudioSearchServiceProvider;

    private Provider<PlaybackStrategy> providePlaybackStrategyProvider;

    private Provider<MetricsBinder> provideMetricsBinderProvider;

    private Provider<MostPlayedService> provideMostPlayedServiceProvider;

    private Provider<TrackCacheService> provideTrackCacheServiceProvider;

    private Provider<AudioController> provideAudioControllerProvider;

    private Provider<PlaylistManager> providePlaylistManagerProvider;

    private BotComponentImpl(BotModule botModuleParam, CacheModule cacheModuleParam) {

      initialize(botModuleParam, cacheModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final BotModule botModuleParam, final CacheModule cacheModuleParam) {
      this.provideConfigLoaderProvider = DoubleCheck.provider(BotModule_ProvideConfigLoaderFactory.create(botModuleParam));
      this.provideAudioSearchServiceProvider = DoubleCheck.provider(BotModule_ProvideAudioSearchServiceFactory.create(botModuleParam, provideConfigLoaderProvider));
      this.providePlaybackStrategyProvider = DoubleCheck.provider(BotModule_ProvidePlaybackStrategyFactory.create(botModuleParam, provideConfigLoaderProvider, provideAudioSearchServiceProvider));
      this.provideMetricsBinderProvider = DoubleCheck.provider(BotModule_ProvideMetricsBinderFactory.create(botModuleParam));
      this.provideMostPlayedServiceProvider = DoubleCheck.provider(CacheModule_ProvideMostPlayedServiceFactory.create(cacheModuleParam));
      this.provideTrackCacheServiceProvider = DoubleCheck.provider(CacheModule_ProvideTrackCacheServiceFactory.create(cacheModuleParam));
      this.provideAudioControllerProvider = DoubleCheck.provider(BotModule_ProvideAudioControllerFactory.create(botModuleParam, provideAudioSearchServiceProvider, providePlaybackStrategyProvider, provideMetricsBinderProvider, provideMostPlayedServiceProvider, provideTrackCacheServiceProvider));
      this.providePlaylistManagerProvider = DoubleCheck.provider(BotModule_ProvidePlaylistManagerFactory.create(botModuleParam));
    }

    @Override
    public AudioController audioController() {
      return provideAudioControllerProvider.get();
    }

    @Override
    public PlaybackStrategy playbackStrategy() {
      return providePlaybackStrategyProvider.get();
    }

    @Override
    public PlaylistManager playlistManager() {
      return providePlaylistManagerProvider.get();
    }
  }
}
