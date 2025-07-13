package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.config.ConfigLoader;
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
public final class BotModule_ProvideAudioSearchServiceFactory implements Factory<AudioSearchService> {
  private final BotModule module;

  private final Provider<ConfigLoader> configProvider;

  public BotModule_ProvideAudioSearchServiceFactory(BotModule module,
      Provider<ConfigLoader> configProvider) {
    this.module = module;
    this.configProvider = configProvider;
  }

  @Override
  public AudioSearchService get() {
    return provideAudioSearchService(module, configProvider.get());
  }

  public static BotModule_ProvideAudioSearchServiceFactory create(BotModule module,
      Provider<ConfigLoader> configProvider) {
    return new BotModule_ProvideAudioSearchServiceFactory(module, configProvider);
  }

  public static AudioSearchService provideAudioSearchService(BotModule instance,
      ConfigLoader config) {
    return Preconditions.checkNotNullFromProvides(instance.provideAudioSearchService(config));
  }
}
