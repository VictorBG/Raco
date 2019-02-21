package com.victorbg.racofib.data.repository.exams;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.NetworkBoundResource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.util.NetworkRateLimiter;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.UserUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class ExamsRepository {

    private ApiService apiService;
    private ExamDao examDao;
    private PrefManager prefManager;
    private AppExecutors appExecutors;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NetworkRateLimiter rateLimiter = new NetworkRateLimiter(1, TimeUnit.HOURS);


    @Inject
    public ExamsRepository(AppExecutors appExecutors, ExamDao examDao, PrefManager prefManager, ApiService apiService) {
        this.examDao = examDao;
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<List<Exam>>> getExams(User user) {
        Timber.d("Repository getExams() called at time %d", System.currentTimeMillis());
        return new NetworkBoundResource<List<Exam>, ApiListResponse<Exam>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull ApiListResponse<Exam> item) {
                examDao.insertExams(item.result);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Exam> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch();
            }

            @NonNull
            @Override
            protected LiveData<List<Exam>> loadFromDb() {
                return examDao.getExams();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiListResponse<Exam>>> createCall() {
                return null;
            }

            @Override
            protected void fetchFromNetwork(LiveData<List<Exam>> dbSource) {
                //result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
                compositeDisposable.add(apiService.getCurrentSemester("json").flatMap(semester -> {

                    String exams = UserUtils.getStringSubjectsApi(user.subjects);

                    return apiService.getExams(semester.id, "json", exams).flatMap(result -> {
                        UserUtils.sortExamsList(result.result);
                        return Single.just(result.result);
                    });
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                        .subscribe(objects -> {
                            appExecutors.diskIO().execute(() -> examDao.insertExams(objects));
                            setValue(Resource.success(objects));
                        }, error -> {
                            Timber.d(error);
                            result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
                        }));
            }
        }.getAsLiveData();
    }
}
