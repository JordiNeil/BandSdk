package com.wakeup.bandsdk.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
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
    public TextView tv_tiredFragment;
    public TextView tv_heart_rateFragment;
    public TextView tv_blood_pressureFragment;
    public TextView tv_blood_oxygenFragment;
    String temp;
    String presS;
    String oximetria;
    String ritmoCar;
    String dateRegister;
    ArrayList<Integer> a;
    Context thiscontect;
    TextView rl_heart_rate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void getPhysiometryDataById() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");

        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryData("Bearer " + storedJwtToken, 3);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {


                if (response.isSuccessful()) {
                    List<DataFisiometria> res = response.body();

                    assert res != null;
                    ArrayList<Object> fetchedPhysiometryData = new ArrayList<>();
                    fetchedPhysiometryData.add(0, res.get(res.size() - 1).getRitmoCardiaco());
                    fetchedPhysiometryData.add(1, res.get(res.size() - 1).getOximetria());
                    fetchedPhysiometryData.add(2, res.get(res.size() - 1).getPresionArterialSistolica());
                    fetchedPhysiometryData.add(3, res.get(res.size() - 1).getPresionArterialDiastolica());
                    fetchedPhysiometryData.add(4, res.get(res.size() - 1).getTemperatura());
                    fetchedPhysiometryData.add(5, res.get(res.size() - 1).getFechaToma());
                    System.out.println(fetchedPhysiometryData.get(5).toString());

                    String fecha=fetchedPhysiometryData.get(5).toString();

                    int year=Integer.parseInt(fecha.split("T")[0].split("-")[0]);
                    int month=Integer.parseInt(fecha.split("T")[0].split("-")[1]);
                    int day=Integer.parseInt(fecha.split("T")[0].split("-")[2]);

                    int hour=Integer.parseInt(fecha.split("T")[1].split(":")[0]);
                    int minute=Integer.parseInt(fecha.split("T")[1].split(":")[1]);
//                    int second= Integer.parseInt(fecha.split("T")[1].split(":")[2].split(".")[0]);



                    String hora =fecha.split("T")[1];
                    System.out.println(hora.split(":")[0]);
                    Calendar calendario=Calendar.getInstance();

                    calendario.set(year,month,day,hour-5,minute,0);

                    dateRegister=getString(R.string.measure_recently)+": "+calendario.get(Calendar.YEAR)+"-"+calendario.get(Calendar.MONTH)+"-"+calendario.get(Calendar.DAY_OF_MONTH)+" " +
                            calendario.get(Calendar.HOUR)+":"+calendario.get(Calendar.MINUTE);




                    temp = fetchedPhysiometryData.get(4) + "°C";
                    presS = fetchedPhysiometryData.get(2) + "/" + fetchedPhysiometryData.get(3);
                    oximetria = fetchedPhysiometryData.get(1).toString();
                    ritmoCar = fetchedPhysiometryData.get(0).toString();
                    System.out.println(temp + presS + oximetria + ritmoCar);
                    tv_tiredFragment.setText(temp);
                    tv_blood_oxygenFragment.setText(oximetria);
                    tv_blood_pressureFragment.setText(presS);
                    tv_heart_rateFragment.setText(ritmoCar);

                    rl_heart_rate.setText(dateRegister);
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {
                System.out.println(t.getMessage());

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View fragmentViewHome = inflater.inflate(R.layout.fragmen_home, container, false);
        rl_heart_rate = fragmentViewHome.findViewById(R.id.show_data);

        tv_tiredFragment = fragmentViewHome.findViewById(R.id.tv_tired);
        //tv_tiredFragment.setText(temp);
        tv_blood_oxygenFragment = fragmentViewHome.findViewById(R.id.tv_blood_oxygen);
        //tv_blood_oxygenFragment.setText(oximetria);
        tv_blood_pressureFragment = fragmentViewHome.findViewById(R.id.tv_blood_pressure);
        //tv_blood_pressureFragment.setText(presS);
        tv_heart_rateFragment = fragmentViewHome.findViewById(R.id.tv_heart_rate);
        //  tv_heart_rateFragment.setText(ritmoCar);

        getPhysiometryDataById();

        return fragmentViewHome;

    }


}
