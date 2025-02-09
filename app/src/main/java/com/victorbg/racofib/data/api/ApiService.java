package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.exams.Semester;
import com.victorbg.racofib.data.model.season.APIEvent;
import com.victorbg.racofib.data.model.seminar.Seminar;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;

import androidx.lifecycle.LiveData;

import java.util.Calendar;

import io.reactivex.Observable;
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
  @GET("jo/avisos?format=json")
  LiveData<ApiResponse<ApiNotesResponse>> getNotes();

  @Headers({"Content-Type: application/json"})
  @GET("jo?format=json")
  Single<User> getUser(@Header("Authorization") String authToken);

  @FormUrlEncoded
  @POST("o/token")
  Single<TokenResponse> getAccessToken(
      @Field("grant_type") String grantType,
      @Field("code") String code,
      @Field("redirect_uri") String redirectURI,
      @Field("client_id") String clientID,
      @Field("client_secret") String clientSecret);

  @Headers({"Content-Type: application/json"})
  @GET("jo/assignatures?format=json")
  Single<ApiListResponse<Subject>> getSubjects(@Header("Authorization") String authToken);

  @Headers({"Content-Type: application/json"})
  @GET("jo/classes?format=json")
  Single<ApiListResponse<SubjectSchedule>> getSubjectsSchedule(
      @Header("Authorization") String authToken);

  @Headers({"Content-Type: application/json"})
  @GET("quadrimestres/actual?format=json")
  Single<Semester> getCurrentSemester();

  @Headers({"Content-Type: application/json"})
  @GET("quadrimestres/{semester}/examens?format=json")
  Single<ApiListResponse<Exam>> getExams(
      @Path("semester") String semester, @Query("assig") String subject);

  @Headers({"Content-Type: application/json"})
  @GET("assignatures/{subject}/guia?format=json")
  Single<Subject> getSubject(@Path("subject") String subject);

  @Headers({"Content-Type: application/json"})
  @GET("events/?format=json")
  Single<ApiListResponse<APIEvent>> getEvents();

  @Headers({"Content-Type: application/json"})
  @GET("seminaris-siri/?format=json")
  Single<ApiListResponse<Seminar>> internalGetSeminars(@Query("curs") Integer course);

  public default Observable<ApiListResponse<Seminar>> getSeminars() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    if (calendar.get(Calendar.MONTH) > 6) {
      return internalGetSeminars(year - 1)
          .toObservable()
          .mergeWith(internalGetSeminars(year).toObservable());
    } else {
      return internalGetSeminars(year)
          .toObservable()
          .mergeWith(internalGetSeminars(year + 1).toObservable());
    }
  }
}
