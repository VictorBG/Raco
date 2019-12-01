package com.victorbg.racofib;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import androidx.core.os.ConfigurationCompat;
import androidx.work.WorkManager;

import com.facebook.stetho.Stetho;
import com.victorbg.racofib.data.background.digest.CustomWorkerFactory;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.AppComponent;
import com.victorbg.racofib.di.DaggerAppComponent;
import com.victorbg.racofib.di.injector.AppInjector;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

public class AppRaco extends Application implements HasActivityInjector {

  @Inject
  DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

  @Inject
  PrefManager prefManager;

  @Override
  public void onCreate() {
    super.onCreate();

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//
//        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this);
//        }

    AppInjector.init(this);

    if (BuildConfig.DEBUG) {
      Stetho.initializeWithDefaults(this);
      Timber.plant(new Timber.DebugTree());
    }

    firstTime();
  }

  @Override
  public AndroidInjector<Activity> activityInjector() {
    return dispatchingAndroidInjector;
  }

  private void firstTime() {
    if (prefManager.isFirstTime()) {
      String lang = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
      if (lang.equals(new Locale("en").getLanguage())) {
        prefManager.getSharedPreferences().edit().putString(PrefManager.LOCALE_KEY, PrefManager.LOCALE_ENGLISH).apply();
      } else if (lang.equals(new Locale("ca").getLanguage())) {
        prefManager.getSharedPreferences().edit().putString(PrefManager.LOCALE_KEY, PrefManager.LOCALE_CATALAN).apply();
      }
    }

    int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    prefManager.setDarkTheme(currentNightMode == UI_MODE_NIGHT_YES);
  }
}
