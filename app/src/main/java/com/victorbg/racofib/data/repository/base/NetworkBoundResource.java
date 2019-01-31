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

    protected MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    protected AppExecutors appExecutors;

    @MainThread
    protected NetworkBoundResource(AppExecutors appExecutors) {

        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            Timber.d("Fetching data");
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
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

        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            if (response.isSuccessful()) {

                //Save response from network into database
                appExecutors.diskIO().execute(() -> saveCallResult(processResponse(response)));

                //And request a new live data from which whe will observe the new data inserted into database, remember, only db
                //is a trusted source
                appExecutors.mainThread().execute(() -> result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData))));
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData -> setValue(Resource.error(response.errorMessage, newData)));
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

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @MainThread
    protected void onFetchFailed() {
    }

    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }
}
