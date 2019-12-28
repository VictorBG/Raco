package com.victorbg.racofib.data.domain.subjects;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadSubjectDatabaseUseCase extends UseCase<String, LiveData<Subject>> {
    private SubjectsDao subjectsDao;

    @Inject
    public LoadSubjectDatabaseUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
        super(appExecutors);
        this.subjectsDao = appDatabase.subjectsDao();
    }

    @Override
    public LiveData<Subject> execute(String parameter) {
        return subjectsDao.getSubject(parameter);
    }
}
