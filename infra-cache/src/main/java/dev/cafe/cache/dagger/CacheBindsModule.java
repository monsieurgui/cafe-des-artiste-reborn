package dev.cafe.cache.dagger;

import dagger.Binds;
import dagger.Module;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.FileCachePlaybackStrategy;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.cache.guild.SqliteGuildSettingsRepository;

@Module
public abstract class CacheBindsModule {

  @Binds
  abstract PlaybackStrategy bindPlaybackStrategy(FileCachePlaybackStrategy strategy);

  @Binds
  abstract GuildSettingsRepository bindGuildSettingsRepository(
      SqliteGuildSettingsRepository repository);
}
