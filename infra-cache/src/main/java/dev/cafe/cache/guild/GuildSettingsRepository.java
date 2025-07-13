package dev.cafe.cache.guild;

import java.util.Optional;

/** Manages persistence for guild-specific settings. */
public interface GuildSettingsRepository {

  /**
   * Finds the settings for a given guild.
   *
   * @param guildId The ID of the guild.
   * @return An {@link Optional} containing the settings if found, otherwise empty.
   */
  Optional<GuildSettings> findById(long guildId);

  /**
   * Saves or updates the settings for a guild.
   *
   * @param settings The settings to save.
   */
  void save(GuildSettings settings);

  /** Creates the required database table if it does not exist. */
  void createTable();
}
