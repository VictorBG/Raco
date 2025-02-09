package com.victorbg.racofib.di;

import android.app.Application;

import com.victorbg.racofib.AppRaco;
import com.victorbg.racofib.data.background.CustomWorkerFactory;
import com.victorbg.racofib.data.preferences.PrefManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(
    modules = {
      AppModule.class,
      AndroidInjectionModule.class,
      ActivitiesModule.class,
      WorkerModule.class
    })
public interface AppComponent {

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder application(Application application);

    AppComponent build();
  }

  void inject(AppRaco appClass);

  PrefManager getPrefManager();

  CustomWorkerFactory factory();
}
