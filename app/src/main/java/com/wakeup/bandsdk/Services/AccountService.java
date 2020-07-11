package com.wakeup.bandsdk.Services;

import com.wakeup.bandsdk.Pojos.DataUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AccountService {
    @GET("account")
    Call<DataUser> getUserData(@Header("Authorization") String api_token);
}
