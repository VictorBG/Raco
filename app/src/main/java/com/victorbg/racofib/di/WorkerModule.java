package com.victorbg.racofib.di;

import androidx.work.Worker;

import com.victorbg.racofib.data.background.Loteria;
import com.victorbg.racofib.data.background.digest.ChildWorkerFactory;
import com.victorbg.racofib.data.background.digest.DigestWorker;
import com.victorbg.racofib.di.annotations.WorkerKey;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;


@Module
public interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(DigestWorker.class)
    ChildWorkerFactory bindDigestWorker(DigestWorker.Factory factory);

    @Binds
    @IntoMap
    @WorkerKey(Loteria.class)
    ChildWorkerFactory bindLoteria(Loteria.Factory factory);
}
