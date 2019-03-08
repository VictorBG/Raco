package com.victorbg.racofib;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.AppInjector;

import java.util.Locale;

import javax.inject.Inject;

import androidx.core.os.ConfigurationCompat;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

public class AppRaco extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    PrefManager prefManager;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }

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
    }
}
