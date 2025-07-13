package dev.cafe.bot;

import dagger.Module;
import dagger.Provides;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.audio.lavaplayer.LavaplayerPlaybackStrategy;
import dev.cafe.audio.lavaplayer.LavaplayerSearchService;
import dev.cafe.core.AudioController;
import dev.cafe.core.VoiceManager;
import dev.cafe.metrics.MetricsBinder;
import javax.inject.Singleton;

/**
 * Dagger module for dependency injection.
 */
@Module
public class BotModule {

  @Provides
  @Singleton
  AudioSearchService provideAudioSearchService() {
    return new LavaplayerSearchService();
  }

  @Provides
  @Singleton
  PlaybackStrategy providePlaybackStrategy(LavaplayerSearchService searchService) {
    return new LavaplayerPlaybackStrategy(searchService);
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
  AudioController provideAudioController(AudioSearchService searchService, 
                                         PlaybackStrategy playbackStrategy,
                                         MetricsBinder metrics) {
    AudioController controller = new AudioController(searchService, playbackStrategy, metrics);
    
    // Set up circular dependency
    if (playbackStrategy instanceof LavaplayerPlaybackStrategy) {
      ((LavaplayerPlaybackStrategy) playbackStrategy).setAudioController(controller);
    }
    
    return controller;
  }
}