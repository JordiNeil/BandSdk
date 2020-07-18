package com.wakeup.bandsdk.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.wakeup.bandsdk.Fragments.Fisiometria.temperature;
import com.wakeup.bandsdk.Fragments.HomeFragment;
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
    Fragment fragmentTemp = new temperature();
    RadioButton radioButtonTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisiometria_user);
        SharedPreferences sharedPrefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        userId = sharedPrefs.getInt(ConfigGeneral.STOREDUSERID, 0);
        radioButtonTemp = (RadioButton) findViewById(R.id.rb_ritmo_cardiaco);


    }


    public void fragmentTemp(View view) {

        if (radioButtonTemp.isChecked() == true) {
            System.out.println("cambio a home");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_container_fisiometria, fragmentTemp);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }




}