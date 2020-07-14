package com.wakeup.bandsdk.Services;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Alarms.AlarmData;
import com.wakeup.bandsdk.Pojos.DataUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AlarmService {
    @POST("alarmas")
    Call<AlarmData> setAlarm(@Header("Authorization") String api_token, @Body JsonObject body);
}
