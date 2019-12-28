package com.victorbg.racofib.data.domain.subjects;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton

public class SaveSubjectUseCase extends UseCase<Subject, Void> {

    private final SubjectsDao subjectsDao;

    @Inject
    public SaveSubjectUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
        super(appExecutors);
        this.subjectsDao = appDatabase.subjectsDao();
    }

    @Override
    public Void execute(Subject parameter) {
        subjectsDao.update(parameter);
        return null;
    }
}
