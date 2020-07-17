package com.wakeup.bandsdk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.Pojos.Conditions.ConditionData;
import com.wakeup.bandsdk.Pojos.Ips.IpsData;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ConditionService;
import com.wakeup.bandsdk.Services.IpsService;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context = this;
    SharedPreferences sharedPrefs;
    String storedJwtToken;
    public  static List<ConditionData> conditionDataList = new ArrayList<>();
    public  static List<IpsData> ipsDataList = new ArrayList<>();
    public static ConditionData selectedCondition;
    public static IpsData selectedIps;
    public Spinner idTypeSpinner, sexSpinner, conditionSpinner, ipsSpinner;
    public ArrayList<String> idType = new ArrayList<>();
    public ArrayList<String> sexArray = new ArrayList<>();
    private JsonObject allPatientData = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        sharedPrefs = context.getSharedPreferences(ConfigGeneral.preference_file_key, Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");

        // Form fields and button
        EditText fullNameInput = findViewById(R.id.fullNameInput);
        EditText idNumberInput = findViewById(R.id.idNumberInput);
        EditText ageInput = findViewById(R.id.ageInput);
        EditText weightInput = findViewById(R.id.weightInput);
        EditText heightInput = findViewById(R.id.heightInput);
        EditText oximetryRefInput = findViewById(R.id.oximetryRefInput);
        EditText temperatureRefInput = findViewById(R.id.temperatureRefInput);
        EditText hrRefInput = findViewById(R.id.hrRefInput);
        EditText systolicBpRefInput = findViewById(R.id.systolicBpRefInput);
        EditText diastolicBpRefInput = findViewById(R.id.diastolicBpRefInput);
        EditText medicationInput = findViewById(R.id.medicationInput);
        EditText commentsInput = findViewById(R.id.commentsInput);
        Button sendPatientDataBtn = findViewById(R.id.sendPatientDataBtn);

        // Form fields and button event handlers
        fullNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("nombre", editable.toString());
                } else {
                    fullNameInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        idNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("identificacion", editable.toString());
                } else {
                    idNumberInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        ageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("edad", editable.toString());
                } else {
                    ageInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("pesoKG", editable.toString());
                } else {
                    weightInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        heightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("estaturaCM", editable.toString());
                } else {
                    heightInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        oximetryRefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("oximetriaReferencia", editable.toString());
                } else {
                    oximetryRefInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        temperatureRefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("temperaturaReferencia", editable.toString());
                } else {
                    temperatureRefInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        hrRefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("ritmoCardiacoReferencia", editable.toString());
                } else {
                    hrRefInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        systolicBpRefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("presionSistolicaReferencia", editable.toString());
                } else {
                    systolicBpRefInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        diastolicBpRefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("presionDistolicaReferencia", editable.toString());
                } else {
                    diastolicBpRefInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        medicationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("medicacion", editable.toString());
                } else {
                    medicationInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        commentsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    allPatientData.addProperty("comentarios", editable.toString());
                } else {
                    commentsInput.setError(ConfigGeneral.INPUTVACIO);
                }
            }
        });
        sendPatientDataBtn.setOnClickListener(view -> {
            sendPatientData(storedJwtToken, selectedCondition, selectedIps);
        });

        // Spinners
        idTypeSpinner = (Spinner) findViewById(R.id.idTypeSpinner);
        idTypeSpinner.setOnItemSelectedListener(this);
        sexSpinner = (Spinner) findViewById(R.id.sexSpinner);
        sexSpinner.setOnItemSelectedListener(this);
        conditionSpinner = (Spinner) findViewById(R.id.conditionSpinner);
        conditionSpinner.setOnItemSelectedListener(this);
        ipsSpinner = (Spinner) findViewById(R.id.ipsSpinner);
        ipsSpinner.setOnItemSelectedListener(this);

        idType.add("CEDULA");
        idType.add("TARJETA");
        idType.add("IDENTIDAD");
        idType.add("PASAPORTE");
        idType.add("OTRO");

        sexArray.add("MASCULINO");
        sexArray.add("FEMENINO");
        sexArray.add("OTRO");

        ArrayAdapter idTypeAdapter = new ArrayAdapter(context,
                R.layout.list_item, idType);
        // Specify the layout to use when the list of choices appears
        idTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        idTypeSpinner.setAdapter(idTypeAdapter);

        ArrayAdapter sexArrayAdapter = new ArrayAdapter(context,
                R.layout.list_item, sexArray);
        // Specify the layout to use when the list of choices appears
        sexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sexSpinner.setAdapter(sexArrayAdapter);

        fetchConditionData(storedJwtToken);
    }

    public void fetchConditionData(String storedJwtToken) {
        ConditionService condition = ConfigGeneral.retrofit.create(ConditionService.class);
        final Call<List<ConditionData>> conditionRequest = condition.getConditionData("Bearer " + storedJwtToken);

        conditionRequest.enqueue(new Callback<List<ConditionData>>() {
            @Override
            public void onResponse(Call<List<ConditionData>> call, Response<List<ConditionData>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Fetch Condition Data response: " + response.body());
                    conditionDataList = response.body();
                    ArrayList<Integer> conditionIds = new ArrayList<>();
                    assert conditionDataList != null;
                    for (ConditionData condition: conditionDataList){
                        System.out.println(condition.getId());
                        conditionIds.add(condition.getId());
                    }
                    ArrayAdapter conditionAdapter = new ArrayAdapter(context, R.layout.list_item, conditionIds);
                    // Specify the layout to use when the list of choices appears
                    conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    conditionSpinner.setAdapter(conditionAdapter);
                    fetchIpsData(storedJwtToken);
                }
            }

            @Override
            public void onFailure(Call<List<ConditionData>> call, Throwable t) {
                Log.d(TAG, "Fetch Condition Data onFailure: " + t.getMessage());
            }
        });
    }

    public void fetchIpsData(String storedJwtToken) {
        IpsService ips = ConfigGeneral.retrofit.create(IpsService.class);
        final Call<List<IpsData>> ipsRequest = ips.getIpsData("Bearer " + storedJwtToken);

        ipsRequest.enqueue(new Callback<List<IpsData>>() {
            @Override
            public void onResponse(Call<List<IpsData>> call, Response<List<IpsData>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Fetch Ips Data response: " + response.body());
                    ipsDataList = response.body();
                    ArrayList<String> ipsNames = new ArrayList<>();
                    assert ipsDataList != null;
                    for (IpsData ips: ipsDataList){
                        System.out.println(ips.getNombre());
                        ipsNames.add(ips.getNombre());
                    }
                    ArrayAdapter ipsAdapter = new ArrayAdapter(context, R.layout.list_item, ipsNames);
                    // Specify the layout to use when the list of choices appears
                    ipsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    ipsSpinner.setAdapter(ipsAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<IpsData>> call, Throwable t) {
                Log.d(TAG, "Fetch Ips Data onFailure: " + t.getMessage());
            }
        });
    }

    public void sendPatientData(String jwtToken, ConditionData condition, IpsData ips) {
        // Mezclar allPatientData con condition, ips y datos del usuario guardados en local storage para enviarlos
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected: " + adapterView.getItemAtPosition(i));
        for (ConditionData condition: conditionDataList){
            if (condition.getId() == adapterView.getItemAtPosition(i)) {
                selectedCondition = condition;
                Log.d(TAG, "Selected condition: " + selectedCondition.getNombre());
            }
        }
        for (IpsData ips: ipsDataList){
            if (ips.getNombre() == adapterView.getItemAtPosition(i)) {
                selectedIps = ips;
                Log.d(TAG, "Selected ips: " + selectedIps.getNombre());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}