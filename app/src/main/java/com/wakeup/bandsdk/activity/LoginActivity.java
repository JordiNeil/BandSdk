package com.wakeup.bandsdk.activity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.Pojos.Authenticate.JWTAuth;
import com.wakeup.bandsdk.Pojos.DataUser;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.AccountService;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;
import com.wakeup.mylibrary.Config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.autofill.UserData;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText loginUsernameInput, loginPasswordInput;
    Button loginBtn;
    TextView toRegisterBtn;
    private JsonObject userCredentials = new JsonObject();
    private Context context = this;
    View viewAlert;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginBtn);
        toRegisterBtn = findViewById(R.id.tv_thrid_login);
        // Capturing form fields and input
        loginUsernameInput = findViewById(R.id.loginUsernameText);
        loginPasswordInput = findViewById(R.id.loginPasswordText);

       /* directAccessBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            startActivity(homeIntent);
        });*/

        //Loading
        viewAlert = LayoutInflater.from(this).inflate(R.layout.loading_data, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewAlert);
        builder.setCancelable(false);
        dialog = builder.create();

        // Check if there is user data in shared preferences
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPrefs.contains("storedJwtToken") && !sharedPrefs.getString("storedJwtToken", "").equals("")) {
            Log.d(TAG, "JWT Token: " + sharedPrefs.getString("storedJwtToken", ""));
            Intent homeIntent = new Intent(context, HomeActivity.class);
            startActivity(homeIntent);
        } else {
            Log.d(TAG, "No hay datos de token en shared preferences");
        }

        loginUsernameInput.addTextChangedListener(new TextWatcher() {
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
                    loginUsernameInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });

        loginPasswordInput.addTextChangedListener(new TextWatcher() {
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
                    loginPasswordInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });

        loginBtn.setOnClickListener(v -> {
            Log.d(TAG, "User credentials -> " + userCredentials);
            dialog.show();
            getJwtToken(context, userCredentials);
        });
        toRegisterBtn.setOnClickListener(v -> {
            Intent registerIntent = new Intent(this, HomeActivity.class);
            startActivity(registerIntent);
        });
    }

    public void getJwtToken(Context loginContext, JsonObject credentials) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        if (storedJwtToken != null || storedJwtToken.equals("")) {
            ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
            final Call<JWTAuth> responseData = service.getJwtToken(credentials);

            responseData.enqueue(new Callback<JWTAuth>() {
                @Override
                public void onResponse(Call<JWTAuth> call, Response<JWTAuth> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Log.i(TAG, "onResponse: " + response.body().toString());
                        editor.putString(ConfigGeneral.TOKENSHARED, response.body().getIdToken());
                        editor.commit();
                        fetchUserData(loginContext, response.body().getIdToken());

                    }
                    if (response.code() == 401) {
                        loginUsernameInput.setError(ConfigGeneral.ERRORINPUTS);
                        loginPasswordInput.setError(ConfigGeneral.ERRORINPUTS);
                        dialog.dismiss();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onFailure(Call<JWTAuth> call, Throwable t) {
                    System.out.println(t.getMessage());
                    Snackbar.make(loginPasswordInput, ConfigGeneral.ERRORGENERAL, Snackbar.LENGTH_LONG)
                            .setTextColor(getColor(R.color.e9))
                            .setBackgroundTint(getColor(R.color.error_color_material_light))
                            .show();
                    dialog.dismiss();
                }
            });
        } else {
            fetchUserData(loginContext, storedJwtToken);
        }
    }

    public void fetchUserData(Context loginContext, String jwtToken) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        AccountService service = ConfigGeneral.retrofit.create(AccountService.class);
        final Call<DataUser> dataResponse = service.getUserData("Bearer " + jwtToken);

        dataResponse.enqueue(new Callback<DataUser>() {
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.i(TAG, "onResponse: " + response.body().toString());

                    DataUser res = response.body();
                    assert res != null;
                    ArrayList<Object> fetchedUserData = new ArrayList<>();
                    fetchedUserData.add(0, res.getId());
                    fetchedUserData.add(1, res.getLogin());
                    fetchedUserData.add(2, res.getFirstName());
                    fetchedUserData.add(3, res.getLastName());
                    fetchedUserData.add(4, res.getEmail());
                    fetchedUserData.add(5, res.getImageUrl());
                    fetchedUserData.add(6, res.getActivated());
                    fetchedUserData.add(7, res.getLangKey());
                    fetchedUserData.add(8, res.getCreatedBy());
                    fetchedUserData.add(9, res.getCreatedDate());
                    fetchedUserData.add(10, res.getLastModifiedBy());
                    fetchedUserData.add(11, res.getLastModifiedDate());

                    // Storing necessary user data to shared preferences
                    editor.putInt("userId", res.getId());
                    editor.putString("login", res.getLogin());
                    editor.putString("firstName", res.getFirstName());
                    editor.putString("lastName", res.getLastName());
                    editor.putString("email", res.getEmail());
                    editor.putBoolean("activated", res.getActivated());
                    editor.putString("langKey", res.getLangKey());
                    editor.putString("imageUrl", res.getImageUrl());

                    Intent homeIntent = new Intent(loginContext, HomeActivity.class);
                    homeIntent.putExtra("fetchedUserData", fetchedUserData);
                    startActivity(homeIntent);
                    dialog.dismiss();
                }
                if (response.code() == 401) {
                    editor.remove(ConfigGeneral.TOKENSHARED);
                    editor.commit();
                    getJwtToken(context, userCredentials);
                    dialog.dismiss();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<DataUser> call, Throwable t) {
                System.out.println(t.getMessage());

                Snackbar.make(loginPasswordInput, ConfigGeneral.ERRORGENERAL, Snackbar.LENGTH_LONG)
                        .setTextColor(getColor(R.color.e9))
                        .setBackgroundTint(getColor(R.color.error_color_material_light))
                        .show();
//                if (Objects.requireNonNull(t.getMessage()).equals("timeout")) {
//                    Toast.makeText(context, "Ha ocurrido un error. Por favor intentalo de nuevo.", Toast.LENGTH_LONG);
//                }
                dialog.dismiss();
            }
        });
    }

    /*public void getPhysiometryDataById(Context loginContext, String jwtToken, int userId) {
        System.out.println("------------------------ENTRO-----------------");

        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryData("Bearer " + jwtToken, userId);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
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
                    System.out.println(fetchedPhysiometryData);

                }
                if (response.code() == 401) {

                    getJwtToken(context, userCredentials);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {
                System.out.println(t.getMessage());
                Snackbar.make(loginPasswordInput, ConfigGeneral.ERRORGENERAL, Snackbar.LENGTH_LONG)
                        .setTextColor(getColor(R.color.e9))
                        .setBackgroundTint(getColor(R.color.error_color_material_light))
                        .show();
            }
        });
    }*/
}
