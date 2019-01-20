package com.victorbg.racofib.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;

import com.victorbg.racofib.data.api.ApiManager;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.model.Subject;
import com.victorbg.racofib.model.SubjectSchedule;
import com.victorbg.racofib.model.api.ApiListResponse;
import com.victorbg.racofib.model.api.ApiNotesResponse;
import com.victorbg.racofib.model.login.LoginData;
import com.victorbg.racofib.model.user.User;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {

    //region variables
    private AppDatabase appDatabase;
    private ApiService apiService;
    private PrefManager prefManager;
    private CompositeDisposable compositeDisposable;

    public MutableLiveData<User> user;

    //endregion

    private static volatile DataManager ourInstance;

    public synchronized static DataManager getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (DataManager.class) {
                ourInstance = new DataManager(context);
            }
        }
        return ourInstance;
    }

    private DataManager() {
    }

    private DataManager(Context context) {
        appDatabase = AppDatabase.getAppDatabase(context);
        apiService = ApiManager.getInstance().getApiService();
        prefManager = PrefManager.getInstance();

        user = new MutableLiveData<>();
        user.setValue(null);

        initUser();
    }

    /**
     * Get user information from the database
     */
    private void initUser() {
        if (!prefManager.isLogged()) return;

        //The following operations are done in the main thread due they are
        //critical for the app as the app is needed even before inflating the main activity UI
        User u = appDatabase.userDao().getUser();
        u.subjects = appDatabase.subjectsDao().getSubjects(u.username);

        //Getting the full schedule is not necessary at the start
        //u.schedule = appDatabase.subjectScheduleDao().getSchedule(u.username);

        Calendar c = Calendar.getInstance();
        //-1 to start with 1 on monday (now it starts on sunday)
        //Possible bug if there are lessons on sunday, but i don't think there are lesson on sunday
        u.todaySubjects = appDatabase.subjectScheduleDao().getTodaySchedule(u.username, c.get(Calendar.DAY_OF_WEEK) - 1);

        user.setValue(u);

    }

    /**
     * Logs the user into the system and loads all the necessary into the database.
     *
     * @param token Token provided by the authentication
     */
    @SuppressLint("CheckResult")
    public void loginUser(@NonNull String token, long expirationTime, @NonNull ApiResult result) {

        apiService.getUser("Bearer " + token, "json").enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                User user = response.body();
                user.fullName = user.name + " " + user.surnames;
                appDatabase.userDao().insert(response.body());
                apiService.getSubjects("Bearer " + token, "json").enqueue(new Callback<ApiListResponse<Subject>>() {
                    @Override
                    public void onResponse(Call<ApiListResponse<Subject>> call, Response<ApiListResponse<Subject>> response) {
                        new Thread(() -> {
                            for (Subject subject : response.body().result) {
                                subject.user = user.username;
                                appDatabase.subjectsDao().insert(subject);
                            }
                        }).start();

                        apiService.getSubjectsSchedule("Bearer " + token, "json").enqueue(new Callback<ApiListResponse<SubjectSchedule>>() {
                            @Override
                            public void onResponse(Call<ApiListResponse<SubjectSchedule>> call, Response<ApiListResponse<SubjectSchedule>> response) {
                                new Thread(() -> {
                                    for (SubjectSchedule subject : response.body().result) {
                                        subject.username = user.username;
                                        appDatabase.subjectScheduleDao().insert(subject);
                                    }
                                }).start();

                                prefManager.setLogin(new LoginData(token, expirationTime));
                                initUser();
                                result.onCompleted();
                            }

                            @Override
                            public void onFailure(Call<ApiListResponse<SubjectSchedule>> call, Throwable t) {
                                result.onFailed(t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ApiListResponse<Subject>> call, Throwable t) {
                        result.onFailed(t.getMessage());

                    }
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                result.onFailed(t.getMessage());
            }
        });

    }
}
