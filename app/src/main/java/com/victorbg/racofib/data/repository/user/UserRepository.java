package com.victorbg.racofib.data.repository.user;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;
import com.victorbg.racofib.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class UserRepository {

  private final ApiService apiService;
  private final UserDao userDao;
  private final SubjectsDao subjectsDao;
  private final SubjectScheduleDao subjectScheduleDao;
  private final AppDatabase appDatabase;
  private final PrefManager prefManager;
  private final AppExecutors appExecutors;

  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

  @Inject
  public UserRepository(AppExecutors appExecutors, AppDatabase appDatabase, PrefManager prefManager, ApiService apiService) {
    this.userDao = appDatabase.userDao();
    this.appDatabase = appDatabase;
    this.prefManager = prefManager;
    this.apiService = apiService;
    this.appExecutors = appExecutors;
    this.subjectsDao = appDatabase.subjectsDao();
    this.subjectScheduleDao = appDatabase.subjectScheduleDao();

    userMutableLiveData.setValue(null);
  }

  /**
   * Gets the user from the database {@link User} object
   *
   * @return the fetched {@link User} inside a {@link LiveData}
   */
  public LiveData<User> getUser() {
    if (userMutableLiveData.getValue() == null) {
      appExecutors.diskIO().execute(() ->
          compositeDisposable.add(
              userDao.getUser().subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(user ->
                          appExecutors.mainThread().execute(() -> userMutableLiveData.setValue(user))
                      , Timber::d))
      );
    }
    return userMutableLiveData;
  }

  /**
   * Authenticates the user with the provided code which is should be returned by the API.
   * <p>
   * Once the user has been authorized successfully it downloads all the necessary data for the app to run like {@link User}, the {@link
   * com.victorbg.racofib.data.model.subject.Subject}s and the {@link SubjectSchedule} which indicates the schedule of the user.
   *
   * @param context {@link Context to get the texts}
   * @param code    {@link String} code returned by the API to auth the user
   * @return {@link LiveData} with the state of the login and a message
   * <p>
   * TODO: LoadUserInfoUseCase preserving the current state of the databases
   */
  public LiveData<Resource<String>> authUser(Context context, String code) {

    MutableLiveData<Resource<String>> loadingStatus = new MutableLiveData<>();
    compositeDisposable.add(loadToken(context, code, loadingStatus)
        .subscribeOn(Schedulers.io())
        .subscribe(tokenResponse -> {
          getUser();
          prefManager.setLogin(tokenResponse);
          appExecutors.mainThread().execute(() -> loadingStatus.setValue(Resource.success(null)));
        }, error -> appExecutors.mainThread()
            .execute(() -> Toast.makeText(context, "An error has occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show())));

    return loadingStatus;
  }

  public LiveData<Resource<String>> reloadUser(Context context) {

    appDatabase.runInTransaction(() -> {
      appDatabase.runInTransaction(appDatabase::clearAllTables);
    });

    MutableLiveData<Resource<String>> loadingStatus = new MutableLiveData<>();
    compositeDisposable.add(loadUser(context, getToken(), loadingStatus)
        .subscribeOn(Schedulers.io())
        .subscribe(tokenResponse -> {
          getUser();
          appExecutors.mainThread().execute(() -> loadingStatus.setValue(Resource.success(null)));
        }, error -> appExecutors.mainThread()
            .execute(() -> Toast.makeText(context, "An error has occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show())));

    return loadingStatus;
  }


  private Single<TokenResponse> loadToken(Context context, String code,
      MutableLiveData<Resource<String>> result) {
    return apiService.getAccessToken(
        "authorization_code",
        code,
        BuildConfig.RacoRedirectUrl,
        BuildConfig.RacoClientID,
        BuildConfig.RacoSecret
    ).flatMap(token -> loadUser(context, token, result));
  }

  private Single<TokenResponse> loadUser(Context context, TokenResponse token,
      MutableLiveData<Resource<String>> result) {
    setUIMessage(context, result, R.string.fetching_user_message);
    return apiService.getUser(getToken(token), "json")
        .doOnSuccess(user -> appExecutors.diskIO().execute(() -> userDao.insert(user)))
        .flatMap(user -> loadSubjects(context, token, result));
  }

  private Single<TokenResponse> loadSubjects(Context context, TokenResponse token,
      MutableLiveData<Resource<String>> result) {
    setUIMessage(context, result, R.string.fetching_subjects_message);

    return apiService.getSubjects(getToken(token), "json")
        .map(s -> s.result)
        .doOnSuccess(subjects -> {
          Utils.assignRandomColors(context, subjects);
          retrieveGrades(subjects);
          appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects));
        })
        .flatMap(subjects -> loadSubjectsSchedule(context, token, result));
  }

  private Single<TokenResponse> loadSubjectsSchedule(Context context, TokenResponse token,
      MutableLiveData<Resource<String>> result) {
    setUIMessage(context, result, R.string.fetching_schedule_message);

    return apiService.getSubjectsSchedule(getToken(token), "json")
        .map(t -> t.result)
        .doOnSuccess(timetable -> {
          Optional.of(timetable)
              .ifPresent(t -> appExecutors.diskIO().execute(() -> subjectScheduleDao.insert(t)));
          setUIMessage(context, result, R.string.last_steps_login_loading);
        })
        .flatMap(timetable -> Single.just(token));
  }

  private void setUIMessage(Context context, MutableLiveData<Resource<String>> result,
      @StringRes int id) {
    appExecutors.mainThread()
        .execute(() -> result.setValue(Resource.loading(context.getString(id))));
  }

  private void retrieveGrades(List<Subject> subjects) {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    subjects.forEach(s -> {
      db.collection("subjects").document(s.shortName).get().addOnCompleteListener(task -> {
        Optional.ofNullable(task).ifPresent((d) -> {
          Optional.ofNullable(task.getResult().getData()).ifPresent(map -> {
            s.grades = map
                .keySet()
                .stream()
                .map(key -> new Grade(key, (Double) map.get(key) * 100))
                .collect(Collectors.toList());
          });
          appExecutors.diskIO().execute(() -> subjectsDao.insert(s));
        });
      });
    });
  }

  private String getToken(TokenResponse tokenResponse) {
    return NetworkUtils.prepareToken(tokenResponse.getAccessToken());
  }

  private TokenResponse getToken() {
    return new TokenResponse(prefManager.getToken());
  }

}
