package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.DataRepository;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.api.result.ApiResultData;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.model.Note;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NotesViewModel extends ViewModel implements ApiResult {

    //TODO: this is an anti-pattern, why I have done this, I only can expose an immutable variable
    public MutableLiveData<NotesState> notesState = new MutableLiveData<>();

    @Inject
    DataRepository dataRepository;

    @Inject
    public NotesViewModel(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        notesState.setValue(NotesState.LOADING);
    }


    public void reload(boolean force) {
        notesState.setValue(NotesState.LOADING);
        dataRepository.getNotes(this, force);
    }

    @Override
    public void onCompleted() {
        if (getLiveData().getValue() == null || getLiveData().getValue().isEmpty()) {
            notesState.setValue(NotesState.EMPTY);
        } else {
            notesState.setValue(NotesState.LOADED);
        }
    }

    @Override
    public void onFailed(String errorMessage) {
        notesState.setValue(NotesState.ERROR);
    }


    public enum NotesState {
        LOADING,
        ERROR,
        LOADED,
        EMPTY
    }

    public LiveData<List<Note>> getLiveData() {
        return dataRepository.getNotesLiveData();
    }

}
