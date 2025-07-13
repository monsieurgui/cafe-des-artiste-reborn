package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
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

    private Builder() {
    }

    public Builder botModule(BotModule botModule) {
      this.botModule = Preconditions.checkNotNull(botModule);
      return this;
    }

    public BotComponent build() {
      if (botModule == null) {
        this.botModule = new BotModule();
      }
      return new BotComponentImpl(botModule);
    }
  }

  private static final class BotComponentImpl implements BotComponent {
    private final BotComponentImpl botComponentImpl = this;

    private Provider<ConfigLoader> provideConfigLoaderProvider;

    private Provider<AudioSearchService> provideAudioSearchServiceProvider;

    private Provider<PlaybackStrategy> providePlaybackStrategyProvider;

    private Provider<MetricsBinder> provideMetricsBinderProvider;

    private Provider<AudioController> provideAudioControllerProvider;

    private Provider<PlaylistManager> providePlaylistManagerProvider;

    private BotComponentImpl(BotModule botModuleParam) {

      initialize(botModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final BotModule botModuleParam) {
      this.provideConfigLoaderProvider = DoubleCheck.provider(BotModule_ProvideConfigLoaderFactory.create(botModuleParam));
      this.provideAudioSearchServiceProvider = DoubleCheck.provider(BotModule_ProvideAudioSearchServiceFactory.create(botModuleParam, provideConfigLoaderProvider));
      this.providePlaybackStrategyProvider = DoubleCheck.provider(BotModule_ProvidePlaybackStrategyFactory.create(botModuleParam, provideConfigLoaderProvider, provideAudioSearchServiceProvider));
      this.provideMetricsBinderProvider = DoubleCheck.provider(BotModule_ProvideMetricsBinderFactory.create(botModuleParam));
      this.provideAudioControllerProvider = DoubleCheck.provider(BotModule_ProvideAudioControllerFactory.create(botModuleParam, provideAudioSearchServiceProvider, providePlaybackStrategyProvider, provideMetricsBinderProvider));
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
