package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.domain.subjects.LoadSingleSubjectUseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.base.Resource;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SubjectDetailViewModel extends ViewModel {


    private LiveData<Resource<Subject>> subject = null;

    private final LoadSingleSubjectUseCase loadSingleSubjectUseCase;

    @Inject
    public SubjectDetailViewModel(LoadSingleSubjectUseCase loadSingleSubjectUseCase) {
        this.loadSingleSubjectUseCase = loadSingleSubjectUseCase;
    }

    public LiveData<Resource<Subject>> getSubject(@Nullable String subject) {
        if (subject != null && this.subject == null) {
            this.subject = loadSingleSubjectUseCase.execute(subject);
        }
        return this.subject;
    }
}
