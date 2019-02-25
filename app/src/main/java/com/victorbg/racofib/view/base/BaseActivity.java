package com.victorbg.racofib.view.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

@SuppressLint("Registered")
public abstract class BaseActivity extends BaseThemeActivity implements Injectable {

    private String locale;

    @Override
    public void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        locale = sharedPreferences.getString(PrefManager.LOCALE_KEY, PrefManager.LOCALE_SPANISH);
        super.attachBaseContext(BaseContextWrapper.wrap(newBase, locale));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void internalRecreate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (isDarkThemeEnabled != prefManager.isDarkThemeEnabled() ||
                !locale.equals(sharedPreferences.getString(PrefManager.LOCALE_KEY, PrefManager.LOCALE_SPANISH))) {
            recreate();
        }
    }


    public Snackbar showSnackbar(String s) {
        return showSnackbar(s, Snackbar.LENGTH_LONG);
    }

    public Snackbar showSnackbar(String s, int length) {
        return showSnackbar(findViewById(android.R.id.content), s, length);
    }

    public Snackbar showSnackbar(View v, String s) {
        return showSnackbar(v, s, Snackbar.LENGTH_LONG);
    }

    public Snackbar showSnackbar(View v, String s, int length) {
        Snackbar snackbar = Snackbar.make(v, s, length);
        snackbar.show();
        return snackbar;
    }
}
