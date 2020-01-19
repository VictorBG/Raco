package com.victorbg.racofib.domain.schedule;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.domain.UseCase;
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

@Singleton
public class LoadScheduleUseCase extends UseCase<Void, LiveData<Resource<List<SubjectSchedule>>>> {

  private final AppDatabase appDatabase;

  @Inject
  public LoadScheduleUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
    super(appExecutors);
    this.appDatabase = appDatabase;
  }

  /**
   * For every emission of subjects returns the schedule associated. It is util when the colors of the subjects changes this also emits a new schedule
   * with the correct colors.
   * <p>
   *
   * @return
   */
  @Override
  public LiveData<Resource<List<SubjectSchedule>>> execute() {
    MediatorLiveData<Resource<List<SubjectSchedule>>> result = new MediatorLiveData<>();
    result.setValue(Resource.loading(null));

    executeSingleAction(() -> appDatabase.subjectsDao().getSubjects().flatMap(subjects ->
            appDatabase.subjectScheduleDao().getSchedule().flatMap(schedule -> {
              Utils.assignColorsSchedule(subjects, schedule);
              return Single.just(schedule);
            })),
        data -> appExecutors.executeOnMainThread(() -> result.setValue(Resource.success(data))),
        error -> appExecutors.executeOnMainThread(() -> result.setValue(Resource.error(error.getMessage()))));

    return result;
  }

}
