package com.victorbg.racofib.view.base;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import com.victorbg.racofib.data.sp.PrefManager;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Created by Víctor Blanco (VictorBG).
 */

public class BaseContextWrapper extends ContextWrapper {


    private static String language = PrefManager.LOCALE_SPANISH;

    public BaseContextWrapper(Context base) {
        super(base);
    }

    public static void setLanguageLocale(String string) {
        language = string;
    }

    public static ContextWrapper wrap(Context context, @NonNull String locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        Locale newLocale = new Locale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else {
            configuration.setLocale(newLocale);
            //context = context.createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        }

        return new ContextWrapper(context);
    }

    public static Context getContextAware(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        Locale newLocale = new Locale(language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else {
            configuration.setLocale(newLocale);
            //context = context.createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        }

        return context;
    }


}
