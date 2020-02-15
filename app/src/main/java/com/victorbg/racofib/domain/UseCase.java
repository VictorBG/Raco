package com.victorbg.racofib.domain;

import androidx.annotation.NonNull;

import com.victorbg.racofib.data.repository.AppExecutors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.security.InvalidParameterException;
import java.util.function.Supplier;

public abstract class UseCase<P, R> {

  protected final AppExecutors appExecutors;
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  public UseCase(AppExecutors appExecutors) {
    this.appExecutors = appExecutors;
  }

  /**
   * Executes the use case without parameters
   *
   * @return R
   */
  public R execute() {
    throw new InvalidParameterException("execute() cannot be called without a valid parameter");
  }

  /**
   * Executes the use case with parameters
   *
   * @param parameter P
   * @return R
   */
  public R execute(P parameter) {
    return execute();
  }

  protected <T> void executeSingleAction(
      @NonNull Supplier<Single<T>> supplier, @NonNull final Consumer<? super T> onSuccess) {
    compositeDisposable.add(
        supplier
            .get()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess));
  }

  protected <T> void executeSingleAction(
      @NonNull Supplier<Single<T>> supplier,
      @NonNull final Consumer<? super T> onSuccess,
      final Consumer<? super Throwable> onError) {

    if (onError == null) {
      executeSingleAction(supplier, onSuccess);
    } else {
      compositeDisposable.add(
          supplier
              .get()
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(onSuccess, onError));
    }
  }
}
