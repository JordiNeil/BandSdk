package com.wakeup.bandsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.app.AlertDialog.Builder;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wakeup.bandsdk.activity.DeviceScanActivity;
import com.wakeup.bandsdk.activity.HomeActivity;
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
import com.wakeup.mylibrary.constants.Constants;
import com.wakeup.mylibrary.constants.MessageID;
import com.wakeup.mylibrary.constants.MessageType;
import com.wakeup.mylibrary.data.DataParse;
import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.DataHandUtils;
import com.wakeup.mylibrary.utils.SPUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView mTextMessage, tv_connect_state;
    private static final int REQUEST_SEARCH = 1;
    private BluetoothService mBluetoothLeService;

    private String address;
    private Button connectBt;
    private ProgressBar progressBar;
    private CommandManager commandManager;
    private DataParse dataPasrse;
    public static Boolean bandInfo = false;
    private ImageView imgConecct;

    View viewAlert;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextMessage = (TextView) findViewById(R.id.message);
        connectBt = findViewById(R.id.connect);
        imgConecct = findViewById(R.id.iv_band_connected);
        tv_connect_state = findViewById(R.id.tv_connect_state);

        // Button loginBtn = findViewById(R.id.goToLogin);
        //progressBar = findViewById(R.id.progressBar);
        viewAlert = LayoutInflater.from(this).inflate(R.layout.loading_data, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewAlert);
        builder.setCancelable(false);
        dialog = builder.create();

        isBLESupported();

        //开启蓝牙
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
        //6.0以上开启定位
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }
        }

        /*goToLoginBtn.setOnClickListener(v -> {
            Log.d(TAG, "To Login Activity...");
            setContentView(R.layout.activity_login);
        });*/

        @Nullable
        //启动蓝牙服务
                Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        commandManager = CommandManager.getInstance(this);

        dataPasrse = DataParse.getInstance();

    }

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
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
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

            mTextMessage.setText("name: " + name + "\n" + "address: " + address);

            SPUtils.putString(MainActivity.this, SPUtils.ADDRESS, address);
        }
    }

    public boolean desconectadoPorUsuario = false;

    public void connect(View view) {
        conectarBluetooth();
//        System.out.println("DESCONECTADO POR USUARIO = FALSE");
        showDialog();


    }

    public boolean medicionCorrecta = false;

    private final BroadcastReceiver mGattUpdateReceiverConnect = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
                desconectadoPorUsuario = false;

                dialog.dismiss();
            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                hideDialog();
                tv_connect_state.setText("Desconectado");
                imgConecct.setBackgroundResource(R.drawable.band_unconnect);

            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                dialog.dismiss();
                imgConecct.setBackgroundResource(R.drawable.band_connected);
                tv_connect_state.setText("Conectado");
            }
        }
    };

//    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
//                Log.i(TAG, "ACTION_GATT_CONNECTED");
//                connectBt.setText("DISCONNECT");
//                imgConecct.setBackgroundResource(R.drawable.band_connected);
//                tv_connect_state.setText("Conectado");
////                progressBar.setVisibility(View.GONE);
//                Intent intentHome = new Intent(context, HomeActivity.class);
//                intentHome.putExtra("address",address);
//                startActivity(intentHome);
//
//                /**
//                 *
//                 * INICIO MEDICIÓN AUTOMÁTICA DEL NIVEL DE BATERÍA
//                 */
//
//                /**
//                 *
//                 * INICIO MEDICIÓN AUTOMÁTICA CADA HORA
//                 */
//
//
//
//                /**
//                 *
//                 * SINCRONIZACIÓN DE TIEMPO
//                 *
//                 */
//
//
//
//            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
//                connectBt.setText("CONNECTION");
//                imgConecct.setBackgroundResource(R.drawable.band_unconnect);
//                tv_connect_state.setText("Desconectado");
//
//                //progressBar.setVisibility(View.GONE);
//
//
//            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
//
//
//            } else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
//                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
//                +" at "+(calendar.get(Calendar.HOUR_OF_DAY)) +":"+
//                        (calendar.get(Calendar.MINUTE))+":"+
//                        (calendar.get(Calendar.SECOND)));
//                List<Integer> datas = DataHandUtils.bytesToArrayList(txValue);
//                if (datas.size() == 0) {
//                    return;
//                }
//
//                if (datas.get(0) == 0xAB) {
//                    switch (datas.get(4)) {
//                        case 0x91:
//                            //BATTERY POWER
//                            Battery battery = (Battery) dataPasrse.parseData(datas);
//                            Log.i(TAG, battery.toString());
//                            break;
//                        case 0x92:
//                            //BRACELET DATA
//                            bandInfo = (BandInfo) dataPasrse.parseData(datas);
//                            Log.i(TAG, bandInfo.toString());
//                            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);
//
//
//                            if (bandInfo.getBandType() == 0x0B
//                                    || bandInfo.getBandType() == 0x0D
//                                    || bandInfo.getBandType() == 0x0E
//                                    || bandInfo.getBandType() == 0x0F) {
//
//                                Config.hasContinuousHeart = true;
//
//                            }
//
//
//                            break;
//                        case 0x51:
//
//                            switch (datas.get(5)) {
//                                case 0x11:
//                                    //STAND-ALONE MEASUREMENT OF HEART RATE DATA
//                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, heartRateBean.toString());
//                                    break;
//                                case 0x12:
//                                    //STAND-ALONE MEASUREMENT OF BLOOD OXYGEN RATE DATA
//                                    BloodOxygenBean bloodOxygenBean = (BloodOxygenBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodOxygenBean.toString());
//                                    break;
//                                case 0x14:
//                                    //STAND-ALONE MEASUREMENT OF BLOOD PREASURE
//                                    BloodPressureBean bloodPressureBean = (BloodPressureBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodPressureBean.toString());
//                                    break;
//                                case 0x08:
//                                    //CURRENT DATA
//                                    CurrentDataBean currentDataBean = (CurrentDataBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, currentDataBean.toString());
//                                    break;
//
//                                case 0x20:
//                                    //RETURN HOURLY DATA
//                                    HourlyMeasureDataBean hourlyMeasureDataBean = (HourlyMeasureDataBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, hourlyMeasureDataBean.toString());
//                                    break;
//                                case 0x21:
//                                    //BODY TEMPERATURE AND IMMUNITY DATA
//                                    BodytempAndMianyiBean bodytempAndMianyiBean = (BodytempAndMianyiBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bodytempAndMianyiBean.toString());
//                                    break;
//
//                                case 0x13:
//                                    //STAND-ALONE BODY TEMPERATURE MEASUREMENT
//                                    BodyTempBean bodyTempBean = (BodyTempBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bodyTempBean.toString());
//                                    break;
//
//                                case 0x18:
//                                    //RETURN TO STAND-ALONE IMMUNITY MEASUREMENT
//                                    MianyiBean mianyiBean = (MianyiBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, mianyiBean.toString());
//                                    break;
//
//                            }
//
//
//                            break;
//                        case 0x52:
//                            //SLEEP TIME RECORD
//                            SleepData sleepData = (SleepData) dataPasrse.parseData(datas);
//                            Log.i(TAG, sleepData.toString());
//
//                            break;
//
//                        case 0x31:
//                            //SINGLE MEASUREMENT, REAL TIME MEASUREMENT
//                            switch (datas.get(5)) {
//                                case 0x09:
//                                    //HEART RATE(SINGLE)
//                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, heartRateBean.toString());
//                                    break;
//                                case 0x11:
//                                    //BLOOD OXYGEN (SINGLE)
//                                    BloodOxygenBean bloodOxygenBean = (BloodOxygenBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodOxygenBean.toString());
//                                    break;
//                                case 0x21:
//                                    //BLOOD PRESSURE (SINGLE)
//                                    BloodPressureBean bloodPressureBean = (BloodPressureBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodPressureBean.toString());
//
//                                    break;
//                                case 0X0A:
//                                    //HEART RATE (REAL-TIME)
//                                    HeartRateBean heartRateBean1 = (HeartRateBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, heartRateBean1.toString());
//                                    break;
//                                case 0x12:
//                                    //BLOOD OXYGEN(REAL-TIME)
//                                    BloodOxygenBean bloodOxygenBean1 = (BloodOxygenBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodOxygenBean1.toString());
//                                    break;
//                                case 0x22:
//                                    //BLOOD PRESSURE(REAL-TIME)
//                                    BloodPressureBean bloodPressureBean1 = (BloodPressureBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bloodPressureBean1.toString());
//
//                                    break;
//                                case 0x81:
//                                    //SINGLE TEMPERATURE MEASUREMENT
//                                    BodyTempBean bodyTempBean = (BodyTempBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, bodyTempBean.toString());
//                                    break;
//                                case 0x41:
//                                    //SINGLE MEASUREMENT OF IMMUNITY
//                                    MianyiBean mianyiBean = (MianyiBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, mianyiBean.toString());
//
//                                    break;
//                            }
//
//                            break;
//                        case 0x32:
//                            //ONE-CLICK MEASUREMENT
//                            OneButtonMeasurementBean oneButtonMeasurementBean = (OneButtonMeasurementBean) dataPasrse.parseData(datas);
//
//                            System.out.println("-----------" + datas);
//
//                            System.out.println("---------" + datas);
//
//                            Log.i(TAG, oneButtonMeasurementBean.toString());
//
//                            break;
//
//                        case 0x84:
//                            //CONTINUOUS HEART RATE, BRACELET REAL-TIME HEART RATE RETURN
//                            HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
//                            Log.i(TAG, heartRateBean.toString());
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//            }
//        }
//    };


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiverConnect, makeGattUpdateIntentFilter());

        address = SPUtils.getString(MainActivity.this, SPUtils.ADDRESS, "");
        mTextMessage.setText(address);

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unbindService(mServiceConnection);
        // unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
//        unregisterReceiver(mGattUpdateReceiver);

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

    public synchronized void single_heartRate(View view) {


        meassure();
    }

    //-------------------------------------------OWN FUNCTIONS------------------------------------------
    public void meassure() {
        Measure();
    }

    //-------------------------------------------OWN FUNCTIONS------------------------------------------
    public synchronized void Measure() {
        /**
         *
         * DECLARACIÓN DEL TIMER PARA CORRER LAS MEDICIONES.
         */
        Timer timer;
        timer = new Timer();


        /**
         *
         *CREACIÓN DE LAS TAREAS PARA LAS MEDICIONES
         */

        TimerTask finishMeasure = new TimerTask() {
            @Override
            public void run() {
                commandManager.oneButtonMeasurement(0);
            }
        };

        /**
         *
         * INICIO DE LA MEDICIÓN
         */
        commandManager.oneButtonMeasurement(1);

        /**
         *
         * INICIO DE LAS TAREAS DE INICIO DE TEMPERATURA Y FINALIZACIÓN DE LA MEDICIÓN
         */
        timer.schedule(finishMeasure, 45000);


    }

    public void retryMeasure() {
        /**
         *
         * DECLARACIÓN DEL TIMER PARA CORRER LAS MEDICIONES.
         */
        Timer timer;
        timer = new Timer();


        /**
         *
         *CREACIÓN DE LAS TAREAS PARA LAS MEDICIONES
         */
        TimerTask startMeasure = new TimerTask() {
            @Override
            public void run() {
                dialog.show();
                commandManager.oneButtonMeasurement(1);
            }
        };

        TimerTask finishMeasure = new TimerTask() {
            @Override
            public void run() {
                commandManager.oneButtonMeasurement(0);
            }
        };

        /**
         *
         * INICIO DE LA MEDICIÓN
         */
        timer.schedule(startMeasure, 300000);
//        timer.schedule(startMeasure, 60000);

        /**
         *
         * INICIO DE LAS TAREAS DE INICIO DE TEMPERATURA Y FINALIZACIÓN DE LA MEDICIÓN
         */
        timer.schedule(finishMeasure, 45000 + 300000);
//        timer.schedule(finishMeasure, 60000+45000);

    }

    public void medirBateria() {
        Timer timer;
        timer = new Timer();

        TimerTask batteryInfo = new TimerTask() {
            @Override
            public void run() {
                commandManager.getBatteryInfo();
            }
        };
        timer.schedule(batteryInfo, 0, 600000);
    }


    public void sincronizarHora() {
        Timer timer;
        timer = new Timer();

        TimerTask syncTime = new TimerTask() {
            @Override
            public void run() {
                commandManager.setTimeSync();
//                commandManager.syncData(0);
//                commandManager.syncDataHr(30000,30000);
                if (Config.hasContinuousHeart) {
                    Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);

                    commandManager.syncDataHr(System.currentTimeMillis() - 7 * 24 * 3600 * 1000,
                            System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

                } else {
                    //不带连续心率的手环的同步数据的方式
                    commandManager.syncData(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

                }
            }
        };
        timer.schedule(syncTime, 5000);

        Log.i(TAG, "SINCRONIZACIÓN DE TIEMPO");
    }

    public void iniciarMedicionHora() {

        Timer timer;
        timer = new Timer();

        TimerTask openMeasure = new TimerTask() {
            @Override
            public void run() {
                commandManager.openHourlyMeasure(1);
                ;
            }
        };
        timer.schedule(openMeasure, 5000);

        Log.i(TAG, "INICIO MEDICIÓN POR HORA");


        TimerTask hourMeasure = new TimerTask() {
            @Override
            public void run() {
                commandManager.oneButtonMeasurement(1);
                System.out.println("STARTING HOURLY MEASURE");
            }
        };

        TimerTask finishHourMeasure = new TimerTask() {
            @Override
            public void run() {
                commandManager.oneButtonMeasurement(0);
            }
        };


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Calendar calendar2 = Calendar.getInstance();


        calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY) + 1, 1, 0);
        System.out.println("CALENDAR: " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        System.out.println("CALENDAR2: " + calendar2.get(Calendar.YEAR) + "/" + calendar2.get(Calendar.MONTH) + "/" + calendar2.get(Calendar.DATE) + " " + calendar2.get(Calendar.HOUR_OF_DAY) + ":" + calendar2.get(Calendar.MINUTE) + ":" + calendar2.get(Calendar.SECOND));

        long delta = Math.abs(calendar2.getTimeInMillis() - calendar.getTimeInMillis());

        System.out.println("HOURLY MEASURE IN " + Math.round(delta / 60000) + " MIN");

//        timer.schedule(hourMeasure,delta,3600000);
        timer.schedule(finishHourMeasure, delta, 3600000);

//


    }


    public void nivelBateria(ArrayList<Integer> datas) {
        Log.i(TAG, "NIVEL DE BATERÍA: " + datas.get(7) + "%");
    }

    public void desconectarBluetooth(View view) {
        desconectadoPorUsuario = true;
        System.out.println("DESCONECTADO POR USUARIO");
        mBluetoothLeService.disconnect();
    }


    public void conectarBluetooth() {
        desconectadoPorUsuario = false;
        System.out.println("DESCONECTADO POR USUARIO=FALSE");
        mBluetoothLeService.connect(address);
    }

    public void encontrarDispositivo(View view) {
        commandManager.vibrate();
    }


    public void hideDialog() {
        dialog.dismiss();
    }

    public void showDialog() {
        dialog.show();
    }

    /**
     * @param n       NUEVO TAMAÑO DEL VECTOR
     * @param arreglo ARREGLO AL CUAL SE LE VA A ADICIONAR EL SUBARREGLO AGREGAR
     * @param agregar SUBARREGLO A AGREGAR
     * @return RETORNA EL AERREGLO CON LA ADICIÓN
     */
    public int[][] redimensionarArreglo(int n, int[][] arreglo, int[] agregar) {
        int[][] nuevoArreglo = new int[n + 1][14];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 14; j++) {
                try {
                    nuevoArreglo[i][j] = arreglo[i][j];
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        for (int k = 0; k < 14; k++) {
            nuevoArreglo[n][k] = agregar[k];
        }
        return nuevoArreglo;
    }

    /**
     * @param arreglo SE ELIMINA EL CACHE DE LA MANILLA Y SE MUESTRAN LOS DATOS QUE SE TRAEN
     */

    public void mostrarDataBorrarCache(List arreglo) {
        commandManager.clearData();

        int n = arreglo.size();
        for (int i = 0; i < n; i++) {
            int[] objetos = (int[]) arreglo.get(i);
            String texto = "";
            for (int elemento : objetos) {
            for (int elemento : objetos) {
                texto = texto + "," + elemento;

            }
            System.out.println(texto);
        }
        System.out.println(n);
        ;
    }

}



