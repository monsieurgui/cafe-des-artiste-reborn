package dev.cafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/** Loads application configuration from HOCON files with environment variable overrides. */
public class ConfigLoader {
  private final Config config;

  public ConfigLoader() {
    this.config = ConfigFactory.load();
  }

  public Config getConfig() {
    return config;
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

  public String getLavalinkHost() {
    return config.hasPath("audio.lavalink.host")
        ? config.getString("audio.lavalink.host")
        : "lavalink";
  }

  public int getLavalinkPort() {
    return config.hasPath("audio.lavalink.port") ? config.getInt("audio.lavalink.port") : 2333;
  }

  public String getLavalinkPassword() {
    return config.hasPath("audio.lavalink.password")
        ? config.getString("audio.lavalink.password")
        : "youshallnotpass";
  }
}
