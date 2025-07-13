package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CacheCommands_Factory implements Factory<CacheCommands> {
  private final Provider<MostPlayedService> mostPlayedServiceProvider;

  private final Provider<TrackCacheService> trackCacheServiceProvider;

  public CacheCommands_Factory(Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    this.mostPlayedServiceProvider = mostPlayedServiceProvider;
    this.trackCacheServiceProvider = trackCacheServiceProvider;
  }

  @Override
  public CacheCommands get() {
    return newInstance(mostPlayedServiceProvider.get(), trackCacheServiceProvider.get());
  }

  public static CacheCommands_Factory create(Provider<MostPlayedService> mostPlayedServiceProvider,
      Provider<TrackCacheService> trackCacheServiceProvider) {
    return new CacheCommands_Factory(mostPlayedServiceProvider, trackCacheServiceProvider);
  }

  public static CacheCommands newInstance(MostPlayedService mostPlayedService,
      TrackCacheService trackCacheService) {
    return new CacheCommands(mostPlayedService, trackCacheService);
  }
}
