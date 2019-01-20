package com.victorbg.racofib.data.sp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.victorbg.racofib.model.login.LoginData;

import androidx.annotation.NonNull;

/**
 * Class to manage the preferences of the application using a Singleton
 * pattern. It is initialized in a secure way to prevent duplicate on more
 * than one thread.
 * <p>
 * TODO: Singleton initialization must be replaced with @Singleton pattern of Dagger thus involving di.
 * <p>
 * <p>
 * TODO: Tests are not ready yet for this class.
 */
public class PrefManager {
    private static volatile PrefManager ourInstance;

    public static void initialize(Application application) {
        if (ourInstance == null) {
            ourInstance = new PrefManager(application);
        }
    }

    //Lazy init not used in the project as it is initialize on application load
    public synchronized static PrefManager getInstance(Application application) {
        if (ourInstance == null) {
            synchronized (PrefManager.class) {
                ourInstance = new PrefManager(application);
            }
        }
        return ourInstance;
    }

    public synchronized static PrefManager getInstance() {
        if (ourInstance == null) {
            throw new RuntimeException("PrefManager must be initialized with an application context");
        }
        return ourInstance;
    }

    private SharedPreferences sharedPreferences;


    private PrefManager() {
    }

    private PrefManager(Application application) {
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
        return sharedPreferences.getString(TOKEN_KEY, null);
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
