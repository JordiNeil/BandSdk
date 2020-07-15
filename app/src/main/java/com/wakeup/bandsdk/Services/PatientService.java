package com.wakeup.bandsdk.Services;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Patients.PatientData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PatientService {
    @POST("pacientes")
    Call<PatientData> setPatient(@Header("Authorization") String api_token, @Body JsonObject body);
}
