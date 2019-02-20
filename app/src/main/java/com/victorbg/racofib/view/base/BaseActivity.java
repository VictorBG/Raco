package com.victorbg.racofib.view.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

@SuppressLint("Registered")
public abstract class BaseActivity extends BaseThemeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureUI();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    private void configureUI() {
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
