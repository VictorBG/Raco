package com.victorbg.racofib.data.background.digest;

import android.content.Context;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import com.victorbg.racofib.utils.CollectionsUtil;

public class CustomWorkerFactory extends WorkerFactory {

    private final Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workersFactories;

    @Inject
    public CustomWorkerFactory(Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workersFactories) {
        this.workersFactories = workersFactories;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        Provider<ChildWorkerFactory> factoryProvider = CollectionsUtil.getWorkerFactoryProviderByKey(workersFactories, workerClassName);
        return factoryProvider.get().create(appContext, workerParameters);
    }
}