package com.victorbg.racofib.view;

import android.content.Intent;
import android.os.Bundle;

import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;

public class StartActivity extends BaseActivity implements Injectable {

    @Inject
    PrefManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (prefManager.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
