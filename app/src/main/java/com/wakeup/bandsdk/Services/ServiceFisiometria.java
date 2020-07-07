package com.wakeup.bandsdk.Services;

import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.Pojos.Fisiometria.ResponseFisiometria;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;


public interface ServiceFisiometria {

    @GET("fisiometria-1-s?")
    Call<List<DataFisiometria>> getStudiesSubjes(@Header("Authorization") String api_token,
                                                 @Query("userId.equals") int id);
}
