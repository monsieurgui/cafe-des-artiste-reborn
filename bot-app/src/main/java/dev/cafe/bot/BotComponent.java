package dev.cafe.bot;

import dagger.Component;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.dagger.CacheModule;
import dev.cafe.core.AudioController;
import dev.cafe.core.PlaylistManager;
import javax.inject.Singleton;

/** Dagger component for dependency injection. */
@Singleton
@Component(modules = {BotModule.class, CacheModule.class})
public interface BotComponent {
  AudioController audioController();

  PlaybackStrategy playbackStrategy();

  PlaylistManager playlistManager();
}
