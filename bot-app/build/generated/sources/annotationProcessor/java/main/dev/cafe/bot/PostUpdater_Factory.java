package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.guild.GuildSettingsRepository;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dev.cafe.cache.dagger.Streaming")
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
public final class PostUpdater_Factory implements Factory<PostUpdater> {
  private final Provider<GuildSettingsRepository> guildSettingsRepositoryProvider;

  private final Provider<PlaybackStrategy> playbackStrategyProvider;

  public PostUpdater_Factory(Provider<GuildSettingsRepository> guildSettingsRepositoryProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider) {
    this.guildSettingsRepositoryProvider = guildSettingsRepositoryProvider;
    this.playbackStrategyProvider = playbackStrategyProvider;
  }

  @Override
  public PostUpdater get() {
    return newInstance(guildSettingsRepositoryProvider.get(), playbackStrategyProvider.get());
  }

  public static PostUpdater_Factory create(
      Provider<GuildSettingsRepository> guildSettingsRepositoryProvider,
      Provider<PlaybackStrategy> playbackStrategyProvider) {
    return new PostUpdater_Factory(guildSettingsRepositoryProvider, playbackStrategyProvider);
  }

  public static PostUpdater newInstance(GuildSettingsRepository guildSettingsRepository,
      PlaybackStrategy playbackStrategy) {
    return new PostUpdater(guildSettingsRepository, playbackStrategy);
  }
}
