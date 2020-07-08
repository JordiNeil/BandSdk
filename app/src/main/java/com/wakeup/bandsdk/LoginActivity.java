package com.wakeup.bandsdk;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button toRegisterBtn = findViewById(R.id.toRegisterBtn);

        toRegisterBtn.setOnClickListener(v -> setContentView(R.layout.activity_register));
    }
}
