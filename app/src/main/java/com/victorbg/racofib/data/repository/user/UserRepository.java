package com.victorbg.racofib.data.repository.user;


import android.content.Context;

import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class UserRepository {

    private ApiService apiService;
    private UserDao userDao;
    private SubjectsDao subjectsDao;
    private SubjectScheduleDao subjectScheduleDao;
    private PrefManager prefManager;
    private AppExecutors appExecutors;
    private AppDatabase appDatabase;
    private Context context;


    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    @Inject
    public UserRepository(Context context, AppExecutors appExecutors, UserDao userDao, SubjectsDao subjectsDao, SubjectScheduleDao subjectScheduleDao, PrefManager prefManager, ApiService apiService, AppDatabase appDatabase, NotesDao notesDao) {
        this.userDao = userDao;
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.subjectsDao = subjectsDao;
        this.subjectScheduleDao = subjectScheduleDao;
        this.appDatabase = appDatabase;
        this.context = context;

        userMutableLiveData.setValue(null);
        //Init user
        //getUser();
    }

    /**
     * Gets the user from the database as a LiveData ready to observe
     *
     * @return
     */
    public LiveData<User> getUser() {
        if (userMutableLiveData.getValue() == null) {
            appExecutors.diskIO().execute(() ->
                    compositeDisposable.add(
                            userDao.getUser().flatMap(user ->
                                    Single.zip(
                                            subjectsDao.getSubjects().subscribeOn(Schedulers.io()),
                                            subjectScheduleDao.getSchedule().subscribeOn(Schedulers.io()),
                                            subjectScheduleDao.getTodaySchedule(Utils.getDayOfWeek()).subscribeOn(Schedulers.io()),
                                            (subjects, schedule, today) -> {
                                                user.subjects = subjects;
                                                user.schedule = schedule;
                                                user.todaySubjects = today;
                                                return user;
                                            }
                                    )).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(user ->
                                                    appExecutors.mainThread().execute(() -> userMutableLiveData.setValue(user))
                                            , Timber::d))
            );
        }
        return userMutableLiveData;
    }

    public LiveData<Resource<String>> authUser(String code) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading("Fetching user"));
        appExecutors.networkIO().execute(() ->
                compositeDisposable.add(apiService.getAccessToken(
                        "authorization_code",
                        code,
                        BuildConfig.RacoRedirectUrl,
                        BuildConfig.RacoClientID,
                        BuildConfig.RacoSecret
                ).flatMap(token -> apiService.getUser(getToken(token.getAccessToken()), "json").flatMap(user -> {
                    appExecutors.diskIO().execute(() -> userDao.insert(user));

                    appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching subjects...")));

                    return apiService.getSubjects(getToken(token.getAccessToken()), "json").flatMap(subjects -> {
                        Utils.assignRandomColors(context, subjects.result);
                        user.subjects = subjects.result;
                        appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                        appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching schedule...")));

                        return apiService.getSubjectsSchedule(getToken(token.getAccessToken()), "json").flatMap(timetable -> {
                            if (timetable != null) {
                                appExecutors.diskIO().execute(() -> subjectScheduleDao.insert(timetable.result));
                                user.schedule = timetable.result;
                            }
                            return Single.just(token);
                        });
                    });
                })).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(tokenResponse -> {
                            getUser();
                            prefManager.setLogin(tokenResponse);
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.success("All fetched")));
                        }, error -> {
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.error("An error has occurred: " + error.getMessage(), null)));
                        })));


        return result;
    }

    public void refreshToken() {
        appExecutors.networkIO().execute(() -> {
            compositeDisposable.add(apiService.refreshToken(
                    "refresh_token",
                    prefManager.getRefreshToken(),
                    BuildConfig.RacoClientID,
                    BuildConfig.RacoSecret
            ).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(token -> {
                        prefManager.setLogin(token);
                    }));
        });
    }

    public LiveData<List<SubjectSchedule>> getSchedule() {

        MutableLiveData<List<SubjectSchedule>> result = new MutableLiveData<>();
        result.setValue(new ArrayList<>());

        appExecutors.diskIO().execute(() -> {
            if (userMutableLiveData.getValue() == null) {
                return;
            }

            Utils.assignColorsSchedule(userMutableLiveData.getValue().subjects, userMutableLiveData.getValue().schedule);

            appExecutors.mainThread().execute(() -> result.setValue(new ArrayList<>(userMutableLiveData.getValue().schedule)));
//            compositeDisposable.add(
//                    subjectScheduleDao.getSchedule().flatMap(Single::just).subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(subjects -> appExecutors.mainThread().execute(() -> result.setValue(subjects)), Timber::d)
//            );
        });


        return result;
    }

    public void logout() {
        appExecutors.diskIO().execute(() -> {
            appDatabase.clearAllTables();
        });
        prefManager.logout();

    }

    public String getToken() {
        return getToken(prefManager.getToken());
    }

    private String getToken(String token) {
        return "Bearer " + token;
    }

}
