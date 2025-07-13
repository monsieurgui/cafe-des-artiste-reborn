package dev.cafe.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Prometheus metrics collector for bot performance monitoring.
 */
@Singleton
public class MetricsBinder {
  private final PrometheusMeterRegistry meterRegistry;
  private final Counter commandsExecuted;
  private final Counter tracksPlayed;
  private final Timer commandLatency;
  private final AtomicLong activeGuilds = new AtomicLong(0);
  private final AtomicLong queuedTracks = new AtomicLong(0);

  @Inject
  public MetricsBinder() {
    this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    
    // Command metrics
    this.commandsExecuted = Counter.builder("bot.commands.executed")
        .description("Number of slash commands executed")
        .tag("type", "audio")
        .register(meterRegistry);
    
    this.commandLatency = Timer.builder("bot.commands.latency")
        .description("Command execution latency")
        .register(meterRegistry);
    
    // Audio metrics
    this.tracksPlayed = Counter.builder("bot.tracks.played")
        .description("Total tracks played")
        .register(meterRegistry);
    
    Gauge.builder("bot.guilds.active")
        .description("Number of active guilds")
        .register(meterRegistry, this, m -> m.activeGuilds.get());
    
    Gauge.builder("bot.tracks.queued")
        .description("Total tracks in all queues")
        .register(meterRegistry, this, m -> m.queuedTracks.get());
  }

  public void recordCommandExecuted(String commandName) {
    commandsExecuted.increment();
  }

  public Timer.Sample startCommandTimer() {
    return Timer.start(meterRegistry);
  }

  public void recordCommandLatency(Timer.Sample sample) {
    sample.stop(commandLatency);
  }

  public void recordTrackPlayed() {
    tracksPlayed.increment();
  }

  public void updateActiveGuilds(long count) {
    activeGuilds.set(count);
  }

  public void updateQueuedTracks(long count) {
    queuedTracks.set(count);
  }

  public String getMetrics() {
    return meterRegistry.scrape();
  }

  public MeterRegistry getMeterRegistry() {
    return meterRegistry;
  }
}