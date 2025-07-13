package dev.cafe.audio.lavalink;

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
public final class LavalinkSearchService_Factory implements Factory<LavalinkSearchService> {
  @Override
  public LavalinkSearchService get() {
    return newInstance();
  }

  public static LavalinkSearchService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LavalinkSearchService newInstance() {
    return new LavalinkSearchService();
  }

  private static final class InstanceHolder {
    private static final LavalinkSearchService_Factory INSTANCE = new LavalinkSearchService_Factory();
  }
}
