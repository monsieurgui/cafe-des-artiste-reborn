package dev.cafe.bot;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dev.cafe.core.QueueChangeListener;

@Module
public abstract class BotBindsModule {

  @BindsOptionalOf
  abstract QueueChangeListener optionalQueueChangeListener();

  @Binds
  abstract QueueChangeListener bindQueueChangeListener(PostUpdater impl);
}
