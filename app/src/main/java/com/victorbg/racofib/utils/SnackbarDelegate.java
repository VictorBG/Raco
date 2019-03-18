package com.victorbg.racofib.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;

public class SnackbarDelegate {

    private Activity activity;
    private boolean isDarkThemeEnabled = false;

    public SnackbarDelegate(Activity activity, boolean isDarkThemeEnabled) {
        this.activity = activity;
        this.isDarkThemeEnabled = isDarkThemeEnabled;
    }

    public Snackbar showSnackbar(String s) {
        return showSnackbar(s, Snackbar.LENGTH_LONG);
    }

    public Snackbar showSnackbar(String s, int length) {
        return showSnackbar(activity.findViewById(android.R.id.content), s, length);
    }

    public Snackbar showSnackbar(View v, String s) {
        return showSnackbar(v, s, Snackbar.LENGTH_LONG);
    }

    public Snackbar showSnackbar(View v, String s, int length) {
        Snackbar snackbar = Snackbar.make(v, s, length);
        if (isDarkThemeEnabled) {
            snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBDBDBD")));
            ((TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setTextColor(activity.getResources().getColor(R.color.md_light_primary_text));
        }
        snackbar.show();
        return snackbar;
    }
}
