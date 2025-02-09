package com.victorbg.racofib.data.repository.subjects;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class SubjectsRepository {

  private final AppExecutors appExecutors;
  private final ApiService apiService;

  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public SubjectsRepository(AppExecutors appExecutors, ApiService apiService) {
    this.apiService = apiService;
    this.appExecutors = appExecutors;
  }

  /**
   * Returns a Resource with the state of the desired subject, which is retrieved from the API every
   * time it is called and it is not saved in the local database, is not considered a critial
   * information to know even when internet is not available.
   *
   * @param subject the subject to retrieve
   * @return {@link Resource} indicating the state of the subject
   */
  public LiveData<Resource<Subject>> getSubject(String subject) {
    MutableLiveData<Resource<Subject>> result = new MutableLiveData<>();
    result.setValue(Resource.loading(null));

    appExecutors.executeOnNetwork(
        () ->
            compositeDisposable.add(
                apiService
                    .getSubject(subject)
                    .flatMap(s -> Single.just(SubjectProcessor.processSubject(s)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        s -> result.postValue(Resource.success(s)),
                        error -> {
                          Timber.d(error);
                          result.postValue(Resource.error(error));
                        })));

    return result;
  }
}
