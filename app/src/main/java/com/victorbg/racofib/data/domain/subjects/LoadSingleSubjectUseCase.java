package com.victorbg.racofib.data.domain.subjects;

import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.subjects.SubjectsRepository;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadSingleSubjectUseCase extends UseCase<String, LiveData<Resource<Subject>>> {

    private final SubjectsRepository subjectsRepository;

    @Inject
    public LoadSingleSubjectUseCase(AppExecutors appExecutors, SubjectsRepository subjectsRepository) {
        super(appExecutors);
        this.subjectsRepository = subjectsRepository;
    }

    @Override
    public LiveData<Resource<Subject>> execute() {
        throw new InvalidParameterException("execute() cannot be called with invalid parameters");
    }

    @Override
    public LiveData<Resource<Subject>> execute(String parameter) {
        return subjectsRepository.getSubject(parameter);
    }
}
