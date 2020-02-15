package com.victorbg.racofib.data.repository.base;

import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.repository.AppExecutors;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import timber.log.Timber;

public abstract class NetworkBoundResource<ResultType, RequestType> {

  protected final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

  protected final AppExecutors appExecutors;

  @MainThread
  protected NetworkBoundResource(AppExecutors appExecutors) {

    this.appExecutors = appExecutors;
    LiveData<ResultType> dbSource = loadFromDb();

    if (preShouldFetch()) {
      result.setValue(Resource.loading(null));
    } else {
      result.addSource(dbSource, data -> setValue(Resource.success(data)));
      return;
    }

    result.addSource(
        dbSource,
        data -> {
          Timber.d("Fetching data");
          result.removeSource(dbSource);
          if (shouldFetch(data)) {
            Timber.d("Should fetch returned true, fetching from network");
            fetchFromNetwork(dbSource);
          } else {
            result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
          }
        });
  }

  @MainThread
  protected void setValue(Resource<ResultType> newValue) {
    if (result.getValue() != newValue) {
      result.setValue(newValue);
    }
  }

  protected void fetchFromNetwork(LiveData<ResultType> dbSource) {
    Timber.d("Fetching from network");
    LiveData<ApiResponse<RequestType>> apiResponse = createCall();
    result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));

    result.addSource(
        apiResponse,
        response -> {
          result.removeSource(apiResponse);
          result.removeSource(dbSource);
          if (response.isSuccessful()) {

            // Save response from network into database
            appExecutors.executeOnDisk(() -> saveCallResult(processResponse(response)));

            // And request a new live data from which we will observe the new data inserted into
            // database, remember, only db
            // is a trusted source
            appExecutors.executeOnMainThread(
                () ->
                    result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData))));
          } else {
            onFetchFailed();
            result.addSource(
                dbSource, newData -> setValue(Resource.error(response.errorMessage, newData)));
          }
        });
  }

  @WorkerThread
  protected RequestType processResponse(ApiResponse<RequestType> response) {
    return response.body;
  }

  @WorkerThread
  protected abstract void saveCallResult(@NonNull RequestType item);

  @MainThread
  protected abstract boolean shouldFetch(@Nullable ResultType data);

  /**
   * Returns if the prefecth has to be done or we can pass the result directly without loading state
   *
   * @return
   */
  @MainThread
  protected boolean preShouldFetch() {
    return true;
  }

  @NonNull
  @MainThread
  protected abstract LiveData<ResultType> loadFromDb();

  @NonNull
  @MainThread
  protected abstract LiveData<ApiResponse<RequestType>> createCall();

  /** Called when a fetch has failed */
  @MainThread
  protected void onFetchFailed() {}

  public final LiveData<Resource<ResultType>> getAsLiveData() {
    return result;
  }
}
