package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.DataRepository;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.exams.Exam;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<NotesViewModel.NotesState> examsState = new MutableLiveData<>();

    DataRepository dataRepository;

    @Inject
    public HomeViewModel(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        examsState.setValue(NotesViewModel.NotesState.LOADING);
    }

    public void getExams(@NonNull ApiResult apiResult) {

    }

    public LiveData<List<Exam>> getExamsLiveData() {
        return dataRepository.getExamsLiveData();
    }

    public LiveData<NotesViewModel.NotesState> getExamStateLiveData() {
        return this.examsState;
    }
}
