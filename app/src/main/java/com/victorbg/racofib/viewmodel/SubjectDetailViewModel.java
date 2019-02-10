package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.subjects.SubjectsRepository;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SubjectDetailViewModel extends ViewModel {


    private LiveData<Resource<Subject>> subject = null;

    private SubjectsRepository subjectsRepository;

    @Inject
    public SubjectDetailViewModel(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }


    public LiveData<Resource<Subject>> getSubject(@Nullable String subject) {
        if (subject != null && this.subject == null) {
            this.subject = subjectsRepository.getSubject(subject);
        }
        return this.subject;
    }
}
