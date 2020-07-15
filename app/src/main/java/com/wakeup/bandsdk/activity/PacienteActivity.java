package com.wakeup.bandsdk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.Pojos.Conditions.ConditionData;
import com.wakeup.bandsdk.Pojos.Ips.IpsData;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ConditionService;
import com.wakeup.bandsdk.Services.IpsService;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context = this;
    SharedPreferences sharedPrefs;
    String storedJwtToken;
    public  static List<ConditionData> conditionDataList = new ArrayList<>();
    public  static List<IpsData> ipsDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        sharedPrefs = context.getSharedPreferences(ConfigGeneral.preference_file_key, Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        String username = sharedPrefs.getString(ConfigGeneral.STOREDUSERLOGIN, "");
        Log.d(TAG, "Username: " + username);

        fetchConditionData(storedJwtToken);
    }

    public void fetchConditionData(String storedJwtToken) {
        ConditionService condition = ConfigGeneral.retrofit.create(ConditionService.class);
        final Call<List<ConditionData>> conditionRequest = condition.getConditionData("Bearer " + storedJwtToken);

        conditionRequest.enqueue(new Callback<List<ConditionData>>() {
            @Override
            public void onResponse(Call<List<ConditionData>> call, Response<List<ConditionData>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Fetch Condition Data response: " + response.body());
                    conditionDataList = response.body();
                    assert conditionDataList != null;
                    for (ConditionData cond: conditionDataList){
                        System.out.println(cond.getId());
                    }
                    fetchIpsData(storedJwtToken);
                }
            }

            @Override
            public void onFailure(Call<List<ConditionData>> call, Throwable t) {
                Log.d(TAG, "Fetch Condition Data onFailure: " + t.getMessage());
            }
        });
    }

    public void fetchIpsData(String storedJwtToken) {
        IpsService ips = ConfigGeneral.retrofit.create(IpsService.class);
        final Call<List<IpsData>> ipsRequest = ips.getIpsData("Bearer " + storedJwtToken);

        ipsRequest.enqueue(new Callback<List<IpsData>>() {
            @Override
            public void onResponse(Call<List<IpsData>> call, Response<List<IpsData>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Fetch Ips Data response: " + response.body());
                    ipsDataList = response.body();
                    assert ipsDataList != null;
                    for (IpsData ips: ipsDataList){
                        System.out.println(ips.getNombre());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<IpsData>> call, Throwable t) {
                Log.d(TAG, "Fetch Ips Data onFailure: " + t.getMessage());
            }
        });
    }

    public void sendPatientData() {

    }
}