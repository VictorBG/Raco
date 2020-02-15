package com.victorbg.racofib.domain.schedule;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.preferences.PrefManager;
import com.victorbg.racofib.utils.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

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
      executeSingleAction(() -> appDatabase.subjectScheduleDao().getTodaySchedule(Utils.getDayOfWeek()),
          data -> result.setValue(Resource.success(data)),
          error -> result.setValue(Resource.error(error)));
    }

    return result;
  }

}
