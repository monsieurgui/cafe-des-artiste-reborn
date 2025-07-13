package dev.cafe.cache.dagger;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.cache.MostPlayedService;
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
public final class CacheModule_ProvideMostPlayedServiceFactory implements Factory<MostPlayedService> {
  private final CacheModule module;

  public CacheModule_ProvideMostPlayedServiceFactory(CacheModule module) {
    this.module = module;
  }

  @Override
  public MostPlayedService get() {
    return provideMostPlayedService(module);
  }

  public static CacheModule_ProvideMostPlayedServiceFactory create(CacheModule module) {
    return new CacheModule_ProvideMostPlayedServiceFactory(module);
  }

  public static MostPlayedService provideMostPlayedService(CacheModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideMostPlayedService());
  }
}
