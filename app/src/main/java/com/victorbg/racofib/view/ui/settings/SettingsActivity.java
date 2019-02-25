package com.victorbg.racofib.view.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.oss.licenses.OssLicensesActivity;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.repository.user.UserRepository;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;


public class SettingsActivity extends BaseActivity implements Injectable, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    PrefManager prefManager;

    @Inject
    UserRepository userRepository;

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
                        .title("Cambiar idioma")
                        .content("Para cambiar el idioma es necesario reiniciar sesión")
                        .positiveText("Reiniciar")
                        .negativeText("Cancelar")
                        .onPositive((dialog, which) -> {
                            userRepository.clean();
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
        }
    }
}
