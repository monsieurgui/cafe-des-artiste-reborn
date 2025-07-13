package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.FileCachePlaybackStrategy;
import dev.cafe.cache.FileCachePlaybackStrategy_Factory;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.cache.dagger.CacheModule;
import dev.cafe.cache.dagger.CacheModule_ProvideDataSourceFactory;
import dev.cafe.cache.dagger.CacheModule_ProvideDatabaseUrlFactory;
import dev.cafe.cache.dagger.CacheModule_ProvideMostPlayedServiceFactory;
import dev.cafe.cache.dagger.CacheModule_ProvideTrackCacheServiceFactory;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.cache.guild.SqliteGuildSettingsRepository;
import dev.cafe.cache.guild.SqliteGuildSettingsRepository_Factory;
import dev.cafe.config.ConfigLoader;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import dev.cafe.metrics.MetricsBinder;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import javax.sql.DataSource;

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

    private Provider<PlaybackStrategy> provideStreamingPlaybackStrategyProvider;

    private Provider<MostPlayedService> provideMostPlayedServiceProvider;

    private Provider<TrackCacheService> provideTrackCacheServiceProvider;

    private Provider<FileCachePlaybackStrategy> fileCachePlaybackStrategyProvider;

    private Provider<MetricsBinder> provideMetricsBinderProvider;

    private Provider<AudioController> provideAudioControllerProvider;

    private Provider<PlaylistManager> providePlaylistManagerProvider;

    private Provider<CacheCommands> provideCacheCommandsProvider;

    private Provider<String> provideDatabaseUrlProvider;

    private Provider<DataSource> provideDataSourceProvider;

    private Provider<SqliteGuildSettingsRepository> sqliteGuildSettingsRepositoryProvider;

    private Provider<SetupCommands> setupCommandsProvider;

    private BotComponentImpl(BotModule botModuleParam, CacheModule cacheModuleParam) {

      initialize(botModuleParam, cacheModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final BotModule botModuleParam, final CacheModule cacheModuleParam) {
      this.provideConfigLoaderProvider = DoubleCheck.provider(BotModule_ProvideConfigLoaderFactory.create(botModuleParam));
      this.provideAudioSearchServiceProvider = DoubleCheck.provider(BotModule_ProvideAudioSearchServiceFactory.create(botModuleParam, provideConfigLoaderProvider));
      this.provideStreamingPlaybackStrategyProvider = DoubleCheck.provider(BotModule_ProvideStreamingPlaybackStrategyFactory.create(botModuleParam, provideConfigLoaderProvider, provideAudioSearchServiceProvider));
      this.provideMostPlayedServiceProvider = DoubleCheck.provider(CacheModule_ProvideMostPlayedServiceFactory.create(cacheModuleParam));
      this.provideTrackCacheServiceProvider = DoubleCheck.provider(CacheModule_ProvideTrackCacheServiceFactory.create(cacheModuleParam));
      this.fileCachePlaybackStrategyProvider = DoubleCheck.provider(FileCachePlaybackStrategy_Factory.create(provideStreamingPlaybackStrategyProvider, provideMostPlayedServiceProvider, provideTrackCacheServiceProvider));
      this.provideMetricsBinderProvider = DoubleCheck.provider(BotModule_ProvideMetricsBinderFactory.create(botModuleParam));
      this.provideAudioControllerProvider = DoubleCheck.provider(BotModule_ProvideAudioControllerFactory.create(botModuleParam, provideAudioSearchServiceProvider, ((Provider) fileCachePlaybackStrategyProvider), provideMetricsBinderProvider, provideMostPlayedServiceProvider, provideTrackCacheServiceProvider));
      this.providePlaylistManagerProvider = DoubleCheck.provider(BotModule_ProvidePlaylistManagerFactory.create(botModuleParam));
      this.provideCacheCommandsProvider = DoubleCheck.provider(BotModule_ProvideCacheCommandsFactory.create(botModuleParam, provideMostPlayedServiceProvider, provideTrackCacheServiceProvider));
      this.provideDatabaseUrlProvider = DoubleCheck.provider(CacheModule_ProvideDatabaseUrlFactory.create(cacheModuleParam, provideConfigLoaderProvider));
      this.provideDataSourceProvider = DoubleCheck.provider(CacheModule_ProvideDataSourceFactory.create(cacheModuleParam, provideDatabaseUrlProvider));
      this.sqliteGuildSettingsRepositoryProvider = DoubleCheck.provider(SqliteGuildSettingsRepository_Factory.create(provideDataSourceProvider));
      this.setupCommandsProvider = DoubleCheck.provider(SetupCommands_Factory.create(((Provider) sqliteGuildSettingsRepositoryProvider)));
    }

    @Override
    public AudioController audioController() {
      return provideAudioControllerProvider.get();
    }

    @Override
    public PlaybackStrategy playbackStrategy() {
      return fileCachePlaybackStrategyProvider.get();
    }

    @Override
    public PlaylistManager playlistManager() {
      return providePlaylistManagerProvider.get();
    }

    @Override
    public CacheCommands cacheCommands() {
      return provideCacheCommandsProvider.get();
    }

    @Override
    public GuildSettingsRepository guildSettingsRepository() {
      return sqliteGuildSettingsRepositoryProvider.get();
    }

    @Override
    public SetupCommands setupCommands() {
      return setupCommandsProvider.get();
    }
  }
}
