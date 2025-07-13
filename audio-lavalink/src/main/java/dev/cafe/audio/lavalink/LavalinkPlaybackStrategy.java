package dev.cafe.audio.lavalink;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate;
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdateTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.core.AudioController;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lavalink implementation of PlaybackStrategy using REST API.
 */
@Singleton
public class LavalinkPlaybackStrategy implements PlaybackStrategy {
  private static final Logger logger = LoggerFactory.getLogger(LavalinkPlaybackStrategy.class);
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  
  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String baseUrl;
  private final String password;
  private final ConcurrentMap<Long, String> guildSessions = new ConcurrentHashMap<>();
  private AudioController audioController;

  @Inject
  public LavalinkPlaybackStrategy() {
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    // Default values - should be configurable
    this.baseUrl = "http://lavalink:2333";
    this.password = "youshallnotpass";
  }

  public LavalinkPlaybackStrategy(String host, int port, String password) {
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    this.baseUrl = "http://" + host + ":" + port;
    this.password = password;
  }

  public void setAudioController(AudioController audioController) {
    this.audioController = audioController;
  }

  @Override
  public void startPlayback(long guildId, dev.cafe.audio.AudioTrack track) {
    try {
      if (!(track instanceof LavalinkAudioTrack)) {
        logger.error("Track is not a LavalinkAudioTrack: {}", track.getClass());
        return;
      }

      LavalinkAudioTrack lavalinkTrack = (LavalinkAudioTrack) track;
      String sessionId = guildSessions.computeIfAbsent(guildId, 
          k -> "session-" + guildId);

      PlayerUpdate update = new PlayerUpdate();
      PlayerUpdateTrack updateTrack = new PlayerUpdateTrack();
      updateTrack.setEncoded(lavalinkTrack.getTrackData());
      update.setTrack(updateTrack);

      String json = objectMapper.writeValueAsString(update);
      RequestBody body = RequestBody.create(json, JSON);

      Request request = new Request.Builder()
          .url(baseUrl + "/v4/sessions/" + sessionId + "/players/" + guildId)
          .patch(body)
          .header("Authorization", password)
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (response.isSuccessful()) {
          logger.info("Started playback for guild {}: {}", guildId, track.getTitle());
        } else {
          logger.error("Failed to start playback for guild {}: {} {}", 
              guildId, response.code(), response.message());
        }
      }
    } catch (IOException e) {
      logger.error("Failed to start playback for guild " + guildId, e);
    }
  }

  @Override
  public void stopPlayback(long guildId) {
    updatePlayer(guildId, "{\"track\":{\"encoded\":null}}");
  }

  @Override
  public void pausePlayback(long guildId) {
    updatePlayer(guildId, "{\"paused\":true}");
  }

  @Override
  public void resumePlayback(long guildId) {
    updatePlayer(guildId, "{\"paused\":false}");
  }

  @Override
  public void setVolume(long guildId, int volume) {
    int clampedVolume = Math.max(0, Math.min(100, volume));
    updatePlayer(guildId, "{\"volume\":" + clampedVolume + "}");
  }

  @Override
  public boolean isPlaying(long guildId) {
    // For Lavalink, we'd need to track this state or query the REST API
    // For now, return false as we don't have WebSocket events
    return false;
  }

  @Override
  public boolean isPaused(long guildId) {
    // For Lavalink, we'd need to track this state or query the REST API
    // For now, return false as we don't have WebSocket events
    return false;
  }

  private void updatePlayer(long guildId, String json) {
    try {
      String sessionId = guildSessions.computeIfAbsent(guildId, 
          k -> "session-" + guildId);

      RequestBody body = RequestBody.create(json, JSON);
      Request request = new Request.Builder()
          .url(baseUrl + "/v4/sessions/" + sessionId + "/players/" + guildId)
          .patch(body)
          .header("Authorization", password)
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          logger.error("Failed to update player for guild {}: {} {}", 
              guildId, response.code(), response.message());
        }
      }
    } catch (IOException e) {
      logger.error("Failed to update player for guild " + guildId, e);
    }
  }
}