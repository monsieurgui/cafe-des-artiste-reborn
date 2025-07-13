package dev.cafe.metrics;

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
public final class MetricsBinder_Factory implements Factory<MetricsBinder> {
  @Override
  public MetricsBinder get() {
    return newInstance();
  }

  public static MetricsBinder_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MetricsBinder newInstance() {
    return new MetricsBinder();
  }

  private static final class InstanceHolder {
    private static final MetricsBinder_Factory INSTANCE = new MetricsBinder_Factory();
  }
}
