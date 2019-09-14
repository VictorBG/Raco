package com.victorbg.racofib.di.injector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.victorbg.racofib.AppRaco;
import com.victorbg.racofib.di.DaggerAppComponent;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

public class AppInjector {

  private AppInjector() {
  }

  public static void init(AppRaco appClass) {
    DaggerAppComponent.builder().application(appClass)
        .build().inject(appClass);

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

}
