package dev.cafe.bot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.cafe.metrics.MetricsBinder;
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
public final class BotModule_ProvideMetricsBinderFactory implements Factory<MetricsBinder> {
  private final BotModule module;

  public BotModule_ProvideMetricsBinderFactory(BotModule module) {
    this.module = module;
  }

  @Override
  public MetricsBinder get() {
    return provideMetricsBinder(module);
  }

  public static BotModule_ProvideMetricsBinderFactory create(BotModule module) {
    return new BotModule_ProvideMetricsBinderFactory(module);
  }

  public static MetricsBinder provideMetricsBinder(BotModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideMetricsBinder());
  }
}
