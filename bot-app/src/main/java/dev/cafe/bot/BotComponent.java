package dev.cafe.bot;

import dagger.Component;
import dev.cafe.core.AudioController;
import javax.inject.Singleton;

/**
 * Dagger component for dependency injection.
 */
@Singleton
@Component(modules = BotModule.class)
public interface BotComponent {
  AudioController audioController();
}