package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {

    @FormUrlEncoded
    @POST("o/token")
    Call<TokenResponse> refreshToken(
            @Field("grant_type") String grantType,
            @Field("refresh_token") String code,
            @Field("client_id") String clientID,
            @Field("client_secret") String client_secret);


}