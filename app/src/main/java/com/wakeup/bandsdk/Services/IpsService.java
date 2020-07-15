package com.wakeup.bandsdk.Services;

import com.wakeup.bandsdk.Pojos.Conditions.ConditionData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IpsService {
    @GET("ips")
    Call<List<ConditionData>> getIpsData(@Header("Authorization") String api_token);
}
