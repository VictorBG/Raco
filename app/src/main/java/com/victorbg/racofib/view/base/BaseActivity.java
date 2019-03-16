package com.victorbg.racofib.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.background.AttachmentDownload;
import com.victorbg.racofib.data.model.notes.Attachment;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;

@SuppressLint("Registered")
public class BaseActivity extends BaseThemeActivity implements Injectable {

    @Inject
    AttachmentDownload attachmentDownload;

    private Attachment attachmentSaved = null;

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

    public void downloadFile(Attachment attachment) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.attachmentSaved = attachment;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            attachmentDownload.download(attachment, this);
            attachmentSaved = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile(attachmentSaved);
            }
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
        if (isDarkThemeEnabled) {
            snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFBDBDBD")));
            ((TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setTextColor(getResources().getColor(R.color.md_light_primary_text));
        }
        snackbar.show();
        return snackbar;
    }
}
