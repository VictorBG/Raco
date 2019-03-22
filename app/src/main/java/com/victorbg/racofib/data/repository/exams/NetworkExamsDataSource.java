package com.victorbg.racofib.data.repository.exams;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.base.DataSource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.SaveOfflineData;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.utils.Utils;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class NetworkExamsDataSource implements DataSource<Resource<List<Exam>>> {

    private final ApiService apiService;
    private final ExamDao examDao;
    private final SaveOfflineData<List<Exam>> saveOfflineData;
    private final List<String> subjects;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    public NetworkExamsDataSource(ApiService apiService, ExamDao examDao, SaveOfflineData<List<Exam>> saveData, List<String> subjects) {
        this.apiService = apiService;
        this.examDao = examDao;
        this.saveOfflineData = saveData;
        this.subjects = subjects;
    }

    /**
     * Returns the remote data of exams.
     * <p>
     * It first gets the last semester, in order to get the last exams, and then
     * the exams of this semester with the subjects short name as filter.
     * <p>
     * Once retrieved it is stored in the database (if {@link SaveOfflineData} provided)
     * and then returns the LiveData with the current fetched values.
     *
     * @return
     * @throws RuntimeException if not {@link SaveOfflineData} has been provided
     */
    @Override
    public LiveData<Resource<List<Exam>>> getRemoteData() {
        MediatorLiveData<Resource<List<Exam>>> result = new MediatorLiveData();

        compositeDisposable.add(
                apiService.getCurrentSemester("json").flatMap(semester -> {

                    String exams = Utils.getStringSubjectsApi(subjects);

                    return apiService.getExams(semester.id, "json", exams).flatMap(data -> {
                        Utils.sortExamsList(data.result);
                        return Single.just(data.result);
                    });
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(data -> {
                            if (data != null) {
                                if (saveOfflineData != null) {
                                    saveOfflineData.saveData(data);
                                    result.postValue(Resource.success(data));
                                }
                            } else {
                                throw new RuntimeException("SaveOfflineData has not been provided");
                            }
                        }, error -> {
                            LiveData<Resource<List<Exam>>> dbSource = getOfflineData();
                            result.addSource(dbSource, dbData -> {
                                if (dbData.status != Status.LOADING) {
                                    result.removeSource(dbSource);
                                    result.setValue(dbData);
                                }
                            });
                        }));

        return result;

    }

    /**
     * Returns the offline data of the exams directly from the database.
     *
     * @return The LiveData provided by the database ready to observe
     */
    @Override
    public LiveData<Resource<List<Exam>>> getOfflineData() {
        MediatorLiveData<Resource<List<Exam>>> result = new MediatorLiveData();

        result.postValue(Resource.loading(null));

        LiveData<List<Exam>> dbSource = examDao.getExams();
        result.addSource(dbSource, dbData -> {
            //Once source emits, remove it and post the value to the returned LiveData
            result.removeSource(dbSource);
            result.postValue(Resource.success(dbData));
        });

        return result;
    }
}
