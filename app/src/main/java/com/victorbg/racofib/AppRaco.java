package com.victorbg.racofib;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.victorbg.racofib.data.DataManager;
import com.victorbg.racofib.data.sp.PrefManager;

public class AppRaco extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PrefManager.initialize(this);
        DataManager.getInstance(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
