package com.victorbg.racofib.data.repository.user;


import android.content.Context;
import android.graphics.Color;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.login.LoginData;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
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
    private AppDatabase appDatabase;
    private String[] colors;
    private int defaultColor = Color.GRAY;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    @Inject
    public UserRepository(Context context, AppExecutors appExecutors, UserDao userDao, SubjectsDao subjectsDao, SubjectScheduleDao subjectScheduleDao, PrefManager prefManager, ApiService apiService, AppDatabase appDatabase) {
        this.userDao = userDao;
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.subjectsDao = subjectsDao;
        this.subjectScheduleDao = subjectScheduleDao;
        this.colors = context.getResources().getStringArray(R.array.mdcolor_500);
        //Init user
        getUser();
    }

    /**
     * Gets the user from the database as a LiveData ready to observe
     *
     * @return
     */
    public LiveData<User> getUser() {
        if (userMutableLiveData.getValue() == null) {
            appExecutors.diskIO().execute(() -> {
                compositeDisposable.add(userDao.getUser().flatMap(user -> {

                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if (day < 0) day = 7;

                    return Single.zip(
                            subjectsDao.getSubjects(user.username).subscribeOn(Schedulers.io()),
                            subjectScheduleDao.getSchedule(user.username).subscribeOn(Schedulers.io()),
                            subjectScheduleDao.getTodaySchedule(user.username, day).subscribeOn(Schedulers.io()),
                            (subjects, schedule, today) -> {
                                user.subjects = subjects;
                                user.schedule = schedule;
                                user.todaySubjects = today;
                                return user;
                            }
                    );

//                    return subjectsDao.getSubjects(user.username).flatMap(subjects -> {
//                        user.subjects = subjects;
//
//                        return subjectScheduleDao.getSchedule(user.username).flatMap(schedule -> {
//                            user.schedule = schedule;
//
//
//                            return subjectScheduleDao.getTodaySchedule(user.username, day).flatMap(today -> {
//                                user.todaySubjects = today;
//                                return Single.just(user);
//                            });
//                        });
//                    });
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user ->
                                appExecutors.mainThread().execute(() -> userMutableLiveData.setValue(user))));
                //user.todaySubjects = subjectScheduleDao.getTodaySchedule(user.username, day);

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

//        appExecutors.diskIO().execute(() -> appDatabase.beginTransaction());
        compositeDisposable.add(apiService.getUser(getToken(token), "json").flatMap(user -> {

            appExecutors.diskIO().execute(() -> userDao.insert(user));

            appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching subjects...")));

            return apiService.getSubjects(getToken(token), "json").flatMap(subjects -> {

                ArrayList<String> c = (ArrayList<String>) Arrays.asList(colors);
                Collections.shuffle(c);
                for (int i = 0; i < subjects.result.size(); i++) {
                    subjects.result.get(i).color = c.get(i);
                }
                user.subjects = subjects.result;
                appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching schedule...")));

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
//                    appExecutors.diskIO().execute(() -> appDatabase.setTransactionSuccessful());
                    userMutableLiveData.postValue(user);
                    prefManager.setLogin(new LoginData(token, expirationTime, user.username));
                    appExecutors.mainThread().execute(() -> result.setValue(Resource.success("All fetched bro")));
                }, error -> {
                    //TODO: There is a bug on the API that if the user has no classes it returns an error 500 instead of
                    //TODO: a correct error, like 204 No Content or a 200 with an empty body: {total:0, results:[]}

                    //FIB development team has been notified about this bug, which makes me unable to
                    //test anything until I have enrolled some classes

//                    appExecutors.diskIO().execute(() -> appDatabase.endTransaction());
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
