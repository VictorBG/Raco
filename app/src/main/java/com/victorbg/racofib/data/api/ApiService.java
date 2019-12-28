package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.exams.Semester;
import com.victorbg.racofib.data.model.season.APIEvent;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;

import androidx.lifecycle.LiveData;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @GET("jo/avisos")
    LiveData<ApiResponse<ApiNotesResponse>> getNotes(@Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo")
    Single<User> getUser(@Header("Authorization") String authToken, @Query("format") String format);

    @FormUrlEncoded
    @POST("o/token")
    Single<TokenResponse> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectURI,
            @Field("client_id") String clientID,
            @Field("client_secret") String client_secret);

    @Headers({"Content-Type: application/json"})
    @GET("jo/assignatures")
    Single<ApiListResponse<Subject>> getSubjects(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("jo/classes")
    Single<ApiListResponse<SubjectSchedule>> getSubjectsSchedule(@Header("Authorization") String authToken, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/actual")
    Single<Semester> getCurrentSemester(@Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("quadrimestres/{semester}/examens")
    Single<ApiListResponse<Exam>> getExams(@Path("semester") String semester, @Query("format") String format, @Query("assig") String subject);

    @Headers({"Content-Type: application/json"})
    @GET("assignatures/{subject}/guia")
    Single<Subject> getSubject(@Path("subject") String subject, @Query("format") String format);

    @Headers({"Content-Type: application/json"})
    @GET("events/?format=json")
    Single<ApiListResponse<APIEvent>> getEvents();

}
