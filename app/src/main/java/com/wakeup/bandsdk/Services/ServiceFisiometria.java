package com.wakeup.bandsdk.Services;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Authenticate.JWTAuth;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.Pojos.Fisiometria.ResponseFisiometria;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ServiceFisiometria {

    @POST("authenticate")
    Call<JWTAuth> getJwtToken(@Body JsonObject body);

    @GET("fisiometria-1-s?size=1&sort=fechaRegistro,desc&sort=id&")
    Call<List<DataFisiometria>> getPhysiometryData(@Header("Authorization") String api_token,
                                                   @Query("userId.equals") int id);

    @GET("fisiometria-1-s?size=20&sort=fechaRegistro,desc&sort=id&")
    Call<List<DataFisiometria>> getPhysiometryDataAll(@Header("Authorization") String api_token,
                                                      @Query("userId.equals") int id);

    @POST("fisiometria-1-s")
    Call<DataFisiometria> setPhysiometryData(@Header("Authorization") String api_token, @Body JsonObject body);
}
