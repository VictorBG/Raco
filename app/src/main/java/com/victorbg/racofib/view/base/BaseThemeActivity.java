package com.victorbg.racofib.view.base;

import android.os.Bundle;

import com.afollestad.rxkprefs.Pref;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dagger.android.AndroidInjection;
import timber.log.Timber;

public abstract class BaseThemeActivity extends AppCompatActivity implements Injectable {

    @Inject
    PrefManager prefManager;

    protected int themeId;

    protected boolean isDarkThemeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        if (prefManager != null && prefManager.isDarkThemeEnabled()) {
            isDarkThemeEnabled = true;
            themeId = getDarkTheme();
        } else {
            isDarkThemeEnabled = false;
            themeId = getLightTheme();
        }
        setTheme(themeId);
        super.onCreate(savedInstanceState);
    }

    protected int getLightTheme() {
        return R.style.AppTheme_Light;
    }

    protected int getDarkTheme() {
        return R.style.AppTheme_Dark;
    }

    protected void internalRecreate() {
        if (isDarkThemeEnabled != prefManager.isDarkThemeEnabled()) {
            recreate();
        }
    }

}
