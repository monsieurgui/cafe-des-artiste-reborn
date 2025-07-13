package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.core.VoiceManager;
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
public final class BotModule_ProvideVoiceManagerFactory implements Factory<VoiceManager> {
  private final BotModule module;

  public BotModule_ProvideVoiceManagerFactory(BotModule module) {
    this.module = module;
  }

  @Override
  public VoiceManager get() {
    return provideVoiceManager(module);
  }

  public static BotModule_ProvideVoiceManagerFactory create(BotModule module) {
    return new BotModule_ProvideVoiceManagerFactory(module);
  }

  public static VoiceManager provideVoiceManager(BotModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideVoiceManager());
  }
}
