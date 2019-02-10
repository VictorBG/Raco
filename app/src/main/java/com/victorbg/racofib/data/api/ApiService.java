package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.exams.Semester;
import com.victorbg.racofib.data.model.user.User;


import androidx.lifecycle.LiveData;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @GET("jo/avisos")
    LiveData<ApiResponse<ApiNotesResponse>> getPublications(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo")
    Single<User> getUser(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo/assignatures")
    Single<ApiListResponse<Subject>> getSubjects(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo/classes")
    Single<ApiListResponse<SubjectSchedule>> getSubjectsSchedule(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/actual")
    Single<Semester> getCurrentSemester(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/{semester}/examens")
    Single<ApiListResponse<Exam>> getExams(@Header("Authorization") String authToken, @Path("semester") String semester, @Query("format") String format, @Query("assig") String subject);

    @Headers({"Content-Type: application/json"})
    @GET("assignatures/{subject}/guia")
    Single<Subject> getSubject(@Header("Authorization") String authToken, @Path("subject") String subject, @Query("format") String format);
}
