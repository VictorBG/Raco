package com.victorbg.racofib.data.api;

import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.data.preferences.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

  private static final String BASE_URL = "https://api.fib.upc.edu/v2/";

  /**
   * Creates the AuthService used for refreshing the token
   *
   * @return {@link AuthService} created
   */
  public static AuthService createAuthService() {
    Retrofit retrofit =
        new Retrofit.Builder()
            .client(new OkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    return retrofit.create(AuthService.class);
  }

  /**
   * Creates the ApiService that is used for every network call except {@link
   * AuthService#refreshToken(String, String, String, String)}.
   *
   * <p>It also has attached an interceptor to add the Bearer token automatically and an {@link
   * okhttp3.Authenticator} to refresh the token when needed.
   *
   * <p>It also adds the language to be retrieved as a header.
   *
   * @param prefManager
   * @param tokenAuthenticator
   * @return {@link ApiService} created
   */
  public static ApiService create(PrefManager prefManager, TokenAuthenticator tokenAuthenticator) {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.NONE);

    OkHttpClient client =
        new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .addInterceptor(
                chain -> {
                  if (chain.request().method().equals("GET")) {
                    // Don't add auth header to request that already have that header, which
                    // means the token has not been already saved into storage
                    boolean skipHeader = false;
                    for (String s : chain.request().headers().names()) {
                      if (s.toLowerCase().contains("Authorization".toLowerCase())) {
                        skipHeader = true;
                        break;
                      }
                    }
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Accept-Language", prefManager.getLocale());
                    if (!skipHeader) {
                      requestBuilder.addHeader(
                          "Authorization", NetworkUtils.prepareToken(prefManager.getToken()));
                    }
                    return chain.proceed(requestBuilder.build());
                  }
                  return chain.proceed(chain.request());
                })
            .authenticator(tokenAuthenticator)
            .build();

    Retrofit retrofit =
        new Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(new LiveDataCallAdapterFactory())
            .build();
    return retrofit.create(ApiService.class);
  }
}
