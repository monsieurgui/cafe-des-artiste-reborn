package dev.cafe.cache.dagger;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.cache.TrackCacheService;
import javax.annotation.processing.Generated;

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
public final class CacheModule_ProvideTrackCacheServiceFactory implements Factory<TrackCacheService> {
  private final CacheModule module;

  public CacheModule_ProvideTrackCacheServiceFactory(CacheModule module) {
    this.module = module;
  }

  @Override
  public TrackCacheService get() {
    return provideTrackCacheService(module);
  }

  public static CacheModule_ProvideTrackCacheServiceFactory create(CacheModule module) {
    return new CacheModule_ProvideTrackCacheServiceFactory(module);
  }

  public static TrackCacheService provideTrackCacheService(CacheModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideTrackCacheService());
  }
}
