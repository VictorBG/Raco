package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.api.ApiResponse;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 *
 * @param <R>
 */
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {
  private final Type responseType;

  public LiveDataCallAdapter(Type responseType) {
    this.responseType = responseType;
  }

  @Override
  public Type responseType() {
    return responseType;
  }

  @Override
  public LiveData<ApiResponse<R>> adapt(Call<R> call) {
    return new LiveData<ApiResponse<R>>() {
      final AtomicBoolean started = new AtomicBoolean(false);

      @Override
      protected void onActive() {
        super.onActive();
        if (started.compareAndSet(false, true)) {
          call.enqueue(
              new Callback<R>() {
                @Override
                public void onResponse(Call<R> call, Response<R> response) {
                  postValue(new ApiResponse<>(response));
                }

                @Override
                public void onFailure(Call<R> call, Throwable throwable) {
                  postValue(new ApiResponse<R>(throwable));
                }
              });
        }
      }
    };
  }
}
