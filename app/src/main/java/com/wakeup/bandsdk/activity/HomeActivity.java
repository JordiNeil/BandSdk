package com.wakeup.bandsdk.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wakeup.bandsdk.Fragments.HomeFragment;
import com.wakeup.bandsdk.Fragments.UserFragment;
import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.Pojos.Alarms.AlarmData;
import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.AlarmService;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;
import com.wakeup.mylibrary.Config;
import com.wakeup.mylibrary.bean.BandInfo;
import com.wakeup.mylibrary.bean.Battery;
import com.wakeup.mylibrary.bean.BloodOxygenBean;
import com.wakeup.mylibrary.bean.BloodPressureBean;
import com.wakeup.mylibrary.bean.BodyTempBean;
import com.wakeup.mylibrary.bean.BodytempAndMianyiBean;
import com.wakeup.mylibrary.bean.CurrentDataBean;
import com.wakeup.mylibrary.bean.HeartRateBean;
import com.wakeup.mylibrary.bean.HourlyMeasureDataBean;
import com.wakeup.mylibrary.bean.MianyiBean;
import com.wakeup.mylibrary.bean.OneButtonMeasurementBean;
import com.wakeup.mylibrary.bean.SleepData;
import com.wakeup.mylibrary.command.CommandManager;
import com.wakeup.mylibrary.data.DataParse;
import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.DataHandUtils;
import com.wakeup.mylibrary.utils.SPUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends MainActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public RadioButton radioButtonHome;
    public RadioButton radioButtonInfo;
    public RadioButton radioButtonUser;
    private BluetoothService mBluetoothLeService;
    private static final int REQUEST_SEARCH = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private String address;
    private CommandManager commandManager;
    private DataParse dataPasrse;
    private BandInfo bandInfo;
    public Button btnMeassre;
    private Context context = this;
    SharedPreferences sharedPrefs;
    String storedJwtToken, userLogin, userFirstName, userLastName, userEmail, userLangKey, userImageUrl;
    Integer userId;
    Boolean userActivated;

    private ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    Fragment fragmentHome = new HomeFragment();
    Bundle args = new Bundle();
    View viewAlert, viewAlertReceived;
    AlertDialog.Builder builder, builderReceived;
    AlertDialog dialog, dialogReciver;
    Boolean StatusConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefs = context.getSharedPreferences(ConfigGeneral.preference_file_key, Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        userId = sharedPrefs.getInt(ConfigGeneral.STOREDUSERID, 0);
        userLogin = sharedPrefs.getString(ConfigGeneral.STOREDUSERLOGIN, "");
        userFirstName = sharedPrefs.getString(ConfigGeneral.STOREDUSERFIRSTNAME, "");
        userLastName = sharedPrefs.getString(ConfigGeneral.STOREDUSERLASTNAME, "");
        userEmail = sharedPrefs.getString(ConfigGeneral.STOREDUSEREMAIL, "");
        userActivated = sharedPrefs.getBoolean(ConfigGeneral.STOREDUSERACTIVATED, false);
        userLangKey = sharedPrefs.getString(ConfigGeneral.STOREDUSERLANGKEY, "");
        userImageUrl = sharedPrefs.getString(ConfigGeneral.STOREDUSERIMAGEURL, "");

        radioButtonHome = (RadioButton) findViewById(R.id.rb_home);

        //radioButtonHome.callOnClick();
        radioButtonInfo = (RadioButton) findViewById(R.id.rb_discover);
        //radioButtonInfo.callOnClick();
        radioButtonUser = (RadioButton) findViewById(R.id.rb_mine);
        //radioButtonUser.callOnClick();

        viewAlert = LayoutInflater.from(this).inflate(R.layout.loading_data_measure, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewAlert);
        builder.setCancelable(false);
        dialog = builder.create();

        //DATARECEIVED
        viewAlertReceived = LayoutInflater.from(this).inflate(R.layout.alert_dialog_base_recived_data, null);
        builderReceived = new AlertDialog.Builder(this);
        builderReceived.setView(viewAlertReceived);
        builderReceived.setCancelable(false);
        dialogReciver = builderReceived.create();
        TextView txtMessage = viewAlertReceived.findViewById(R.id.messageAlert);

        Button btnConnection = viewAlertReceived.findViewById(R.id.btn_acep);
        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogReciver.dismiss();
            }
        });

//        btnMeassure = (Button) findViewById(R.id.button4);
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_fragment_container, fragmentHome);
        fragmentTransaction.commit();*/
        Log.d(TAG, "Fetched User Data: " + getIntent().getSerializableExtra("fetchedUserData"));
        Log.d(TAG, "onCreate: " + utc);
//        btnMeassure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Measure();
//            }
//        });
        // Alert data

    }


    public void fragmentHome(View view) {

        if (radioButtonHome.isChecked() == true) {
            System.out.println("cambio a home");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_container, fragmentHome);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }

    private void DialogAlertmeasure() {


    }

    public synchronized void fragmentInfo(View view) {

        if (StatusConnection) {
            dialog.show();
            meassure();
        } else {
            viewAlert = LayoutInflater.from(this).inflate(R.layout.alert_dialog_base, null);
            builder = new AlertDialog.Builder(this);
            builder.setView(viewAlert);
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
            TextView txtMessage = viewAlert.findViewById(R.id.messageAlert);
            txtMessage.setText(ConfigGeneral.SENDCONNECTION);
            Button btncancelConnection = viewAlert.findViewById(R.id.btn_cancel);
            Button btnConnection = viewAlert.findViewById(R.id.btn_Connection);
            btncancelConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });


        }

    }

    public void fragmentUser(View view) {
        Fragment fragmentUser = new UserFragment();
        Bundle args = new Bundle();
        if (radioButtonUser.isChecked() == true) {
            System.out.println("cambio a user");
            args.putString("a", "");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_container, fragmentUser);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    public boolean medidaCorrecta = false;
    public int numeroIntentos = 0;
    public boolean ponerManilla = false;


    public boolean conectado = false;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
                MainActivity.bandInfo = true;
//                progressBar.setVisibility(View.GONE);
                /*Intent intentHome = new Intent(context, HomeActivity.class);
                intentHome.putExtra("address", address);
                startActivity(intentHome);*/
                if (!StatusConnection) {
                    /**
                     *
                     *
                     * INICIO MEDICIÓN AUTOMÁTICA DE BATERÍA CADA HORA
                     *
                     */
                    medirBateria();



                    /**
                     *
                     *
                     * ABRIR MEDICION CADA HORA
                     */
                    iniciarMedicionHora();
                }

                /**
                 *
                 * SINCRONIZACIÓN DE HORA
                 *
                 */
                sincronizarHora();

                StatusConnection=true;
                conectado=true;

                /**
                 *
                 * INICIO MEDICIÓN AUTOMÁTICA DEL NIVEL DE BATERÍA
                 **/
                Timer timer;
                timer = new Timer();

                TimerTask batteryInfo = new TimerTask() {
                    @Override
                    public void run() {
//                        commandManager.getBatteryInfo();
                    }
                };


            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");

                System.out.println("DESCONECTADO POR USUARIO= "+desconectadoPorUsuario);
                conectado=false;
                Timer timer;
                timer = new Timer();

                TimerTask verificarConexion = new TimerTask() {
                    @Override
                    public void run() {
                        TimerTask reintentarConexion = new TimerTask() {
                            @Override
                            public void run() {
                                if (!conectado) {
                                    conectarBluetooth();
                                    System.out.println("REINTENTANDO CONEXIÓN");
                                    desconectadoPorUsuario=false;
                                }
                            }
                        };
                        if (!desconectadoPorUsuario) {
                            timer.schedule(reintentarConexion, 0);
                        }
                    }
                };

                timer.schedule(verificarConexion, 60000, 600000);


            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");


            } else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                if (calendar.get(Calendar.MINUTE) >= 10 && calendar.get(Calendar.SECOND) >= 10) {
                    Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                            + " at " + (calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                            (calendar.get(Calendar.MINUTE)) + ":" +
                            (calendar.get(Calendar.SECOND)));
                } else if (calendar.get(calendar.MINUTE) >= 10 && calendar.get(Calendar.SECOND) < 10) {
                    Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                            + " at " + (calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                            (calendar.get(Calendar.MINUTE)) + ":0" +
                            (calendar.get(Calendar.SECOND)));
                } else if (calendar.get(calendar.MINUTE) < 10 && calendar.get(Calendar.SECOND) >= 10) {
                    Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                            + " at " + (calendar.get(Calendar.HOUR_OF_DAY)) + ":0" +
                            (calendar.get(Calendar.MINUTE)) + ":" +
                            (calendar.get(Calendar.SECOND)));
                } else {
                    {
                        Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                                + " at " + (calendar.get(Calendar.HOUR_OF_DAY)) + ":0" +
                                (calendar.get(Calendar.MINUTE)) + ":0" +
                                (calendar.get(Calendar.SECOND)));
                    }
                }
                ArrayList<Integer> datas = DataHandUtils.bytesToArrayList(txValue);


                if (datas.size() == 0) {
                    return;
                }

                if (datas.get(0) == 0xAB) {
                    switch (datas.get(4)) {
                        case 0x91:
//                            BATTERY POWER
//                            Battery battery = (Battery) dataPasrse.parseData(datas);
//                            Log.i(TAG, battery.toString());
                            nivelBateria(datas);
                            break;
                        case 0x92:
                            //BRACELET DATA
//                            BandInfo bandInfo = (BandInfo) dataPasrse.parseData(datas);
//                            Log.i(TAG, bandInfo.toString());
//                            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);

//                            if (bandInfo.getBandType() == 0x0B
//                                    || bandInfo.getBandType() == 0x0D
//                                    || bandInfo.getBandType() == 0x0E
//                                    || bandInfo.getBandType() == 0x0F) {
//
//
//                                if (bandInfo.getBandType() == 0x0B
//                                        || bandInfo.getBandType() == 0x0D
//                                        || bandInfo.getBandType() == 0x0E
//                                        || bandInfo.getBandType() == 0x0F) {


//                                    Config.hasContinuousHeart = true;
//
//                                }
//                            }
//
                            break;

                        case 0x51:

                            switch (datas.get(5)) {
                                case 0x11:
                                    //STAND-ALONE MEASUREMENT OF HEART RATE DATA

                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, heartRateBean.toString());
                                    break;
                                case 0x12:
                                    //STAND-ALONE MEASUREMENT OF BLOOD OXYGEN RATE DATA
                                    BloodOxygenBean bloodOxygenBean = (BloodOxygenBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodOxygenBean.toString());
                                    break;
                                case 0x14:
                                    //STAND-ALONE MEASUREMENT OF BLOOD PRESSURE
                                    BloodPressureBean bloodPressureBean = (BloodPressureBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodPressureBean.toString());
                                    break;
                                case 0x08:
                                    //CURRENT DATA
//                                    CurrentDataBean currentDataBean = (CurrentDataBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, currentDataBean.toString());
                                    System.out.println("CURRENT DATA: " + datas);
                                    break;

                                case 0x20:
                                    //RETURN HOURLY DATA
//
                                    System.out.println("HOURLY DATA: " + datas);
                                    HourlyMeasureDataBean hourlyMeasureDataBean = (HourlyMeasureDataBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, hourlyMeasureDataBean.toString());
                                    System.out.println(datas);

                                    /**
                                     *
                                     * DATOS DE LA MEDICIÓN CADA HORA
                                     *
                                     */
//                                    while (!medidaCorrecta) {
////
//                                        if (datas.get(6) == 0 || datas.get(7) == 0 || datas.get(8) == 0 || datas.get(9) == 0 ||
//                                                datas.get(10) == 0 || datas.get(11) == 0) {
//                                            Log.i(TAG, "WRONG MEASURE, WILL TRY AGAIN IN 5 MIN");
//                                            dialog.dismiss();
//                                            numeroIntentos++;
//                                            if (numeroIntentos < 3) {
//                                                dialog.dismiss();
//                                            } else {
//                                                numeroIntentos = 0;
//                                                medicionCorrecta = true;
//                                                ponerManilla = true;
//                                                Log.i(TAG, "POR FAVOR PONERSE LA MANILLA");
//                                            }
//                                        } else {
//                                            medicionCorrecta = true;
//                                            dialog.dismiss();
//                                        }
//                                    }


                                    break;
                                case 0x21:
                                    //BODY TEMPERATURE AND IMMUNITY DATA
//                                    BodytempAndMianyiBean bodytempAndMianyiBean = (BodytempAndMianyiBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bodytempAndMianyiBean.toString());
                                    System.out.println("BODY TEMPERATURE AND IMMUNITY: " + datas);
                                    break;

                                case 0x13:
                                    //STAND-ALONE BODY TEMPERATURE MEASUREMENT
//                                    BodyTempBean bodyTempBean = (BodyTempBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bodyTempBean.toString());
                                    System.out.println("BODY TEMPERATURE MEASUREMENT: " + datas);
                                    break;

                                case 0x18:
                                    //RETURN TO STAND-ALONE IMMUNITY MEASUREMENT
//                                    MianyiBean mianyiBean = (MianyiBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, mianyiBean.toString());
                                    System.out.println("IMMUNITY MEASUREMENT: " + datas);
                                    break;

                            }


                            break;
                        case 0x52:
                            //SLEEP TIME RECORD
//                            SleepData sleepData = (SleepData) dataPasrse.parseData(datas);
//                            Log.i(TAG, sleepData.toString());
                            System.out.println("SLEEP TIME RECORD: " + datas);
                            break;

                        case 0x31:
                            //SINGLE MEASUREMENT, REAL TIME MEASUREMENT
                            switch (datas.get(5)) {
                                case 0x09:
                                    //HEART RATE(SINGLE)
                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, heartRateBean.toString());
                                    break;
                                case 0x11:
                                    //BLOOD OXYGEN (SINGLE)
                                    BloodOxygenBean bloodOxygenBean = (BloodOxygenBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodOxygenBean.toString());
                                    break;
                                case 0x21:
                                    //BLOOD PRESSURE (SINGLE)
                                    BloodPressureBean bloodPressureBean = (BloodPressureBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodPressureBean.toString());

                                    break;
                                case 0X0A:
                                    //HEART RATE (REAL-TIME)
                                    HeartRateBean heartRateBean1 = (HeartRateBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, heartRateBean1.toString());
                                    break;
                                case 0x12:
                                    //BLOOD OXYGEN(REAL-TIME)
                                    BloodOxygenBean bloodOxygenBean1 = (BloodOxygenBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodOxygenBean1.toString());
                                    break;
                                case 0x22:
                                    //BLOOD PRESSURE(REAL-TIME)
                                    BloodPressureBean bloodPressureBean1 = (BloodPressureBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodPressureBean1.toString());

                                    break;
                                case 0x81:
                                    //SINGLE TEMPERATURE MEASUREMENT
                                    BodyTempBean bodyTempBean = (BodyTempBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bodyTempBean.toString());
                                    break;
                                case 0x41:
                                    //SINGLE MEASUREMENT OF IMMUNITY
                                    MianyiBean mianyiBean = (MianyiBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, mianyiBean.toString());

                                    break;
                            }

                            break;
                        case 0x32:
                            //ONE-CLICK MEASUREMENT
//                            OneButtonMeasurementBean oneButtonMeasurementBean = (OneButtonMeasurementBean) dataPasrse.parseData(datas);}
                            args.putIntegerArrayList("DataMeasure", datas);
                            fragmentHome.setArguments(args);
                            System.out.println("-----------" + datas);

                            System.out.println("---------" + datas);

                            //Log.i(TAG, oneButtonMeasurementBean.toString());

                            /**
                             *
                             * SI LA MEDICIÓN RETORNA VALORES NO VÁLIDOS (NULOS O CEROS) SE DEBE VOLVER A HACER
                             *
                             */

//
                            if (!medidaCorrecta) {
//
                                if (datas.get(6) == 0 || datas.get(7) == 0 || datas.get(8) == 0 || datas.get(9) == 0 ||
                                        datas.get(10) == 0 || datas.get(11) == 0) {
                                    dialog.dismiss();
                                    numeroIntentos++;
                                    if (numeroIntentos < 3) {
                                        Log.i(TAG, "WRONG MEASURE, WILL TRY AGAIN IN 5 MIN (" + numeroIntentos + "/3).");
                                        retryMeasure();
                                    } else {
                                        numeroIntentos = 0;
                                        medicionCorrecta = true;
                                        ponerManilla = true;
                                        Log.i(TAG, "POR FAVOR PONERSE LA MANILLA");

                                    }
                                } else {
                                    medicionCorrecta = true;
                                    dialog.dismiss();

                                    int ritmoCardiaco = datas.get(6);
                                    int oxigenoSangre = datas.get(7);
                                    int presionAlta = datas.get(8);
                                    int presionBaja = datas.get(9);
                                    double temperatura = datas.get(11) + datas.get(12) * Math.pow(10, -2);

                                    int[] medidasCorrectas = new int[7];


                                    for (int i = 0; i < medidasCorrectas.length; i++) {
                                        medidasCorrectas[i] = 0;
                                    }

                                    /**
                                     * VALIDACIÓN DE RITMO CARDIACO
                                     *
                                     */

                                    if (ritmoCardiaco < 60 || ritmoCardiaco > 100) {
                                        System.out.println("EL RITMO CARDIACO ESTÁ POR FUERA DE LOS RANGOS NORMALES (60 BPM - 100 BPM)");
                                        dataAlarm(ConfigGeneral.DESC_RM, ConfigGeneral.TITLE_ALARM);
                                    } else {
                                        medidasCorrectas[0] = ritmoCardiaco;
                                    }

                                    /**
                                     *
                                     * VALIDACIÓN DE NIVEL DE OXÍGENO
                                     */

                                    if (oxigenoSangre < 95) {
                                        System.out.println("EL NIVEL DE OXÍGENO ESTÁ POR FUERA DE LOS RANGOS NORMALES (95% - 100%)");
                                        dataAlarm(ConfigGeneral.DESC_OXIME, ConfigGeneral.TITLE_ALARM);
                                    } else {
                                        medidasCorrectas[1] = oxigenoSangre;
                                    }

                                    /**
                                     * VALIDACIÓN DE PRESIÓN SANGUÍNEA
                                     */

                                    if (presionAlta < 80 || presionAlta > 120 || presionBaja < 60 || presionBaja > 80) {

                                        System.out.println("LA PRESIÓN SANGUÍNEA ESTÁ POR FUERA DE LOS RANGOS NORMALES (SISTÓLICA 80mmHg - 120mmHg, DIASTÓLICA 60mmHg - 80 mmHg");
                                        dataAlarm(ConfigGeneral.DESC_PS, ConfigGeneral.TITLE_ALARM);


                                    } else {
                                        medidasCorrectas[2] = presionAlta;
                                        medidasCorrectas[3] = presionBaja;
                                    }

                                    /**
                                     * VALIDACIÓN INMUNE
                                     */
                                    medidasCorrectas[4] = datas.get(10);

                                    /**
                                     * VALIDACIÓN DE TEMPERATURA
                                     */

                                    if (temperatura < 36 || temperatura > 37.2) {
                                        System.out.println("LA TEMPERATURA ESTÁ POR FUERA DE LOS RANGOS NORMALES (36°C - 37.2°C)");
                                        dataAlarm(ConfigGeneral.DES_TEMP, ConfigGeneral.TITLE_ALARM);
                                    } else {
                                        medidasCorrectas[5] = datas.get(11);
                                        medidasCorrectas[6] = datas.get(12);
                                    }
                                    mixUserAndPhysiometryData(datas);
                                    // System.out.println("nuevas medidas"+medidasCorrectas);
                                }
                            }
                            break;

                        case 0x84:
                            //CONTINUOUS HEART RATE, BRACELET REAL-TIME HEART RATE RETURN
                            HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
                            Log.i(TAG, heartRateBean.toString());
                            break;
                        default:
                            break;
                    }
                }

            }
        }
    };


    //Code to manage Service lifecycle
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                return;
            }
            Log.e(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mBluetoothLeService = null;

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(HomeActivity.this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 蓝牙是否开启
     *
     * @return
     */
    public boolean isBLEEnabled() {
        final BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = manager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }


    /**
     * 打开gps
     */
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.scanner_permission_rationale)
                .setCancelable(false)
                .setPositiveButton(R.string.open_gps,
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    /**
     * 请求开启蓝牙
     */
    public void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }


    /**
     * 是否支持蓝牙
     */
    public void isBLESupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast(R.string.no_ble);

        }
    }

    public void showToast(final int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_SEARCH && resultCode == RESULT_OK) {
            address = data.getStringExtra("address");
            String name = data.getStringExtra("name");

            Log.i(TAG, "name: " + name + "\n" + "address: " + address);


            SPUtils.putString(HomeActivity.this, SPUtils.ADDRESS, address);
        }
    }


    public boolean medicionCorrecta = false;


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mServiceConnection);
        //unregisterReceiver(mGattUpdateReceiver);
    }

    public void vibrate(View view) {
        commandManager.vibrate();
    }


    /**
     * SINGLE MEASUREMENT-AFTER 45S MEASUREMENT RESULTS WILL BE RETURNED
     *
     * @param view
     */
    private boolean transfer = true;


    //-------------------------------------------OWN FUNCTIONS------------------------------------------


    public void mixUserAndPhysiometryData(ArrayList<Integer> measuredPhysiometryData) {
//      Log.d(TAG, "Fetched User Data: " + getIntent().getSerializableExtra("fetchedUserData"));
        ArrayList<Object> fetchedUserData;
        fetchedUserData = getIntent().hasExtra("fetchedUserData") ? (ArrayList<Object>) getIntent().getSerializableExtra("fetchedUserData") : null;

        if (fetchedUserData != null) {
            // Defining Physiometry object and adding data to it
            JsonObject physiometryData = new JsonObject();
            physiometryData.addProperty("ritmoCardiaco", measuredPhysiometryData.get(10));
            physiometryData.addProperty("oximetria", measuredPhysiometryData.get(7));
            physiometryData.addProperty("presionArterialSistolica", measuredPhysiometryData.get(8));
            physiometryData.addProperty("presionArterialDiastolica", measuredPhysiometryData.get(9));
            physiometryData.addProperty("temperatura", measuredPhysiometryData.get(11) + "." + measuredPhysiometryData.get(12));
            physiometryData.addProperty("fechaRegistro", utc.toString());
            physiometryData.addProperty("fechaToma", utc.toString());
            // Defining userData object to store the user data from login activity
            JsonObject userData = new JsonObject();
            userData.addProperty("id", (Number) fetchedUserData.get(0));
            userData.addProperty("login", (String) fetchedUserData.get(1));
            userData.addProperty("firstName", (String) fetchedUserData.get(2));
            userData.addProperty("lastName", (String) fetchedUserData.get(3));
            userData.addProperty("email", (String) fetchedUserData.get(4));
            userData.addProperty("imageUrl", (String) fetchedUserData.get(5));
            userData.addProperty("activated", (Boolean) fetchedUserData.get(6));
            userData.addProperty("langKey", (String) fetchedUserData.get(7));
            // Adding userData object to Physiometry object
            physiometryData.add("user", userData);
            // Sending physiometry data to the service
            sendPhysiometryData(storedJwtToken, physiometryData);
        } else {

            Log.d(TAG, "mixUserAndPhysiometryData -> No hay datos de usuario desde login");
        }
    }

    public void sendPhysiometryData(String jwtToken, JsonObject data) {
        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<DataFisiometria> responseData = service.setPhysiometryData("Bearer " + jwtToken, data);

        responseData.enqueue(new Callback<DataFisiometria>() {
            @Override
            public void onResponse(Call<DataFisiometria> call, Response<DataFisiometria> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    dialogReciver.show();
                    Log.i(TAG, "onResponse: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<DataFisiometria> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void dataAlarm(String descAlarm, String titleAlarm) {
        JsonObject alertData = new JsonObject();
        alertData.addProperty("descripcion", descAlarm);
        alertData.addProperty("procedimiento", titleAlarm);
        alertData.addProperty("timeInstant", utc.toString());
        // User data
        JsonObject userData = new JsonObject();
        userData.addProperty("id", userId);
        userData.addProperty("login", userLogin);
        userData.addProperty("firstName", userFirstName);
        userData.addProperty("lastName", userLastName);
        userData.addProperty("email", userEmail);
        userData.addProperty("imageUrl", userImageUrl);
        userData.addProperty("activated", userActivated);
        userData.addProperty("langKey", userLangKey);
        userData.addProperty("resetDate", (String) null);
        // Mixing alert data with user data
        alertData.add("user", userData);

        createNewAlert(storedJwtToken, alertData);
    }

    public void createNewAlert(String jwtToken, JsonObject alarmData) {
        AlarmService alarm = ConfigGeneral.retrofit.create(AlarmService.class);
        final Call<AlarmData> response = alarm.setAlarm("Bearer " + jwtToken, alarmData);

        response.enqueue(new Callback<AlarmData>() {
            @Override
            public void onResponse(Call<AlarmData> call, Response<AlarmData> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Alarm response: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<AlarmData> call, Throwable t) {
                Log.d(TAG, "Alarm onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        address = SPUtils.getString(HomeActivity.this, SPUtils.ADDRESS, "");
    }
}