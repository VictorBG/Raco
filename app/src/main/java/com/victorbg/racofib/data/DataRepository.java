package com.victorbg.racofib.data;

import android.annotation.SuppressLint;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.api.result.ApiResultData;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.repository.notes.NotesRepository;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.SubjectSchedule;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.login.LoginData;
import com.victorbg.racofib.data.model.user.User;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class DataRepository implements Injectable {

    private AppDatabase appDatabase;
    private ApiService apiService;
    private PrefManager prefManager;

    public MutableLiveData<User> user;

    private NotesRepository notesRepository;

    /*
    There should be an option to put the parameters as @Inject directly without injecting them
    into the constructor and then binding them to the variables.
     */
    @Inject
    public DataRepository(PrefManager prefManager, AppDatabase appDatabase, ApiService apiService) {
        this.prefManager = prefManager;
        this.appDatabase = appDatabase;
        this.apiService = apiService;

        this.notesRepository = new NotesRepository(appDatabase.notesDao(), prefManager, apiService);

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
        //critical for the app as they are needed even before inflating the main activity UI
        User u = appDatabase.userDao().getUser();
        u.subjects = appDatabase.subjectsDao().getSubjects(u.username);

        //Getting the full schedule is not necessary at the start
        //u.schedule = appDatabase.subjectScheduleDao().getSchedule(u.username);

        Calendar c = Calendar.getInstance();
        //-1 to start with 1 on monday (now it starts on sunday)
        //Possible bug if there are classes on sunday, but i don't think there are classes on sunday

        //Bugfix for classes on sunday
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) day = 7;

//        u.todaySubjects = appDatabase.subjectScheduleDao().getTodaySchedule(u.username, day);
        u.todaySubjects = appDatabase.subjectScheduleDao().getTodaySchedule(u.username, 4);

        user.setValue(u);

    }

    /**
     * Logs the user into the system and loads all the necessary into the database.
     * <p>
     * TODO: Need to concatenate calls in a more compact way. Maybe with concat() from rxjava
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

//    public void getNotes(@NonNull ApiResultData<List<Note>> result) {
//        apiService.getNotes("Bearer " + prefManager.getToken(), "json").enqueue(new Callback<ApiNotesResponse>() {
//            @Override
//            public void onResponse(Call<ApiNotesResponse> call, Response<ApiNotesResponse> response) {
//                result.onCompleted(response.body().result);
//            }
//
//            @Override
//            public void onFailure(Call<ApiNotesResponse> call, Throwable t) {
//                result.onFailed(t.getMessage());
//            }
//        });
//    }

    public void getNotes(@NonNull ApiResult result, boolean force) {
        notesRepository.getNotes(result, force);
    }

    public LiveData<List<Note>> getNotesLiveData() {
        return notesRepository.getLiveData();
    }
}
