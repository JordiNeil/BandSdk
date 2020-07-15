package com.wakeup.bandsdk.Services;

import com.wakeup.bandsdk.Pojos.Conditions.ConditionData;
import com.wakeup.bandsdk.Pojos.Ips.IpsData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IpsService {
    @GET("ips")
    Call<List<IpsData>> getIpsData(@Header("Authorization") String api_token);
}
