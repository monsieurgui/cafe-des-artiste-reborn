package dev.cafe.audio.lavaplayer;

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
public final class LavaplayerSearchService_Factory implements Factory<LavaplayerSearchService> {
  @Override
  public LavaplayerSearchService get() {
    return newInstance();
  }

  public static LavaplayerSearchService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LavaplayerSearchService newInstance() {
    return new LavaplayerSearchService();
  }

  private static final class InstanceHolder {
    private static final LavaplayerSearchService_Factory INSTANCE = new LavaplayerSearchService_Factory();
  }
}
