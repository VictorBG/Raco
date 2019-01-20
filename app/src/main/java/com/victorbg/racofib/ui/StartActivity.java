package com.victorbg.racofib.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.ui.base.BaseActivity;
import com.victorbg.racofib.ui.login.LoginActivity;

import androidx.annotation.Nullable;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PrefManager.getInstance().isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
