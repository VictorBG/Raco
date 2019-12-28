package com.victorbg.racofib.data.domain.schedule;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class LoadTodayScheduleUseCase extends UseCase<Void, LiveData<Resource<List<SubjectSchedule>>>> {

  private final AppDatabase appDatabase;
  private PrefManager prefManager;
  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public LoadTodayScheduleUseCase(AppExecutors appExecutors, AppDatabase appDatabase, PrefManager prefManager) {
    super(appExecutors);
    this.appDatabase = appDatabase;
    this.prefManager = prefManager;
  }

  @Override
  public LiveData<Resource<List<SubjectSchedule>>> execute() {
    MutableLiveData<Resource<List<SubjectSchedule>>> result = new MutableLiveData<>();
    result.setValue(Resource.loading(null));

    if (prefManager.shouldDisplaySchedule()) {
      appExecutors.diskIO().execute(() -> {
        compositeDisposable.add(appDatabase
            .subjectScheduleDao()
            .getTodaySchedule(Utils.getDayOfWeek())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                data -> appExecutors.executeOnMainThread(() -> result.setValue(Resource.success(data))),
                error -> appExecutors.executeOnMainThread(() -> result.setValue(Resource.error(error.getMessage())))));
      });
    }

    return result;
  }

  @Override
  public LiveData<Resource<List<SubjectSchedule>>> execute(Void parameter) {
    return execute();
  }
}
