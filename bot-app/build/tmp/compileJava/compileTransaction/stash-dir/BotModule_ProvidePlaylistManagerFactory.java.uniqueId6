package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.core.PlaylistManager;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class BotModule_ProvidePlaylistManagerFactory implements Factory<PlaylistManager> {
  private final BotModule module;

  public BotModule_ProvidePlaylistManagerFactory(BotModule module) {
    this.module = module;
  }

  @Override
  public PlaylistManager get() {
    return providePlaylistManager(module);
  }

  public static BotModule_ProvidePlaylistManagerFactory create(BotModule module) {
    return new BotModule_ProvidePlaylistManagerFactory(module);
  }

  public static PlaylistManager providePlaylistManager(BotModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providePlaylistManager());
  }
}
