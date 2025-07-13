package dev.cafe.audio.lavalink;

import dev.cafe.audio.PlaybackStrategy;
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

/** Lavalink implementation of PlaybackStrategy using REST API. */
@Singleton
public class LavalinkPlaybackStrategy implements PlaybackStrategy {
  private static final Logger logger = LoggerFactory.getLogger(LavalinkPlaybackStrategy.class);
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient httpClient;
  private final String baseUrl;
  private final String password;
  private final ConcurrentMap<Long, String> guildSessions = new ConcurrentHashMap<>();

  @Inject
  public LavalinkPlaybackStrategy() {
    this.httpClient = new OkHttpClient();
    // Default values - should be configurable
    this.baseUrl = "http://lavalink:2333";
    this.password = "youshallnotpass";
  }

  public LavalinkPlaybackStrategy(String host, int port, String password) {
    this.httpClient = new OkHttpClient();
    this.baseUrl = "http://" + host + ":" + port;
    this.password = password;
  }

  @Override
  public void startPlayback(long guildId, dev.cafe.audio.AudioTrack track) {
    try {
      if (!(track instanceof LavalinkAudioTrack)) {
        logger.error("Track is not a LavalinkAudioTrack: {}", track.getClass());
        return;
      }

      LavalinkAudioTrack lavalinkTrack = (LavalinkAudioTrack) track;
      String sessionId = guildSessions.computeIfAbsent(guildId, k -> "session-" + guildId);

      // Create JSON manually since the PlayerUpdate objects don't have setters
      String json = String.format("{\"track\":{\"encoded\":\"%s\"}}", lavalinkTrack.getTrackData());
      RequestBody body = RequestBody.create(json, JSON);

      Request request =
          new Request.Builder()
              .url(baseUrl + "/v4/sessions/" + sessionId + "/players/" + guildId)
              .patch(body)
              .header("Authorization", password)
              .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (response.isSuccessful()) {
          logger.info("Started playback for guild {}: {}", guildId, track.getTitle());
        } else {
          logger.error(
              "Failed to start playback for guild {}: {} {}",
              guildId,
              response.code(),
              response.message());
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

  @Override
  public dev.cafe.audio.AudioTrack getCurrentTrack(long guildId) {
    // TODO: Implement by querying the Lavalink REST API
    return null;
  }

  @Override
  public long getPosition(long guildId) {
    // TODO: Implement by querying the Lavalink REST API
    return 0;
  }

  private void updatePlayer(long guildId, String json) {
    try {
      String sessionId = guildSessions.computeIfAbsent(guildId, k -> "session-" + guildId);

      RequestBody body = RequestBody.create(json, JSON);
      Request request =
          new Request.Builder()
              .url(baseUrl + "/v4/sessions/" + sessionId + "/players/" + guildId)
              .patch(body)
              .header("Authorization", password)
              .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          logger.error(
              "Failed to update player for guild {}: {} {}",
              guildId,
              response.code(),
              response.message());
        }
      }
    } catch (IOException e) {
      logger.error("Failed to update player for guild " + guildId, e);
    }
  }
}
