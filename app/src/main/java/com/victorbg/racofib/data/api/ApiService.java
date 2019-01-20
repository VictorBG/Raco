package com.victorbg.racofib.data.api;

import com.victorbg.racofib.model.Note;
import com.victorbg.racofib.model.Subject;
import com.victorbg.racofib.model.SubjectSchedule;
import com.victorbg.racofib.model.api.ApiListResponse;
import com.victorbg.racofib.model.api.ApiNotesResponse;
import com.victorbg.racofib.model.user.User;

import java.util.List;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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
}
