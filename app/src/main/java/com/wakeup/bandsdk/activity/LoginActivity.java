package com.wakeup.bandsdk.activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Authenticate.JWTAuth;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

        Button loginBtn = findViewById(R.id.loginBtn);
        Button toRegisterBtn = findViewById(R.id.toRegisterBtn);

        // Capturing form fields and input
        loginPasswordField = findViewById(R.id.loginPasswordField);
        loginUsernameInput = findViewById(R.id.loginUsernameText);
        loginUsernameField = findViewById(R.id.loginUsernameField);
        loginPasswordInput = findViewById(R.id.loginPasswordText);

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
            getJwtToken(userCredentials);
        });
        toRegisterBtn.setOnClickListener(v -> setContentView(R.layout.activity_register));
    }

    public void getJwtToken(JsonObject credentials) {
        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<JWTAuth> responseData = service.getJwtToken(credentials);

        responseData.enqueue(new Callback<JWTAuth>() {
            @Override
            public void onResponse(Call<JWTAuth> call, Response<JWTAuth> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Log.i(TAG, "onResponse: " + response.body().toString());
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
}
