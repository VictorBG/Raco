package com.victorbg.racofib.data.domain.exams;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.domain.UseCase;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class LoadExamsUseCase extends UseCase<Void, LiveData<Resource<List<Exam>>>> {


  private final ExamsRepository examsRepository;
  private final AppDatabase appDatabase;

  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public LoadExamsUseCase(AppExecutors appExecutors, ExamsRepository examsRepository, AppDatabase appDatabase) {
    super(appExecutors);
    this.examsRepository = examsRepository;
    this.appDatabase = appDatabase;
  }

  @Override
  public LiveData<Resource<List<Exam>>> execute() {
    MediatorLiveData<Resource<List<Exam>>> result = new MediatorLiveData<>();
    result.setValue(Resource.loading(null));

    executeSingleAction(() -> appDatabase.subjectsDao().getSubjectsNames(),
        subjects -> {
          result.addSource(examsRepository.getExams(subjects), data -> {
            executeSingleAction(() -> appDatabase.subjectsDao().getColors(),
                colors -> {
                  Utils.assignColorsToExams(colors, data.data);
                  result.setValue(data);
                });
          });
        }, error -> result.setValue(Resource.error(error)));

//    appExecutors.executeOnDisk(() ->
//        compositeDisposable.add(appDatabase.subjectsDao().getSubjectsNames()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe(subjects -> {
//              result.addSource(examsRepository.getExams(subjects), data -> {
//                compositeDisposable.add(appDatabase.subjectsDao().getColors()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(colors -> {
//                      Utils.assignColorsToExams(colors, data.data);
//                      appExecutors.executeOnMainThread(() -> result.setValue(data));
//                    }));
//              });
//            }, error -> appExecutors.executeOnMainThread(() -> result.setValue(Resource.error(error.getMessage())))))
//    );
    return result;
  }
}
