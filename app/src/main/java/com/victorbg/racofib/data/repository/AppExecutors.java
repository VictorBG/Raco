package com.victorbg.racofib.data.repository;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

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
    this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
        new MainThreadExecutor());
  }

  public Executor diskIO() {
    return mDiskIO;
  }

  public Executor networkIO() {
    return mNetworkIO;
  }

  public Executor mainThread() {
    return mMainThread;
  }

  private static class MainThreadExecutor implements Executor {

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
      mainThreadHandler.post(command);
    }
  }
}
