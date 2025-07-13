package dev.cafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Loads application configuration from HOCON files with environment variable overrides.
 */
public class ConfigLoader {
  private final Config config;

  public ConfigLoader() {
    this.config = ConfigFactory.load();
  }

  public String getDiscordToken() {
    return config.getString("discord.token");
  }

  public int getShardCount() {
    return config.hasPath("discord.shards") ? config.getInt("discord.shards") : 1;
  }

  public String getAudioBackend() {
    return config.hasPath("audio.backend") ? config.getString("audio.backend") : "lavaplayer";
  }
}