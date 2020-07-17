package com.wakeup.bandsdk.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FisiometriaUserActivity extends AppCompatActivity {
    String storedJwtToken;
    Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisiometria_user);
        SharedPreferences sharedPrefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        userId = sharedPrefs.getInt(ConfigGeneral.STOREDUSERID, 0);
        getPhysiometryDataById();
    }


    public void getPhysiometryDataById() {


        System.out.println("ID DE USUARIO-+++++++++++++++++++++++++" + userId+"\n"+storedJwtToken);

        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryDataAll("Bearer " + storedJwtToken, userId);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {


                if (response.isSuccessful()) {
                    List<DataFisiometria> res = response.body();

                    assert res != null;
                    ArrayList<Object> fetchedPhysiometryData = new ArrayList<>();
                    System.out.println(res);
                    if (res.size() != 0) {

                    }
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {
                // System.out.println(t.getMessage());

            }
        });
    }
}