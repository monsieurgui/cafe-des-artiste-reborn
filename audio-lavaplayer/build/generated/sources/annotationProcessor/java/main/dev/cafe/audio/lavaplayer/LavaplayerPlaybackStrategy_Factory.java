package dev.cafe.audio.lavaplayer;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class LavaplayerPlaybackStrategy_Factory implements Factory<LavaplayerPlaybackStrategy> {
  private final Provider<LavaplayerSearchService> searchServiceProvider;

  public LavaplayerPlaybackStrategy_Factory(
      Provider<LavaplayerSearchService> searchServiceProvider) {
    this.searchServiceProvider = searchServiceProvider;
  }

  @Override
  public LavaplayerPlaybackStrategy get() {
    return newInstance(searchServiceProvider.get());
  }

  public static LavaplayerPlaybackStrategy_Factory create(
      Provider<LavaplayerSearchService> searchServiceProvider) {
    return new LavaplayerPlaybackStrategy_Factory(searchServiceProvider);
  }

  public static LavaplayerPlaybackStrategy newInstance(LavaplayerSearchService searchService) {
    return new LavaplayerPlaybackStrategy(searchService);
  }
}
