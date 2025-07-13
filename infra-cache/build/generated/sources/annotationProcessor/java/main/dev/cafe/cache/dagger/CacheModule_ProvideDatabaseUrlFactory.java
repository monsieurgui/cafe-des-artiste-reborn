package dev.cafe.cache.dagger;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.config.ConfigLoader;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class CacheModule_ProvideDatabaseUrlFactory implements Factory<String> {
  private final CacheModule module;

  private final Provider<ConfigLoader> configLoaderProvider;

  public CacheModule_ProvideDatabaseUrlFactory(CacheModule module,
      Provider<ConfigLoader> configLoaderProvider) {
    this.module = module;
    this.configLoaderProvider = configLoaderProvider;
  }

  @Override
  public String get() {
    return provideDatabaseUrl(module, configLoaderProvider.get());
  }

  public static CacheModule_ProvideDatabaseUrlFactory create(CacheModule module,
      Provider<ConfigLoader> configLoaderProvider) {
    return new CacheModule_ProvideDatabaseUrlFactory(module, configLoaderProvider);
  }

  public static String provideDatabaseUrl(CacheModule instance, ConfigLoader configLoader) {
    return Preconditions.checkNotNullFromProvides(instance.provideDatabaseUrl(configLoader));
  }
}
