package com.wakeup.bandsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Pojos.Authenticate.JWTAuth;
import com.wakeup.bandsdk.Pojos.DataUser;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.AccountService;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    TextInputLayout registerUsernameField, registerPasswordField, registerEmailField, registerFirstNameField, registerLastNameField;
    TextInputEditText registerUsernameInput, registerPasswordInput, registerEmailInput, registerFirstNameInput, registerLastNameInput;
    private Context context = this;
    private JsonObject newUserCredentials = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerBtn = findViewById(R.id.registerBtn);

        registerUsernameField = findViewById(R.id.registerUsernameField);
        registerUsernameInput = findViewById(R.id.registerUsernameText);
        registerFirstNameField = findViewById(R.id.registerFirstNameField);
        registerFirstNameInput = findViewById(R.id.registerFirstNameText);
        registerLastNameField = findViewById(R.id.registerLastNameField);
        registerLastNameInput = findViewById(R.id.registerLastNameText);
        registerEmailField = findViewById(R.id.registerEmailField);
        registerEmailInput = findViewById(R.id.registerEmailText);
        registerPasswordField = findViewById(R.id.registerPasswordField);
        registerPasswordInput = findViewById(R.id.registerPasswordText);

        registerUsernameInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    registerUsernameField.setErrorEnabled(false);
                    newUserCredentials.addProperty("login", s.toString());
                } else {
                    registerUsernameField.setError("Este campo no debe estar vacío");
                }
            }
        });

        registerFirstNameInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    registerFirstNameField.setErrorEnabled(false);
                    newUserCredentials.addProperty("firstName", s.toString());
                } else {
                    registerFirstNameField.setError("Este campo no debe estar vacío");
                }
            }
        });

        registerLastNameInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    registerLastNameField.setErrorEnabled(false);
                    newUserCredentials.addProperty("lastName", s.toString());
                } else {
                    registerLastNameField.setError("Este campo no debe estar vacío");
                }
            }
        });

        registerEmailInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    registerEmailField.setErrorEnabled(false);
                    newUserCredentials.addProperty("email", s.toString());
                } else {
                    registerEmailField.setError("Este campo no debe estar vacío");
                }
            }
        });

        registerPasswordInput.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    registerPasswordField.setErrorEnabled(false);
                    newUserCredentials.addProperty("password", s.toString());
                } else {
                    registerPasswordField.setError("Este campo no debe estar vacío");
                }
            }
        });

        registerBtn.setOnClickListener(view -> {
            sendNewUserData(this, newUserCredentials);
        });
    }

    private void sendNewUserData(Context loginContext, JsonObject newUserCredentials) {
        newUserCredentials.addProperty("langKey", "es");
        Intent loginIntent = new Intent(this, LoginActivity.class);
        AccountService service = ConfigGeneral.retrofit.create(AccountService.class);
        final Call<Void> dataResponse = service.setUserData(newUserCredentials);

        dataResponse.enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    assert response.body() != null;
                    Snackbar.make(registerEmailField, "Usuario creado exitosamente!", BaseTransientBottomBar.LENGTH_INDEFINITE)
                            .setTextColor(getColor(R.color.e9))
                            .setBackgroundTint(getColor(R.color.g4))
                            .setAction("Aceptar", view -> startActivity(loginIntent))
                            .setActionTextColor(getColor(R.color.material_grey_900))
                            .show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getMessage());
                Snackbar.make(registerEmailField, "Ha ocurrido un error. Por favor intentalo de nuevo.", Snackbar.LENGTH_LONG)
                        .setTextColor(getColor(R.color.e9))
                        .setBackgroundTint(getColor(R.color.error_color_material_light))
                        .show();
            }
        });
    }
}
