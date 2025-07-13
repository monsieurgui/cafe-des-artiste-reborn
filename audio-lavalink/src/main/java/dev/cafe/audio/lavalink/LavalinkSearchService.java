package dev.cafe.audio.lavalink;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.SearchResult;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lavalink implementation of AudioSearchService using REST API.
 */
@Singleton
public class LavalinkSearchService implements AudioSearchService {
  private static final Logger logger = LoggerFactory.getLogger(LavalinkSearchService.class);
  
  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String baseUrl;
  private final String password;

  @Inject
  public LavalinkSearchService() {
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    // Default values - should be configurable
    this.baseUrl = "http://lavalink:2333";
    this.password = "youshallnotpass";
  }

  public LavalinkSearchService(String host, int port, String password) {
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    this.baseUrl = "http://" + host + ":" + port;
    this.password = password;
  }

  @Override
  public CompletableFuture<SearchResult> search(String query) {
    String searchQuery = query.startsWith("http") ? query : "ytsearch:" + query;
    return loadItem(searchQuery);
  }

  @Override
  public CompletableFuture<SearchResult> loadTrack(String url) {
    return loadItem(url);
  }

  private CompletableFuture<SearchResult> loadItem(String identifier) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        String encodedIdentifier = URLEncoder.encode(identifier, StandardCharsets.UTF_8);
        String url = baseUrl + "/v4/loadtracks?identifier=" + encodedIdentifier;
        
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", password)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
          if (!response.isSuccessful()) {
            logger.error("Lavalink request failed with status {}: {}", 
                response.code(), response.message());
            return LavalinkSearchResult.fromLoadResult(createErrorResult(
                "HTTP " + response.code() + ": " + response.message()));
          }

          String responseBody = response.body().string();
          LoadResult loadResult = objectMapper.readValue(responseBody, LoadResult.class);
          return LavalinkSearchResult.fromLoadResult(loadResult);
        }
      } catch (IOException e) {
        logger.error("Failed to load track from Lavalink", e);
        return LavalinkSearchResult.fromLoadResult(createErrorResult(e.getMessage()));
      }
    });
  }

  private LoadResult createErrorResult(String message) {
    LoadResult errorResult = new LoadResult();
    errorResult.setLoadType(LoadResult.LoadType.ERROR);
    
    LoadResult.ErrorData errorData = new LoadResult.ErrorData();
    errorData.setMessage(message);
    errorResult.setData(errorData);
    
    return errorResult;
  }

  public boolean isHealthy() {
    try {
      Request request = new Request.Builder()
          .url(baseUrl + "/version")
          .header("Authorization", password)
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        return response.isSuccessful();
      }
    } catch (Exception e) {
      logger.debug("Lavalink health check failed", e);
      return false;
    }
  }
}