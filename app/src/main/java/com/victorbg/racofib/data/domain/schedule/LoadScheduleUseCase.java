package com.victorbg.racofib.data.domain.schedule;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.utils.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class LoadScheduleUseCase extends UseCase<Void, LiveData<Resource<List<SubjectSchedule>>>> {

    private AppDatabase appDatabase;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public LoadScheduleUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
        super(appExecutors);
        this.appDatabase = appDatabase;
    }

    /**
     * For every emission of subjects returns the schedule associated. It it util when the colors of the
     * subjects changes this also emits a new schedule with the correct colors
     *
     * @return
     */
    @Override
    public LiveData<Resource<List<SubjectSchedule>>> execute() {
        MediatorLiveData<Resource<List<SubjectSchedule>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));

        appExecutors.diskIO().execute(() ->
                compositeDisposable.add(appDatabase.subjectsDao().getSubjects().flatMap(subjects ->
                        appDatabase.subjectScheduleDao().getSchedule().flatMap(schedule -> {
                            Utils.assignColorsSchedule(subjects, schedule);
                            return Single.just(schedule);
                        }))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(data -> {
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.success(data)));
                        }, error -> {
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.error(error.getMessage(), null)));
                        })));

        return result;
    }

    @Override
    public LiveData<Resource<List<SubjectSchedule>>> execute(Void parameter) {
        return execute();
    }
}
