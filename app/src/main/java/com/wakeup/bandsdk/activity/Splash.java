package com.wakeup.bandsdk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.wakeup.bandsdk.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPrefs = getApplication().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    if (!sharedPrefs.getString("storedJwtToken", "").equals("")) {
                        Intent homeIntent = new Intent(getApplication(), LoginActivity.class);
                        startActivity(homeIntent);
                    } else {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

}