package com.victorbg.racofib.data.repository.user;


import android.content.Context;

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

import java.sql.Time;
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
    private String[] colors;


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
        this.colors = context.getResources().getStringArray(R.array.mdcolor_400);
        this.appDatabase = appDatabase;

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
        appExecutors.networkIO().execute(() ->
                compositeDisposable.add(apiService.getAccessToken(
                        "authorization_code",
                        code,
                        "apifib://login",
                        "dzHij8jTq4tpH9EzmNgmh3svKbRwBkV54cGr3RVh",
                        "d4BfOqLwytQpUa0fDD4EtUXd5iPCdcK9LHlhqX2eVXyrnsbVHfpkWOMWQakt5fP4v76EYzrcZgySaiAPsMXMOHmagHRHxqV7ZuhuihBcEmWxCW1CQjXJ34pt00FIn0By"
                ).flatMap(token -> apiService.getUser(getToken(token.getAccessToken()), "json").flatMap(user -> {

                    Timber.d("Downloading subjects");
                    appExecutors.diskIO().execute(() -> userDao.insert(user));

                    appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching subjects...")));

                    return apiService.getSubjects(getToken(token.getAccessToken()), "json").flatMap(subjects -> {
                        Timber.d("Downloading schedule");
                        List<String> c = Arrays.asList(colors);
                        Collections.shuffle(c);
                        for (int i = 0; i < subjects.result.size(); i++) {
                            subjects.result.get(i).color = c.get(i);
                        }
                        user.subjects = subjects.result;
                        appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                        appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching schedule...")));

                        return apiService.getSubjectsSchedule(getToken(token.getAccessToken()), "json").flatMap(timetable -> {
                            if (timetable != null) {
                                appExecutors.diskIO().execute(() -> subjectScheduleDao.insert(timetable.result));
                                user.schedule = timetable.result;
                            }
                            Timber.d("Schedule downloaded");
                            return Single.just(token);
                        });
                    });
                })).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(tokenResponse -> {
                            getUser();
                            prefManager.setLogin(tokenResponse);
                            appExecutors.mainThread().execute(() -> result.setValue(Resource.success("All fetched bro")));
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
                    "dzHij8jTq4tpH9EzmNgmh3svKbRwBkV54cGr3RVh",
                    "d4BfOqLwytQpUa0fDD4EtUXd5iPCdcK9LHlhqX2eVXyrnsbVHfpkWOMWQakt5fP4v76EYzrcZgySaiAPsMXMOHmagHRHxqV7ZuhuihBcEmWxCW1CQjXJ34pt00FIn0By"
            ).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(token -> {
                        prefManager.setLogin(token);
                    }, error -> {

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

            HashMap<String, String> colorsMap = new HashMap<>();
            for (Subject s : userMutableLiveData.getValue().subjects) {
                colorsMap.put(s.id, s.color);
            }

            for (int i = 0; i < userMutableLiveData.getValue().schedule.size(); i++) {
                String id = userMutableLiveData.getValue().schedule.get(i).id;
                if (colorsMap.containsKey(id)) {
                    userMutableLiveData.getValue().schedule.get(i).color = colorsMap.get(id);
                }
            }

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

    /**
     * Login the user into the app saving the params into the preferences and fetching the important user info
     * from the api.
     *
     * @param tokenResponse {@link TokenResponse}
     * @return {@link Resource<String>} that indicates the state and the message to show on the login activity
     * <p>
     * This method has moved into authUser combined with auth grant type instead of implicit type used
     */
    @Deprecated
    public LiveData<Resource<String>> loginUser(@NonNull TokenResponse tokenResponse) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading("Fetching user..."));

//        appExecutors.diskIO().execute(() -> appDatabase.beginTransaction());
        compositeDisposable.add(apiService.getUser(getToken(tokenResponse.getAccessToken()), "json").flatMap(user -> {

            appExecutors.diskIO().execute(() -> userDao.insert(user));

            appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching subjects...")));

            return apiService.getSubjects(getToken(tokenResponse.getAccessToken()), "json").flatMap(subjects -> {

                List<String> c = Arrays.asList(colors);
                Collections.shuffle(c);
                for (int i = 0; i < subjects.result.size(); i++) {
                    subjects.result.get(i).color = c.get(i);
                }
                user.subjects = subjects.result;
                appExecutors.diskIO().execute(() -> subjectsDao.insert(subjects.result));
                appExecutors.mainThread().execute(() -> result.setValue(Resource.loading("Fetching schedule...")));

                return apiService.getSubjectsSchedule(getToken(tokenResponse.getAccessToken()), "json").flatMap(timetable -> {
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
//                    userMutableLiveData.postValue(user);
                    getUser();
                    prefManager.setLogin(tokenResponse);
                    appExecutors.mainThread().execute(() -> result.setValue(Resource.success("All fetched bro")));

                }, error -> {
                    /*There is a bug on the API that if the user has no classes it returns an error 500 instead of
                      correct error, like 204 No Content or a 200 with an empty body: {total:0, results:[]}
                      FIB development team has been notified about this bug, which makes me unable to
                      test anything until I have enrolled some classes*/

//                    appExecutors.diskIO().execute(() -> appDatabase.endTransaction());
                    appExecutors.mainThread().execute(() -> result.setValue(Resource.error("An error has occurred: " + error.getMessage(), null)));
                }));

        return result;
    }

    public String getToken() {
        return "Bearer " + prefManager.getToken();
    }

    private String getToken(String token) {
        return "Bearer " + token;
    }

}
