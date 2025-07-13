package dev.cafe.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.cafe.audio.lavalink.LavalinkSearchService;
import dev.cafe.config.ConfigLoader;
import dev.cafe.metrics.MetricsBinder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Simple HTTP server for Prometheus metrics endpoint. */
public class MetricsHttpServer {
  private static final Logger logger = LoggerFactory.getLogger(MetricsHttpServer.class);

  private final MetricsBinder metrics;
  private final ConfigLoader config;
  private final ObjectMapper objectMapper;
  private HttpServer server;
  private JDA jda;
  private LavalinkSearchService lavalinkService;

  public MetricsHttpServer(MetricsBinder metrics, ConfigLoader config) {
    this.metrics = metrics;
    this.config = config;
    this.objectMapper = new ObjectMapper();
  }

  public void setJDA(JDA jda) {
    this.jda = jda;
  }

  public void setLavalinkService(LavalinkSearchService lavalinkService) {
    this.lavalinkService = lavalinkService;
  }

  public void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/metrics", new MetricsHandler());
    server.createContext("/health", new HealthHandler());
    server.setExecutor(null);
    server.start();
    logger.info("Metrics server started on port 8080");
  }

  public void stop() {
    if (server != null) {
      server.stop(0);
      logger.info("Metrics server stopped");
    }
  }

  private class MetricsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String response = metrics.getMetrics();
      exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
      exchange.sendResponseHeaders(200, response.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
    }
  }

  private class HealthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      Map<String, Object> health = new HashMap<>();

      try {
        // Check bot status
        if (jda != null) {
          health.put(
              "bot",
              Map.of(
                  "status", jda.getStatus().name(),
                  "ping", jda.getGatewayPing(),
                  "guilds", jda.getGuilds().size(),
                  "shards",
                      jda.getShardInfo() != null
                          ? Map.of(
                              "current", jda.getShardInfo().getShardId(),
                              "total", jda.getShardInfo().getShardTotal())
                          : Map.of("current", 0, "total", 1)));
        } else {
          health.put("bot", Map.of("status", "NOT_READY"));
        }

        // Check Lavalink status if using Lavalink backend
        if ("lavalink".equals(config.getAudioBackend()) && lavalinkService != null) {
          health.put(
              "lavalink",
              Map.of(
                  "host", config.getLavalinkHost(),
                  "port", config.getLavalinkPort(),
                  "healthy", lavalinkService.isHealthy()));
        } else {
          health.put("audio", Map.of("backend", config.getAudioBackend()));
        }

        health.put("timestamp", System.currentTimeMillis());
        health.put("status", "UP");

        String response = objectMapper.writeValueAsString(health);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(response.getBytes());
        }
      } catch (Exception e) {
        logger.error("Health check failed", e);
        health.put("status", "DOWN");
        health.put("error", e.getMessage());

        String response = objectMapper.writeValueAsString(health);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(500, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(response.getBytes());
        }
      }
    }
  }
}
