package com.victorbg.racofib.data.sp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    private SharedPreferences sharedPreferences;
    private String token = null;

    @Inject
    public PrefManager(Application application) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
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

    public void setLogin(@NonNull LoginData loginData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_KEY, true);
        editor.putLong(EXPIRATION_KEY, (System.currentTimeMillis() + (loginData.expirationTime * 1000)));
        editor.putString(TOKEN_KEY, loginData.token);
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
        editor.apply();
    }

    private static final String TOKEN_KEY = "AuthToken";
    private static final String LOGGED_KEY = "UserLogged";
    private static final String EXPIRATION_KEY = "TokenExpiration";


}
