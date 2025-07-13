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
public final class VoiceManager_Factory implements Factory<VoiceManager> {
  @Override
  public VoiceManager get() {
    return newInstance();
  }

  public static VoiceManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VoiceManager newInstance() {
    return new VoiceManager();
  }

  private static final class InstanceHolder {
    private static final VoiceManager_Factory INSTANCE = new VoiceManager_Factory();
  }
}
