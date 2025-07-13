package dev.cafe.cache.guild;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import javax.sql.DataSource;

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
public final class SqliteGuildSettingsRepository_Factory implements Factory<SqliteGuildSettingsRepository> {
  private final Provider<DataSource> dataSourceProvider;

  public SqliteGuildSettingsRepository_Factory(Provider<DataSource> dataSourceProvider) {
    this.dataSourceProvider = dataSourceProvider;
  }

  @Override
  public SqliteGuildSettingsRepository get() {
    return newInstance(dataSourceProvider.get());
  }

  public static SqliteGuildSettingsRepository_Factory create(
      Provider<DataSource> dataSourceProvider) {
    return new SqliteGuildSettingsRepository_Factory(dataSourceProvider);
  }

  public static SqliteGuildSettingsRepository newInstance(DataSource dataSource) {
    return new SqliteGuildSettingsRepository(dataSource);
  }
}
