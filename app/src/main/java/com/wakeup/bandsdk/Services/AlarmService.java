package com.wakeup.bandsdk.Services;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Alarms.AlarmData;
import com.wakeup.bandsdk.Pojos.DataUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AlarmService {
    @GET("alarmas")
    Call<List<AlarmData>> getAlarmData(@Header("Authorization") String api_token, @Query("userId.equals") int id);

    @POST("alarmas")
    Call<AlarmData> setAlarm(@Header("Authorization") String api_token, @Body JsonObject body);
}
