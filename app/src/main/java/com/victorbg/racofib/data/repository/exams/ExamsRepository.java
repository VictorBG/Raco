package com.victorbg.racofib.data.repository.exams;

import android.app.Application;
import android.content.Context;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Repository;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.network.RateLimiter;
import com.victorbg.racofib.utils.NetworkUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

@Singleton
public class ExamsRepository extends Repository {

    private final ApiService apiService;
    private final ExamDao examDao;
    private final AppExecutors appExecutors;
    private final AppDatabase appDatabase;
    private final Context context;


    private final RateLimiter networkRateLimiter = new RateLimiter(1, TimeUnit.HOURS);
    private final RateLimiter databaseRateLimiter = new RateLimiter(15, TimeUnit.MINUTES);

    private LiveData<Resource<List<Exam>>> examCache = null;


    @Inject
    public ExamsRepository(AppExecutors appExecutors, AppDatabase appDatabase, ApiService apiService, Application context) {
        this.examDao = appDatabase.examDao();
        this.appDatabase = appDatabase;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.context = context;
    }

    public LiveData<Resource<List<Exam>>> getExams(List<String> subjects) {
        if (subjects.size() == 0) {
            MutableLiveData result = new MutableLiveData();
            result.setValue(Resource.error("Subjects equal 0", null));
            examCache = result;
            return examCache;
        }
        NetworkExamsDataSource examsDataSource = new NetworkExamsDataSource(apiService, examDao, new ExamsSaveOfflineData(appDatabase, appExecutors), subjects);
        if (examCache == null || NetworkUtils.isOnline(context) && networkRateLimiter.shouldFetch()) {
            databaseRateLimiter.shouldFetch();
            examCache = examsDataSource.getRemoteData();
        } else if (examCache == null || databaseRateLimiter.shouldFetch()) {
            examCache = examsDataSource.getOfflineData();
        }
        return examCache;
    }

    @Override
    public void clean() {
        examCache = null;
        networkRateLimiter.reset();
        databaseRateLimiter.reset();
    }
}
