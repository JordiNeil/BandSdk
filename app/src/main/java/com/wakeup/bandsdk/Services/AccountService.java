package com.wakeup.bandsdk.Services;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.DataUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AccountService {
    @GET("account")
    Call<DataUser> getUserData(@Header("Authorization") String api_token);

    @POST("register")
    Call<Void> setUserData(@Body JsonObject body);
}
