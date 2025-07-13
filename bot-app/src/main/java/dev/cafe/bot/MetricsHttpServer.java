package dev.cafe.bot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.cafe.metrics.MetricsBinder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple HTTP server for Prometheus metrics endpoint.
 */
public class MetricsHttpServer {
  private static final Logger logger = LoggerFactory.getLogger(MetricsHttpServer.class);
  
  private final MetricsBinder metrics;
  private HttpServer server;

  public MetricsHttpServer(MetricsBinder metrics) {
    this.metrics = metrics;
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
      String response = "OK";
      exchange.getResponseHeaders().set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, response.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
    }
  }
}