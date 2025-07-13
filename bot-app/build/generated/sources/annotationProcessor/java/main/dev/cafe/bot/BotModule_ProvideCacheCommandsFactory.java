package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
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
public final class BotModule_ProvideCacheCommandsFactory implements Factory<CacheCommands> {
  private final BotModule module;

  private final Provider<MostPlayedService> mostPlayedServiceProvider;

  private final Provider<TrackCacheService> trackCacheServiceProvider;

  public BotModule_ProvideCacheCommandsFactory(BotModule module,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    this.module = module;
    this.mostPlayedServiceProvider = mostPlayedServiceProvider;
    this.trackCacheServiceProvider = trackCacheServiceProvider;
  }

  @Override
  public CacheCommands get() {
    return provideCacheCommands(module, mostPlayedServiceProvider.get(), trackCacheServiceProvider.get());
  }

  public static BotModule_ProvideCacheCommandsFactory create(BotModule module,
      Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    return new BotModule_ProvideCacheCommandsFactory(module, mostPlayedServiceProvider, trackCacheServiceProvider);
  }

  public static CacheCommands provideCacheCommands(BotModule instance,
      MostPlayedService mostPlayedService, TrackCacheService trackCacheService) {
    return Preconditions.checkNotNullFromProvides(instance.provideCacheCommands(mostPlayedService, trackCacheService));
  }
}
