package com.victorbg.racofib.data.repository;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.sp.PrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

public abstract class Repository<T> {

    private long gap = TimeUnit.MINUTES.toMillis(5);

    private long lastCallTime = -1;

    private long notAvailableUntil = -1;

    protected MutableLiveData<T> data = new MutableLiveData<>();

    protected PrefManager prefManager;

    protected ApiService apiService;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String token = null;

    public Repository(PrefManager prefManager, ApiService apiService) {
        this.apiService = apiService;
        this.prefManager = prefManager;
        token = prefManager.getToken();
    }

    /**
     * Returns if the call should be made or not based on different
     * things.
     * <p>
     * Tbh it is only based on the last time it was fetched
     */
    protected boolean preCall() {
        return notAvailableUntil == -1 || data.getValue() == null || System.currentTimeMillis() >= notAvailableUntil;
    }

    protected void setGap(@FloatRange(from = 1, to = 60) float gap) {
        this.gap = TimeUnit.MINUTES.toMillis(5);
        this.notAvailableUntil = lastCallTime + this.gap;
    }

    protected void postCall() {
        lastCallTime = System.currentTimeMillis();
        this.notAvailableUntil = lastCallTime + gap;
    }

    protected String getToken() {
        if (token == null) prefManager.getToken();
        return "Bearer " + token;
    }

    //Expose LiveData as we don't want to expose a mutable variable (LiveData is not mutable)
    public LiveData<T> getLiveData() {
        return data;
    }


}
