package dev.cafe.core;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class PlaylistManager_Factory implements Factory<PlaylistManager> {
  @Override
  public PlaylistManager get() {
    return newInstance();
  }

  public static PlaylistManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PlaylistManager newInstance() {
    return new PlaylistManager();
  }

  private static final class InstanceHolder {
    private static final PlaylistManager_Factory INSTANCE = new PlaylistManager_Factory();
  }
}
