package com.victorbg.racofib.di.injector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.victorbg.racofib.AppRaco;
import com.victorbg.racofib.data.background.digest.CustomWorkerFactory;
import com.victorbg.racofib.di.AppComponent;
import com.victorbg.racofib.di.DaggerAppComponent;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

public class AppInjector {

    private static AppComponent appComponent;

    private AppInjector() {
    }

    public static void init(AppRaco appClass) {
        appComponent = DaggerAppComponent.builder().application(appClass)
                .build();
        appComponent.inject(appClass);

        // Configure workers
        CustomWorkerFactory factory = appComponent.factory();
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(factory)
                .build();

        WorkManager.initialize(appClass, config);

        appClass.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                handleActivity(activity);
            }
            //region unused methods

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            //endregion
        });
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasSupportFragmentInjector || activity instanceof Injectable) {
            AndroidInjection.inject(activity);
        }
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentCreated(@NotNull FragmentManager fm,
                                                              @NotNull Fragment fragment,
                                                              Bundle savedInstanceState) {
                                    if (fragment instanceof Injectable) {
                                        AndroidSupportInjection.inject(fragment);
                                    }
                                }
                            }, true);
        }
    }

    public static AppComponent getAppComponent() {
        return Preconditions.checkNotNull(appComponent);
    }
}
