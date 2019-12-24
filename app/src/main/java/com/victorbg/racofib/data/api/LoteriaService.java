package com.victorbg.racofib.data.api;

import com.victorbg.racofib.data.model.LoteriaResponse;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoteriaService {

    @GET("LoteriaNavidadPremiados")
    Call<ResponseBody> getLoteria(@Query("n") Integer number);
}
