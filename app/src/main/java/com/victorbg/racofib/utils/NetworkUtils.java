package com.victorbg.racofib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

  private static final String BEARER_TOKEN_PREPEND_STRING = "Bearer ";

  /**
   * Checks if the device is online or not
   *
   * @return If the device is online or not
   */
  public static boolean isOnline(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
  }

  /**
   * Returns the provided token with the correct token type at the beginning
   *
   * @return The token correctly formatted
   */
  public static String prepareToken(String token) {
    return BEARER_TOKEN_PREPEND_STRING + token;
  }
}
