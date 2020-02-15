package com.victorbg.racofib.data.repository.user;


import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.background.season.SeasonCheckTask;
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
import com.victorbg.racofib.data.repository.base.functions.Function;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;
import com.victorbg.racofib.utils.Utils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * TODO: This repository is a mess and bad structured, it even has functionalities that should not be here...
 */
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

  private UIMessaging uiMessaging;

  @Inject
  public UserRepository(AppExecutors appExecutors, AppDatabase appDatabase, PrefManager prefManager, ApiService apiService) {
    this.userDao = appDatabase.userDao();
    this.appDatabase = appDatabase;
    this.prefManager = prefManager;
    this.apiService = apiService;
    this.appExecutors = appExecutors;
    this.subjectsDao = appDatabase.subjectsDao();
    this.subjectScheduleDao = appDatabase.subjectScheduleDao();

    userMutableLiveData.postValue(null);
  }

  /**
   * Gets the user from the database {@link User} object
   *
   * @return the fetched {@link User} inside a {@link LiveData}
   */
  public LiveData<User> getUser() {
    if (userMutableLiveData.getValue() == null) {
      appExecutors.executeOnDisk(() -> compositeDisposable.add(userDao.getUser()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(user -> appExecutors.executeOnMainThread(() -> userMutableLiveData.setValue(user)), Timber::d))
      );
    }
    return userMutableLiveData;
  }

  public LiveData<Resource<String>> reloadUser(Context context) {
    MutableLiveData<Resource<String>> loadingStatus = new MutableLiveData<>();
    this.uiMessaging = new UIMessaging(context, appExecutors, loadingStatus);

    appDatabase.runInTransaction(() -> appDatabase.runInTransaction(appDatabase::clearAllTables));

    compositeDisposable.add(loadUser(getToken())
        .subscribeOn(Schedulers.io())
        .subscribe(tokenResponse -> {
          this.uiMessaging.close();
          getUser();
          appExecutors.executeOnMainThread(() -> loadingStatus.setValue(Resource.success(null)));
        }, error -> appExecutors
            .executeOnMainThread(() -> Toast.makeText(context, "An error has occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show())));

    return loadingStatus;
  }

  /**
   * Authenticates the user with the provided code which should be returned by the API.
   * <p>
   * Once the user has been authorized successfully it downloads all the necessary data for the app to run like {@link User}, the {@link
   * com.victorbg.racofib.data.model.subject.Subject}s and the {@link SubjectSchedule} which indicates the schedule of the user.
   *
   * @param context {@link Context to get the texts}
   * @param code    {@link String} code returned by the API to auth the user
   * @return {@link LiveData} with the state of the login and a message
   * <p>
   */
  public LiveData<Resource<String>> authUser(Context context, String code) {
    MutableLiveData<Resource<String>> loadingStatus = new MutableLiveData<>();
    this.uiMessaging = new UIMessaging(context, appExecutors, loadingStatus);

    compositeDisposable.add(
        loadToken(code)
            .subscribeOn(Schedulers.io())
            .subscribe(tokenResponse -> {
              this.uiMessaging.close();
              getUser();
              prefManager.setLogin(tokenResponse);
              appExecutors.executeOnMainThread(() -> loadingStatus.setValue(Resource.success(null)));
              WorkManager.getInstance().enqueueUniquePeriodicWork(
                  "SeasonCheckTask",
                  ExistingPeriodicWorkPolicy.REPLACE,
                  new PeriodicWorkRequest.Builder(SeasonCheckTask.class, 7, TimeUnit.DAYS).build());
            }, error -> appExecutors
                .executeOnMainThread(() -> Toast.makeText(context, "An error has occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show())));

    return loadingStatus;
  }

  private Single<TokenResponse> loadToken(String code) {
    return apiService.getAccessToken(
        "authorization_code",
        code,
        BuildConfig.RacoRedirectUrl,
        BuildConfig.RacoClientID,
        BuildConfig.RacoSecret
    ).flatMap(this::loadUser);
  }

  private Single<TokenResponse> loadUser(TokenResponse token) {
    setUIMessage(R.string.fetching_user_message);

    return apiService.getUser(getToken(token))
        .doOnSuccess(user -> saveOnDisk(user, userDao::insert))
        .flatMap(user -> loadSubjects(token));
  }

  private Single<TokenResponse> loadSubjects(TokenResponse token) {
    setUIMessage(R.string.fetching_subjects_message);

    return apiService.getSubjects(getToken(token))
        .map(s -> s.result)
        .doOnSuccess(subjects -> {
          Utils.assignRandomColors(subjects);
          retrieveGrades(subjects);
          saveOnDisk(subjects, subjectsDao::insert);
        })
        .flatMap(subjects -> loadSubjectsSchedule(token));
  }

  private Single<TokenResponse> loadSubjectsSchedule(TokenResponse token) {
    setUIMessage(R.string.fetching_schedule_message);

    return apiService.getSubjectsSchedule(getToken(token))
        .map(t -> t.result)
        .doOnSuccess(timetable -> {
          Optional.of(timetable).ifPresent(t -> saveOnDisk(t, subjectScheduleDao::insert));
          setUIMessage(R.string.last_steps_login_loading);
        })
        .flatMap(timetable -> Single.just(token));
  }


  // TODO: Move to SubjectRepository
  private void retrieveGrades(List<Subject> subjects) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference subjectsCollection = db.collection("subjects");

    subjects.forEach(s ->
        subjectsCollection.document(s.shortName).get().addOnCompleteListener(task ->
            Optional.ofNullable(task).ifPresent((t) -> {
              Optional.ofNullable(t.getResult().getData()).ifPresent(map ->
                  s.grades = map
                      .keySet()
                      .stream()
                      .map(key -> new Grade(key, (Double) map.get(key) * 100))
                      .collect(Collectors.toList()));

              saveOnDisk(s, subjectsDao::insert);
            })));
  }

  private String getToken(TokenResponse tokenResponse) {
    return NetworkUtils.prepareToken(tokenResponse.getAccessToken());
  }

  private TokenResponse getToken() {
    return new TokenResponse(prefManager.getToken());
  }

  private void setUIMessage(@StringRes int id) {
    Optional.ofNullable(uiMessaging).ifPresent(m -> m.setUIMessage(id));
  }

  /**
   * Executes a special command for the UserRepository. This command is composed by the general command (the API call) and by a success callback that
   * is executed once the command has finished.
   *
   * @param command     Command to execute
   * @param doOnSuccess Command to execute when on success
   * @param <A>         Inferred type
   * @return A single containing the value of the call
   */
  private <A> Single<A> executeCommandNoMap(@NonNull Function<Single<A>, ?> command, @NonNull Function<Single<A>, A> doOnSuccess) {
    ObjectHelper.requireNonNull(command, "command is null");
    ObjectHelper.requireNonNull(doOnSuccess, "doOnSuccess is null");

    return command.run(null).doOnSuccess(doOnSuccess::run);
  }

  /**
   * Executes a special command for the UserRepository. This command is composed by the general command (the API call), a success callback that is
   * executed once the command has finished and a flatMap that is executed before returning the value
   *
   * @param command     Command to execute
   * @param doOnSuccess Command to execute when on success
   * @param mapFunction Command to execute for mapping
   * @return A single containing the value of the call
   */
  private <A, B> Single<B> executeCommandMap(@NonNull Function<Single<A>, ?> command, @NonNull Function<Single<A>, A> doOnSuccess,
      @NonNull Function<Single<B>, A> mapFunction) {
    return executeCommandNoMap(command, doOnSuccess).flatMap(mapFunction::run);
  }

  /**
   * Saves on disk the given data and returns a single with the data
   *
   * @param data     Data to save
   * @param consumer Function to save
   * @param <T>      Inferred type
   * @return A single containing the data
   */
  private <T> void saveOnDisk(T data, Consumer<T> consumer) {
    appExecutors.executeOnDisk(() -> consumer.accept(data));
  }

  private static class UIMessaging {

    private Context context;
    private AppExecutors appExecutors;
    private MutableLiveData<Resource<String>> loadingStatus;

    public UIMessaging(Context context, AppExecutors appExecutors, MutableLiveData<Resource<String>> loadingStatus) {
      this.context = context;
      this.appExecutors = appExecutors;
      this.loadingStatus = loadingStatus;
    }

    private void setUIMessage(@StringRes int id) {
      appExecutors.executeOnMainThread(() -> loadingStatus.setValue(Resource.loading(context.getString(id))));
    }

    /**
     * Delete references to context or appExecutors, so GC can remove this
     */
    public void close() {
      context = null;
      appExecutors = null;
    }

  }

}
