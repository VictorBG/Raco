package com.victorbg.racofib.domain.exams;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.exams.ExamsRepository;

import com.victorbg.racofib.utils.Utils;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import io.reactivex.disposables.CompositeDisposable;

@Singleton
public class LoadExamsUseCase extends UseCase<Void, LiveData<Resource<List<Exam>>>> {

  private final ExamsRepository examsRepository;
  private final AppDatabase appDatabase;

  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public LoadExamsUseCase(
      AppExecutors appExecutors, ExamsRepository examsRepository, AppDatabase appDatabase) {
    super(appExecutors);
    this.examsRepository = examsRepository;
    this.appDatabase = appDatabase;
  }

  @Override
  public LiveData<Resource<List<Exam>>> execute() {
    MediatorLiveData<Resource<List<Exam>>> result = new MediatorLiveData<>();
    result.setValue(Resource.loading(null));

    executeSingleAction(
        () -> appDatabase.subjectsDao().getSubjectsNames(),
        subjects ->
            result.addSource(
                examsRepository.getExams(subjects),
                data -> {
                  executeSingleAction(
                      () -> appDatabase.subjectsDao().getColors(),
                      colors -> {
                        Utils.assignColorsToExams(colors, data.data);
                        result.setValue(data);
                      });
                }),
        error -> result.setValue(Resource.error(error)));

    return result;
  }
}
