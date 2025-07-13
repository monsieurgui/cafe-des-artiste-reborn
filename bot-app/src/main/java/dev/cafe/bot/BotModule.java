package dev.cafe.bot;

import dagger.Module;
import dagger.Provides;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.audio.lavalink.LavalinkPlaybackStrategy;
import dev.cafe.audio.lavalink.LavalinkSearchService;
import dev.cafe.audio.lavaplayer.LavaplayerPlaybackStrategy;
import dev.cafe.audio.lavaplayer.LavaplayerSearchService;
import dev.cafe.cache.MostPlayedService;
import dev.cafe.cache.TrackCacheService;
import dev.cafe.cache.dagger.Streaming;
import dev.cafe.config.ConfigLoader;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import dev.cafe.core.QueueChangeListener;
import dev.cafe.core.VoiceManager;
import dev.cafe.metrics.MetricsBinder;
import java.util.Optional;
import javax.inject.Singleton;

/** Dagger module for dependency injection. */
@Module
public class BotModule {

  @Provides
  @Singleton
  ConfigLoader provideConfigLoader() {
    return new ConfigLoader();
  }

  @Provides
  @Singleton
  AudioSearchService provideAudioSearchService(ConfigLoader config) {
    String backend = config.getAudioBackend();
    if ("lavalink".equals(backend)) {
      return new LavalinkSearchService(
          config.getLavalinkHost(), config.getLavalinkPort(), config.getLavalinkPassword());
    } else {
      return new LavaplayerSearchService();
    }
  }

  @Provides
  @Singleton
  @Streaming
  PlaybackStrategy provideStreamingPlaybackStrategy(
      ConfigLoader config, AudioSearchService searchService) {
    String backend = config.getAudioBackend();
    if ("lavalink".equals(backend)) {
      return new LavalinkPlaybackStrategy(
          config.getLavalinkHost(), config.getLavalinkPort(), config.getLavalinkPassword());
    } else {
      return new LavaplayerPlaybackStrategy((LavaplayerSearchService) searchService);
    }
  }

  @Provides
  @Singleton
  VoiceManager provideVoiceManager() {
    return new VoiceManager();
  }

  @Provides
  @Singleton
  MetricsBinder provideMetricsBinder() {
    return new MetricsBinder();
  }

  @Provides
  @Singleton
  PlaylistManager providePlaylistManager() {
    return new PlaylistManager();
  }

  @Provides
  @Singleton
  AudioController provideAudioController(
      AudioSearchService searchService,
      @Streaming PlaybackStrategy playbackStrategy,
      MetricsBinder metrics,
      MostPlayedService mostPlayedService,
      TrackCacheService trackCacheService,
      Optional<QueueChangeListener> queueChangeListener) {
    AudioController controller =
        new AudioController(
            searchService,
            playbackStrategy,
            metrics,
            mostPlayedService,
            trackCacheService,
            queueChangeListener);

    // Set up circular dependency
    if (playbackStrategy instanceof LavaplayerPlaybackStrategy) {
      ((LavaplayerPlaybackStrategy) playbackStrategy).setAudioController(controller);
    }

    return controller;
  }

  @Provides
  @Singleton
  CacheCommands provideCacheCommands(
      MostPlayedService mostPlayedService, TrackCacheService trackCacheService) {
    return new CacheCommands(mostPlayedService, trackCacheService);
  }
}
