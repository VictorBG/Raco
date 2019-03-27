package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.subjects.LoadSubjectDatabaseUseCase;
import com.victorbg.racofib.data.domain.subjects.LoadSubjectsUseCase;
import com.victorbg.racofib.data.domain.subjects.SaveSubjectUseCase;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.utils.ConsumableBoolean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GradesViewModel extends ViewModel {

    private final MediatorLiveData<List<Subject>> subjects = new MediatorLiveData<>();
    private final MediatorLiveData<Subject> subject = new MediatorLiveData<>();

    private LiveData<Subject> currentSource;

    private SaveSubjectUseCase saveSubjectUseCase;
    private LoadSubjectDatabaseUseCase loadSubjectDatabaseUseCase;

    private int indexSelected = 0;

    @Inject
    public GradesViewModel(LoadSubjectsUseCase loadSubjectsUseCase, SaveSubjectUseCase saveSubjectUseCase, LoadSubjectDatabaseUseCase loadSubjectDatabaseUseCase) {
        this.saveSubjectUseCase = saveSubjectUseCase;
        this.loadSubjectDatabaseUseCase = loadSubjectDatabaseUseCase;

        LiveData<List<Subject>> subjects = loadSubjectsUseCase.execute();
        this.subjects.addSource(subjects, data -> {
//            this.subjects.removeSource(subjects);

            //Check if the list are equal
            if (this.subjects.getValue() != null) {
                List<Subject> copy = new ArrayList<>(data);
                copy.retainAll(this.subjects.getValue());
                if (copy.size() != 0) {
                    this.subjects.setValue(data);
                }
            } else {
                this.subjects.setValue(data);
            }
            selectSubject(data.get(indexSelected).shortName);
        });

    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public MutableLiveData<Subject> getSubject() {
        return subject;
    }

    public void selectSubject(String subject) {
        if (currentSource != null) {
            this.subject.removeSource(currentSource);
        }
        this.currentSource = loadSubjectDatabaseUseCase.execute(subject);
        this.subject.addSource(this.currentSource, this.subject::setValue);
    }

    public void selectSubject(int index) {
        if (subjects.getValue() == null || subjects.getValue().size() <= index) {
            return;
        }
        this.indexSelected = index;
        selectSubject(subjects.getValue().get(this.indexSelected).shortName);
    }

}
