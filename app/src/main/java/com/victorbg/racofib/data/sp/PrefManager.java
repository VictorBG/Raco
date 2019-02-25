package com.victorbg.racofib.data.sp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.model.login.LoginData;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

/**
 * Class to manage the preferences of the application using a Singleton
 * pattern. It is initialized in a secure way to prevent duplicate on more
 * than one thread.
 * <p>
 * TODO: Tests are not ready yet for this class.
 */
@Singleton
public class PrefManager {

    private static final String TOKEN_KEY = "AuthToken";
    private static final String REFRESH_TOKEN_KEY = "AuthRefreshToken";
    private static final String LOGGED_KEY = "UserLogged";
    private static final String EXPIRATION_KEY = "TokenExpiration";
    private static final String DARK_THEME_KEY = "DarkTheme";
    public static final String LOCALE_KEY = "LocaleApp";

    public static final String LOCALE_ENGLISH = "en";
    public static final String LOCALE_SPANISH = "es";
    public static final String LOCALE_CATALAN = "ca";

    private SharedPreferences sharedPreferences;
    private String token = null;
    private boolean darkTheme = false;
    private String locale = LOCALE_SPANISH;

    private boolean localeFetched = false;

    @Inject
    public PrefManager(Application application) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        refreshDarkTheme();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * @return if user is logged or not
     */
    public boolean isLogged() {
        boolean isLogged = sharedPreferences.getBoolean(LOGGED_KEY, false);
        long expirationDate = sharedPreferences.getLong(EXPIRATION_KEY, -1);
        if (expirationDate == -1) return false;
        return System.currentTimeMillis() < expirationDate && isLogged;
    }

    public void setLogin(@NonNull TokenResponse loginData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_KEY, true);
        editor.putLong(EXPIRATION_KEY, (System.currentTimeMillis() + (loginData.getExpiresIn() * 1000)));
        editor.putString(TOKEN_KEY, loginData.getAccessToken());
        editor.putString(REFRESH_TOKEN_KEY, loginData.getRefreshToken());
        editor.apply();
    }


    public String getToken() {
        if (token == null) {
            token = sharedPreferences.getString(TOKEN_KEY, "");
        }
        return token;
    }

    /**
     * Reset values to default to indicate no one is logged in and the user has to log in
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_KEY, false);
        editor.putLong(EXPIRATION_KEY, -1);
        editor.putString(TOKEN_KEY, null);
        editor.putString(REFRESH_TOKEN_KEY, null);
        editor.apply();
    }

    public boolean isDarkThemeEnabled() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, darkTheme).apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, "");
    }

    public void refreshDarkTheme() {
        darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false);
    }

    public String getLocale() {
        return sharedPreferences.getString(LOCALE_KEY, LOCALE_SPANISH);
    }
}
