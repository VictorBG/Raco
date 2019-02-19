package com.victorbg.racofib.data.api;

import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.data.sp.PrefManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ApiManager {

    private static final String BASE_URL = "https://api.fib.upc.edu/v2/";

    public static ApiService create(PrefManager prefManager) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.NONE);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Timber.d("Intercepting call: %s", chain.request().toString());
                    if (chain.request().method().equals("GET")) {
                        //Don't add auth header to request that already have that header, which
                        //means the token has not been already saved into storage
                        boolean skipHeader = false;
                        for (String s : chain.request().headers().names()) {
                            if (s.toLowerCase().contains("Authorization".toLowerCase())) {
                                skipHeader = true;
                                break;
                            }
                        }
                        if (!skipHeader) {
                            Request request = chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + prefManager.getToken())
                                    .build();
                            return chain.proceed(request);
                        }
                    }
                    return chain.proceed(chain.request());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
        return retrofit.create(ApiService.class);
    }


}
