package com.wakeup.bandsdk.activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Authenticate.JWTAuth;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;
import com.wakeup.bandsdk.activity.HomeActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    TextInputLayout loginUsernameField, loginPasswordField;
    TextInputEditText loginUsernameInput, loginPasswordInput;
    private JsonObject userCredentials = new JsonObject();
    private Object TextInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button directAccessBtn = findViewById(R.id.directAccess);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button toRegisterBtn = findViewById(R.id.toRegisterBtn);

        // Capturing form fields and input
        loginPasswordField = findViewById(R.id.loginPasswordField);
        loginUsernameInput = findViewById(R.id.loginUsernameText);
        loginUsernameField = findViewById(R.id.loginUsernameField);
        loginPasswordInput = findViewById(R.id.loginPasswordText);

        directAccessBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        });

        loginUsernameInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    userCredentials.addProperty("username", s.toString());
                } else {
                    loginUsernameField.setError("Este campo no debe estar vacío");
                }
            }
        });

        loginPasswordInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    userCredentials.addProperty("password", s.toString());
                } else {
                    loginPasswordField.setError("Este campo no debe estar vacío");
                }
            }
        });

        loginBtn.setOnClickListener( v -> {
            Log.d(TAG, "User credentials -> " + userCredentials);
            getJwtToken(this, userCredentials);
        });
        toRegisterBtn.setOnClickListener(v -> {
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }

    public void getJwtToken(Context loginContext, JsonObject credentials) {
        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<JWTAuth> responseData = service.getJwtToken(credentials);

        responseData.enqueue(new Callback<JWTAuth>() {
            @Override
            public void onResponse(Call<JWTAuth> call, Response<JWTAuth> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Log.i(TAG, "onResponse: " + response.body().toString());
                    getPhysiometryDataById(loginContext, response.body().getIdToken(), 3);
                }
                if (response.code() == 401) {
                    loginUsernameField.setError("Credenciales inválidas. Intentalo de nuevo.");
                    loginPasswordField.setError("Credenciales inválidas. Intentalo de nuevo.");
                }
            }

            @Override
            public void onFailure(Call<JWTAuth> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void getPhysiometryDataById(Context loginContext, String jwtToken, int userId) {
        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getStudiesSubjes("Bearer " + jwtToken, userId);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
            @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {
                if (response.isSuccessful()) {
                    List<DataFisiometria> res = response.body();
                    assert res != null;
                    System.out.println("Physiometry data size for this user -> " + res.size());
                    for (DataFisiometria c : res) {
                        System.out.println("User Id -> " + c.getdataUser().getId());
                        System.out.println("User email -> " + c.getdataUser().emailget());
                    }
                    Intent homeIntent = new Intent(loginContext, HomeActivity.class);
                    startActivity(homeIntent);
                }
            }

            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
