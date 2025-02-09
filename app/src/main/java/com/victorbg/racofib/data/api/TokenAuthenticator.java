package com.victorbg.racofib.data.api;

import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.preferences.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {

  private final PrefManager prefManager;
  private final AuthService authService;

  public TokenAuthenticator(PrefManager prefManager, AuthService authService) {
    this.prefManager = prefManager;
    this.authService = authService;
  }

  @Nullable
  @Override
  public Request authenticate(@Nullable Route route, @NotNull Response response)
      throws IOException {

    Timber.d("Intercepted authenticate call with code: %d", response.code());
    if (response.code() == 401) {
      retrofit2.Response<TokenResponse> refreshResponse =
          authService
              .refreshToken(
                  "refresh_token",
                  prefManager.getRefreshToken(),
                  BuildConfig.RacoClientID,
                  BuildConfig.RacoSecret)
              .execute();

      if (refreshResponse != null && refreshResponse.code() == 200) {
        Timber.d("Token refreshed successfully");
        prefManager.setLogin(refreshResponse.body());
        return response
            .request()
            .newBuilder()
            .header(
                "Authorization", NetworkUtils.prepareToken(refreshResponse.body().getAccessToken()))
            .build();
      } else {
        return null;
      }
    } else {
      return response
          .request()
          .newBuilder()
          .header("Authorization", NetworkUtils.prepareToken(prefManager.getToken()))
          .build();
    }
  }
}
