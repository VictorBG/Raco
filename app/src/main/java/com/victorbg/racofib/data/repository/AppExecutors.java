package com.victorbg.racofib.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppExecutors {

  private final Executor mDiskIO;

  private final Executor mNetworkIO;

  private final Executor mMainThread;

  private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
    this.mDiskIO = diskIO;
    this.mNetworkIO = networkIO;
    this.mMainThread = mainThread;
  }

  @Inject
  public AppExecutors() {
    this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(3),
        new MainThreadExecutor());
  }

  private Executor diskIO() {
    return mDiskIO;
  }

  private Executor networkIO() {
    return mNetworkIO;
  }

  private Executor mainThread() {
    return mMainThread;
  }

  public void executeOnDisk(Runnable runnable) {
    execute(diskIO(), runnable);
  }

  public void executeOnNetwork(Runnable runnable) {
    execute(networkIO(), runnable);
  }

  public void executeOnMainThread(Runnable runnable) {
    execute(mainThread(), runnable);
  }

  private void execute(@NonNull Executor executor, Runnable runnable) {
    Optional.ofNullable(runnable).ifPresent(executor::execute);
  }

  private static class MainThreadExecutor implements Executor {

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
      mainThreadHandler.post(command);
    }
  }
}
