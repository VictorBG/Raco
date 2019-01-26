package com.victorbg.racofib.data.repository.notes;

import android.annotation.SuppressLint;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.api.result.ApiResultData;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.repository.Repository;
import com.victorbg.racofib.data.sp.PrefManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotesRepository extends Repository<List<Note>> {

    private NotesDao notesDao;

    public NotesRepository(NotesDao notesDao, PrefManager prefManager, ApiService apiService) {
        super(prefManager, apiService);

        this.notesDao = notesDao;
    }

    public void getNotes(@NonNull ApiResult result, boolean force) {
        if (!force && !preCall()) {
            data.postValue(data.getValue());
            result.onCompleted();
            return;
        }
        apiService.getNotes(getToken(), "json").enqueue(new Callback<ApiNotesResponse>() {
            @Override
            public void onResponse(Call<ApiNotesResponse> call, Response<ApiNotesResponse> response) {
                data.setValue(response.body().result);
                internalSaveOnDatabase();
                postCall();
                result.onCompleted();
            }

            @Override
            public void onFailure(Call<ApiNotesResponse> call, Throwable t) {
                restoreAfterFail(result);
                postCall();
            }
        });
    }

    private void internalSaveOnDatabase() {
        new Thread(() -> {
            for (Note note : data.getValue()) {
                notesDao.insert(note);
            }
        }).start();
    }

    @SuppressLint("CheckResult")
    private void restoreAfterFail(@NonNull ApiResult result) {
        compositeDisposable.add(notesDao.getNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notes -> {
            data.setValue(notes);
            if (notes == null || notes.isEmpty()) result.onFailed("");
            else result.onCompleted();
        }));
    }


}
