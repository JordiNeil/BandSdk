package com.wakeup.bandsdk.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.Pojos.Alarms.AlarmData;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.AlarmService;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.activity.FisiometriaUserActivity;
import com.wakeup.bandsdk.activity.HomeActivity;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public TextView tv_tiredFragment, tv_heart_rateFragment, tv_blood_pressureFragment, tv_blood_oxygenFragment, tv_desc_alarm, tv_title_alarm;

    String temp;
    String presS;
    String oximetria;
    String ritmoCar;
    String dateRegister;
    ArrayList<Integer> a;
    TextView tx_heart_rate;
    String storedJwtToken;
    Integer userId;
    ScrollView scroll_alarma;
    LinearLayout rl_heart_rate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void getPhysiometryDataById() {


        //System.out.println("ID DE USUARIO-+++++++++++++++++++++++++" + userId);
        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryData("Bearer " + storedJwtToken, userId);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {


                if (response.isSuccessful()) {
                    List<DataFisiometria> res = response.body();

                    assert res != null;
                    ArrayList<Object> fetchedPhysiometryData = new ArrayList<>();
                    if (res.size() != 0) {
                        fetchedPhysiometryData.add(0, res.get(res.size() - 1).getRitmoCardiaco());
                        fetchedPhysiometryData.add(1, res.get(res.size() - 1).getOximetria());
                        fetchedPhysiometryData.add(2, res.get(res.size() - 1).getPresionArterialSistolica());
                        fetchedPhysiometryData.add(3, res.get(res.size() - 1).getPresionArterialDiastolica());
                        fetchedPhysiometryData.add(4, res.get(res.size() - 1).getTemperatura());
                        fetchedPhysiometryData.add(5, res.get(res.size() - 1).getFechaToma());
                        System.out.println(fetchedPhysiometryData.get(5).toString());

                        String fecha = fetchedPhysiometryData.get(5).toString();

                        int year = Integer.parseInt(fecha.split("T")[0].split("-")[0]);
                        int month = Integer.parseInt(fecha.split("T")[0].split("-")[1]);
                        int day = Integer.parseInt(fecha.split("T")[0].split("-")[2]);

                        int hour = Integer.parseInt(fecha.split("T")[1].split(":")[0]);
                        int minute = Integer.parseInt(fecha.split("T")[1].split(":")[1]);
//                    int second= Integer.parseInt(fecha.split("T")[1].split(":")[2].split(".")[0]);


                        String hora = fecha.split("T")[1];

                        Calendar calendario = Calendar.getInstance();

                        calendario.set(year, month, day, hour - 5, minute, 0);

                        dateRegister = getString(R.string.measure_recently) + ": " + calendario.get(Calendar.YEAR) + "-" + calendario.get(Calendar.MONTH) + "-" + calendario.get(Calendar.DAY_OF_MONTH) + " " +
                                calendario.get(Calendar.HOUR) + ":" + calendario.get(Calendar.MINUTE);


                        temp = fetchedPhysiometryData.get(4) + "°C";
                        presS = fetchedPhysiometryData.get(2) + "/" + fetchedPhysiometryData.get(3);
                        oximetria = fetchedPhysiometryData.get(1).toString();
                        ritmoCar = fetchedPhysiometryData.get(0).toString();
                        tv_tiredFragment.setText(temp);
                        tv_blood_oxygenFragment.setText(oximetria);
                        tv_blood_pressureFragment.setText(presS);
                        tv_heart_rateFragment.setText(ritmoCar);

                        tx_heart_rate.setText(dateRegister);
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

    public void getLatestAlarmByUserId() {
        AlarmService alarm = ConfigGeneral.retrofit.create(AlarmService.class);
        final Call<List<AlarmData>> response = alarm.getAlarmData("Bearer " + storedJwtToken, userId);

        response.enqueue(new Callback<List<AlarmData>>() {
            @Override
            public void onResponse(Call<List<AlarmData>> call, Response<List<AlarmData>> response) {
                if (response.isSuccessful()) {
                    //Log.d(TAG, "Alarms by userId response: " + response.body());

                    List<AlarmData> alarmDataList = response.body();
                    if (alarmDataList.size() != 0) {
                        ArrayList<Object> fetchedAlarmData = new ArrayList<>();
                        fetchedAlarmData.add(0, alarmDataList.get(alarmDataList.size() - 1).getId());
                        fetchedAlarmData.add(1, alarmDataList.get(alarmDataList.size() - 1).getDescripcion());
                        fetchedAlarmData.add(2, alarmDataList.get(alarmDataList.size() - 1).getProcedimiento());
                        fetchedAlarmData.add(3, alarmDataList.get(alarmDataList.size() - 1).getTimeInstant());
                        //System.out.println(fetchedAlarmData);
                        tv_desc_alarm.setText(getString(R.string.alarma_desc) + " " + fetchedAlarmData.get(2).toString() + "\n" + getString(R.string.alarma_titel) + " " + fetchedAlarmData.get(1).toString());
                    /*tv_title_alarm.setMovementMethod(new ScrollingMovementMethod());
                    tv_title_alarm.setText();*/
                    }


                }
            }

            @Override
            public void onFailure(Call<List<AlarmData>> call, Throwable t) {
                //  Log.d(TAG, "Alarms by userId onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        userId = sharedPrefs.getInt(ConfigGeneral.STOREDUSERID, 0);
        // Inflate the layout for this fragment
        View fragmentViewHome = inflater.inflate(R.layout.fragmen_home, container, false);
        tx_heart_rate = fragmentViewHome.findViewById(R.id.show_data);
        rl_heart_rate=fragmentViewHome.findViewById(R.id.rl_heart_rate);

        tv_tiredFragment = fragmentViewHome.findViewById(R.id.tv_tired);
        //tv_tiredFragment.setText(temp);
        tv_blood_oxygenFragment = fragmentViewHome.findViewById(R.id.tv_blood_oxygen);
        //tv_blood_oxygenFragment.setText(oximetria);
        tv_blood_pressureFragment = fragmentViewHome.findViewById(R.id.tv_blood_pressure);
        //tv_blood_pressureFragment.setText(presS);
        tv_heart_rateFragment = fragmentViewHome.findViewById(R.id.tv_heart_rate);
        //  tv_heart_rateFragment.setText(ritmoCar);
        tv_desc_alarm = fragmentViewHome.findViewById(R.id.tv_desc_alarm);
        tv_title_alarm = fragmentViewHome.findViewById(R.id.tv_title_alarm);
        tv_title_alarm.setMovementMethod(new ScrollingMovementMethod());
        tv_desc_alarm.setMovementMethod(new ScrollingMovementMethod());
        getPhysiometryDataById();
        getLatestAlarmByUserId();

        rl_heart_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInformationFisiometria();
            }
        });

        return fragmentViewHome;

    }


    public void getInformationFisiometria(){
        System.out.println("cambio");
        Intent intent = new Intent(getActivity(), FisiometriaUserActivity.class);
        startActivity(intent);

    }


}
