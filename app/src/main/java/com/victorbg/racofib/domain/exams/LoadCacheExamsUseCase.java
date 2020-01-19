package com.victorbg.racofib.domain.exams;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadCacheExamsUseCase extends UseCase<Void, LiveData<List<Exam>>> {

    private ExamDao examDao;

    @Inject
    public LoadCacheExamsUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
        super(appExecutors);
        this.examDao = appDatabase.examDao();
    }

    @Override
    public LiveData<List<Exam>> execute() {
        return examDao.getExams();
    }

}
