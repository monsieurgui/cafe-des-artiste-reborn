package dev.cafe.bot;

import dagger.Module;
import dagger.Provides;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.audio.lavaplayer.LavaplayerPlaybackStrategy;
import dev.cafe.audio.lavaplayer.LavaplayerSearchService;
import dev.cafe.core.AudioController;
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
  AudioController provideAudioController(AudioSearchService searchService, 
                                         PlaybackStrategy playbackStrategy) {
    AudioController controller = new AudioController(searchService, playbackStrategy);
    
    // Set up circular dependency
    if (playbackStrategy instanceof LavaplayerPlaybackStrategy) {
      ((LavaplayerPlaybackStrategy) playbackStrategy).setAudioController(controller);
    }
    
    return controller;
  }
}