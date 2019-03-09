package com.victorbg.racofib.view.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.user.LogoutUserUseCase;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;


public class SettingsActivity extends BaseActivity implements Injectable, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    PrefManager prefManager;

    @Inject
    LogoutUserUseCase logoutUserUseCase;

    private String locale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPreferenceListener();

        locale = prefManager.getLocale();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.settings);
    }

    private void setPreferenceListener() {
        prefManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("DarkTheme")) {
            prefManager.refreshDarkTheme();
            recreate();
        }

        if (key.equals("LocaleApp")) {
            if (!locale.equals(sharedPreferences.getString(key, PrefManager.LOCALE_SPANISH))) {
                MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                        .title(getString(R.string.change_language_title))
                        .content(getString(R.string.change_language_description))
                        .positiveText(getString(R.string.restart))
                        .negativeText(getString(R.string.cancel))
                        .onPositive((dialog, which) -> {
                            logoutUserUseCase.execute();
                            dialog.dismiss();
                            onBackPressed();
                        })
                        .onNegative(((dialog, which) -> {
                            sharedPreferences.edit().putString(key, locale).apply();
                            dialog.dismiss();
                        })).build();
                materialDialog.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        prefManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected int getLightTheme() {
        return R.style.AppTheme_Settings_Light;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_Settings_Dark;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            findPreference("OpenSource").setOnPreferenceClickListener(v -> {
                startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
                return true;
            });

            findPreference("SubjectsColors").setOnPreferenceClickListener(v -> {
                startActivity(new Intent(getActivity(), ColorSettingsActivity.class));
                return true;
            });
        }
    }
}
