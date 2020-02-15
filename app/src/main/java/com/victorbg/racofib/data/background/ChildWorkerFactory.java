package com.victorbg.racofib.data.background;

import android.content.Context;

import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public interface ChildWorkerFactory {
  ListenableWorker create(Context appContext, WorkerParameters workerParameters);
}
