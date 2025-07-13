package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.config.ConfigLoader;
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
public final class BotModule_ProvideConfigLoaderFactory implements Factory<ConfigLoader> {
  private final BotModule module;

  public BotModule_ProvideConfigLoaderFactory(BotModule module) {
    this.module = module;
  }

  @Override
  public ConfigLoader get() {
    return provideConfigLoader(module);
  }

  public static BotModule_ProvideConfigLoaderFactory create(BotModule module) {
    return new BotModule_ProvideConfigLoaderFactory(module);
  }

  public static ConfigLoader provideConfigLoader(BotModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideConfigLoader());
  }
}
