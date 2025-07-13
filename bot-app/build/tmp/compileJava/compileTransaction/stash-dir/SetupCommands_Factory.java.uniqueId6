package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.cache.guild.GuildSettingsRepository;
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
public final class SetupCommands_Factory implements Factory<SetupCommands> {
  private final Provider<GuildSettingsRepository> guildSettingsRepositoryProvider;

  public SetupCommands_Factory(Provider<GuildSettingsRepository> guildSettingsRepositoryProvider) {
    this.guildSettingsRepositoryProvider = guildSettingsRepositoryProvider;
  }

  @Override
  public SetupCommands get() {
    return newInstance(guildSettingsRepositoryProvider.get());
  }

  public static SetupCommands_Factory create(
      Provider<GuildSettingsRepository> guildSettingsRepositoryProvider) {
    return new SetupCommands_Factory(guildSettingsRepositoryProvider);
  }

  public static SetupCommands newInstance(GuildSettingsRepository guildSettingsRepository) {
    return new SetupCommands(guildSettingsRepository);
  }
}
