package dev.cafe.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages voice channel connections and audio send handlers.
 */
@Singleton
public class VoiceManager {
  private static final Logger logger = LoggerFactory.getLogger(VoiceManager.class);
  
  private final ConcurrentMap<Long, Long> guildVoiceChannels = new ConcurrentHashMap<>();

  @Inject
  public VoiceManager() {
  }

  public void joinVoiceChannel(long guildId, long voiceChannelId) {
    guildVoiceChannels.put(guildId, voiceChannelId);
    logger.info("Joined voice channel {} in guild {}", voiceChannelId, guildId);
  }

  public void leaveVoiceChannel(long guildId) {
    Long channelId = guildVoiceChannels.remove(guildId);
    if (channelId != null) {
      logger.info("Left voice channel {} in guild {}", channelId, guildId);
    }
  }

  public boolean isConnected(long guildId) {
    return guildVoiceChannels.containsKey(guildId);
  }

  public Long getVoiceChannelId(long guildId) {
    return guildVoiceChannels.get(guildId);
  }
}