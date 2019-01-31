package com.victorbg.racofib.data.repository.user;


import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.login.LoginData;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class UserRepository {

    private ApiService apiService;
    private UserDao userDao;
    private SubjectsDao subjectsDao;
    private SubjectScheduleDao subjectScheduleDao;
    private PrefManager prefManager;
    private AppExecutors appExecutors;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    @Inject
    public UserRepository(AppExecutors appExecutors, UserDao userDao, SubjectsDao subjectsDao, SubjectScheduleDao subjectScheduleDao, PrefManager prefManager, ApiService apiService) {
        this.userDao = userDao;
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.subjectsDao = subjectsDao;
        this.subjectScheduleDao = subjectScheduleDao;
    }

    /**
     * Gets the user from the database as a LiveData ready to observe
     *
     * @return
     */
    public LiveData<User> getUser() {
        if (userMutableLiveData.getValue() == null) {
            appExecutors.diskIO().execute(() -> {
                User user = userDao.getUser();
                user.subjects = subjectsDao.getSubjects(user.username);
                user.schedule = subjectScheduleDao.getSchedule(user.username);

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (day < 0) day = 7;
                user.todaySubjects = subjectScheduleDao.getTodaySchedule(user.username, day);
                appExecutors.mainThread().execute(() -> userMutableLiveData.setValue(user));
            });
        }
        return userMutableLiveData;
    }

    /**
     * Login the user into the app saving the params into the preferences and fetching the important user info
     * from the api.
     *
     * @param token
     * @param expirationTime
     * @return {@link Resource<String>} that indicates the state and the message to show on the login activity
     */
    public LiveData<Resource<String>> loginUser(@NonNull String token, long expirationTime) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading("Fetching user..."));

        compositeDisposable.add(apiService.getUser(getToken(token), "json").flatMap(user -> {

            appExecutors.diskIO().execute(() -> userDao.insert(user));

            appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching subjects...")));

            return apiService.getSubjects(getToken(token), "json").flatMap(subjects -> {

                user.subjects = subjects.result;
                appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching timetable...")));

                return apiService.getSubjectsSchedule(getToken(token), "json").flatMap(timetable -> {
                    if (timetable != null) {
                        appExecutors.diskIO().execute(() -> subjectScheduleDao.insert(timetable.result));
                        user.schedule = timetable.result;
                    }
                    return Single.just(user);
                });
            });
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(user -> {
                    userMutableLiveData.postValue(user);
                    prefManager.setLogin(new LoginData(token, expirationTime));
                    appExecutors.mainThread().execute(() -> result.setValue(Resource.success("All fetched bro")));
                }, error -> {
                    //TODO: There is a bug on the API that if the user has no classes it returns an error 500 instead of
                    //TODO: a correct error, like 204 No Content or a 200 with an empty body: {total:0, results:[]}
                    //To prevent the app from failing in the development stage log the user if there was an error
                    prefManager.setLogin(new LoginData(token, expirationTime));
                    appExecutors.mainThread().execute(() -> result.setValue(Resource.error("An error has occurred: " + error.getMessage(), null)));
                }));

        return result;
    }

    private String getToken() {
        return "Bearer " + prefManager.getToken();
    }

    private String getToken(String token) {
        return "Bearer " + token;
    }

}
