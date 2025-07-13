package dev.cafe.bot;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

/**
 * A mock implementation of {@link RestAction} for testing purposes.
 *
 * @param <T> The type of the result.
 */
public class RestActionMock<T> implements RestAction<T> {

  private final T result;

  public RestActionMock(T result) {
    this.result = result;
  }

  @NotNull
  @Override
  public JDA getJDA() {
    return null;
  }

  @NotNull
  @Override
  public RestAction<T> setCheck(BooleanSupplier checks) {
    return this;
  }

  @Override
  public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
    if (success != null) {
      success.accept(result);
    }
  }

  @Override
  public T complete(boolean shouldQueue) {
    return result;
  }

  @NotNull
  @Override
  public CompletableFuture<T> submit(boolean shouldQueue) {
    return CompletableFuture.completedFuture(result);
  }
}
