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

    /**
     * Loads a single {@link Subject} from the database with parameter as filter
     *
     * @param parameter
     * @return
     */
    @Override
    public LiveData<Resource<Subject>> execute(String parameter) {
        return subjectsRepository.getSubject(parameter);
    }
}
