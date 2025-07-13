package dev.cafe.cache.dagger;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import javax.sql.DataSource;

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
public final class CacheModule_ProvideDataSourceFactory implements Factory<DataSource> {
  private final CacheModule module;

  private final Provider<String> databaseUrlProvider;

  public CacheModule_ProvideDataSourceFactory(CacheModule module,
      Provider<String> databaseUrlProvider) {
    this.module = module;
    this.databaseUrlProvider = databaseUrlProvider;
  }

  @Override
  public DataSource get() {
    return provideDataSource(module, databaseUrlProvider.get());
  }

  public static CacheModule_ProvideDataSourceFactory create(CacheModule module,
      Provider<String> databaseUrlProvider) {
    return new CacheModule_ProvideDataSourceFactory(module, databaseUrlProvider);
  }

  public static DataSource provideDataSource(CacheModule instance, String databaseUrl) {
    return Preconditions.checkNotNullFromProvides(instance.provideDataSource(databaseUrl));
  }
}
