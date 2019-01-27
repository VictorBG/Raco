package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.SubjectSchedule;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.exams.Semester;
import com.victorbg.racofib.data.model.user.User;


import java.util.Calendar;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @GET("jo/avisos")
    Call<ApiNotesResponse> getNotes(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo")
    Call<User> getUser(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo/assignatures")
    Call<ApiListResponse<Subject>> getSubjects(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo/classes")
    Call<ApiListResponse<SubjectSchedule>> getSubjectsSchedule(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/actual")
    Single<Semester> getCurrentSemester(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/{semester}/examens")
    Single<ApiListResponse<Exam>> getExams(@Header("Authorization") String authToken, @Path("semester") String semester, @Query("format") String format, @Query("assig") String subject);
}
