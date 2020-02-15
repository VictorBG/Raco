package com.victorbg.racofib.di;

import com.victorbg.racofib.data.background.ChildWorkerFactory;
import com.victorbg.racofib.data.background.digest.DigestWorker;
import com.victorbg.racofib.data.background.season.SeasonCheckTask;
import com.victorbg.racofib.di.annotations.WorkerKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


@Module
public interface WorkerModule {

  @Binds
  @IntoMap
  @WorkerKey(DigestWorker.class)
  ChildWorkerFactory bindDigestWorker(DigestWorker.Factory factory);

  @Binds
  @IntoMap
  @WorkerKey(SeasonCheckTask.class)
  ChildWorkerFactory bindSeasonCheckTask(SeasonCheckTask.Factory factory);
}
