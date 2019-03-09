package com.victorbg.racofib.data.repository.exams;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.SaveOfflineData;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ExamsSaveOfflineData implements SaveOfflineData<List<Exam>> {

    private final AppDatabase appDatabase;
    private final AppExecutors appExecutors;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ExamsSaveOfflineData(AppDatabase appDatabase, AppExecutors appExecutors) {
        this.appDatabase = appDatabase;
        this.appExecutors = appExecutors;
    }

    @Override
    public void saveData(List<Exam> data) {
        appExecutors.diskIO().execute(() ->
                appDatabase.runInTransaction(() -> appDatabase.examDao().insertExams(data))
        );
    }


}
