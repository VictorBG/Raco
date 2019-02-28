package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.subjects.LoadSubjectsUseCase;
import com.victorbg.racofib.data.model.subject.Subject;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


public class SubjectsViewModel extends ViewModel {

    private LiveData<List<Subject>> subjects;

    @Inject
    public SubjectsViewModel(LoadSubjectsUseCase loadSubjectsUseCase) {
        this.subjects = loadSubjectsUseCase.execute();
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }
}
