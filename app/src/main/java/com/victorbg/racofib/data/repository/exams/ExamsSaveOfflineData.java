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

    public ExamsSaveOfflineData(AppDatabase appDatabase, AppExecutors appExecutors) {
        this.appDatabase = appDatabase;
        this.appExecutors = appExecutors;
    }

    /**
     * Save the provided list of exams into the database
     *
     * @param data
     */
    @Override
    public void saveData(List<Exam> data) {
        appExecutors.executeOnDisk(() ->
                appDatabase.runInTransaction(() -> appDatabase.examDao().insertExams(data))
        );
    }


}
