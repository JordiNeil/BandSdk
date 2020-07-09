package com.wakeup.bandsdk.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.activity.HomeActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    public TextView tv_tiredFragment;
    public TextView tv_heart_rateFragment;
    public TextView tv_blood_pressureFragment;
    public TextView tv_blood_oxygenFragment;
    String temp;
    String presS;
    String oximetria;
    String ritmoCar;
    ArrayList<Integer> a;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null) {
            a = getArguments().putAll().getIntegerArrayList("DataMeasure");
            System.out.println("estas en home Fragmen" + a);
            temp = Integer.toString(a.get(11)) + "." + Integer.toString(a.get(12))+"°C";
            presS = Integer.toString(a.get(8)) + "/" + Integer.toString(a.get(9));
            oximetria = Integer.toString(a.get(7));
            ritmoCar = Integer.toString(a.get(10));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentViewHome = inflater.inflate(R.layout.fragmen_home, container, false);
        tv_tiredFragment = fragmentViewHome.findViewById(R.id.tv_tired);
        tv_tiredFragment.setText(temp);
        tv_blood_oxygenFragment = fragmentViewHome.findViewById(R.id.tv_blood_oxygen);
        tv_blood_oxygenFragment.setText(oximetria);
        tv_blood_pressureFragment = fragmentViewHome.findViewById(R.id.tv_blood_pressure);
        tv_blood_pressureFragment.setText(presS);
        tv_heart_rateFragment = fragmentViewHome.findViewById(R.id.tv_heart_rate);
        tv_heart_rateFragment.setText(ritmoCar);

        return fragmentViewHome;

    }


}
