package com.victorbg.racofib.data.repository.user;


import android.content.Context;

import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class UserRepository {

    private final ApiService apiService;
    private final UserDao userDao;
    private final SubjectsDao subjectsDao;
    private final SubjectScheduleDao subjectScheduleDao;
    private final PrefManager prefManager;
    private final AppExecutors appExecutors;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    @Inject
    public UserRepository(AppExecutors appExecutors, AppDatabase appDatabase, PrefManager prefManager, ApiService apiService) {
        this.userDao = appDatabase.userDao();
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.subjectsDao = appDatabase.subjectsDao();
        this.subjectScheduleDao = appDatabase.subjectScheduleDao();

        userMutableLiveData.setValue(null);
    }

    /**
     * Gets the user from the database
     * {@link User} object
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
     * Once the user has been authorized successfully it downloads all the necessary data for the app
     * to run like {@link User}, the {@link com.victorbg.racofib.data.model.subject.Subject}s and the
     * {@link SubjectSchedule} which indicates the schedule of the user.
     *
     * @param context {@link Context to get the texts}
     * @param code    {@link String} code returned by the API to auth the user
     * @return {@link LiveData} with the state of the login and a message
     */
    public LiveData<Resource<String>> authUser(Context context, String code) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(context.getString(R.string.fetching_user_message)));
        appExecutors.networkIO().execute(() ->
                compositeDisposable.add(apiService.getAccessToken(
                        "authorization_code",
                        code,
                        BuildConfig.RacoRedirectUrl,
                        BuildConfig.RacoClientID,
                        BuildConfig.RacoSecret
                ).flatMap(token -> apiService.getUser(getToken(token.getAccessToken()), "json").flatMap(user -> {

                    appExecutors.diskIO().execute(() -> userDao.insert(user));

                    appExecutors.mainThread().execute(() -> result.setValue(Resource.loading(context.getString(R.string.fetching_subjects_message))));

                    return apiService.getSubjects(getToken(token.getAccessToken()), "json").flatMap(subjects -> {

                        Utils.assignRandomColors(context, subjects.result);
                        appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                        appExecutors.mainThread().execute(() -> result.setValue(Resource.loading(context.getString(R.string.fetching_schedule_message))));

                        return apiService.getSubjectsSchedule(getToken(token.getAccessToken()), "json").flatMap(timetable -> {
                            if (timetable != null) {
                                appExecutors.diskIO().execute(() -> subjectScheduleDao.insert(timetable.result));
                            }
                            return Single.just(token);
                        });
                    });
                })).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(tokenResponse -> {
                            getUser();
                            prefManager.setLogin(tokenResponse);
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.success(null)));
                        }, error -> {
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.error("An error has occurred: " + error.getMessage(), null)));
                        })));


        return result;
    }

    private String getToken(String token) {
        return "Bearer " + token;
    }

}
