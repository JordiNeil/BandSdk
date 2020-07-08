package com.wakeup.bandsdk.activity;


import androidx.annotation.Nullable;
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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wakeup.bandsdk.Fragments.HomeFragment;
import com.wakeup.bandsdk.Fragments.UserFragment;
import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.R;
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

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextMessage, tv_connect_state;

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
    public Button btnMeasure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        radioButtonHome = (RadioButton) findViewById(R.id.rb_home);

        //radioButtonHome.callOnClick();
        radioButtonInfo = (RadioButton) findViewById(R.id.rb_discover);
        //radioButtonInfo.callOnClick();
        radioButtonUser = (RadioButton) findViewById(R.id.rb_mine);
        //radioButtonUser.callOnClick();

        btnMeasure = (Button) findViewById(R.id.button4);
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_fragment_container, fragmentHome);
        fragmentTransaction.commit();*/

    }


    public void fragmentHome(View view) {
        Fragment fragmentHome = new HomeFragment();
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

    public synchronized void fragmentInfo(View view) {

//        measure();
//        commandManager.oneButtonMeasurement();
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

    public void connect(View view) {
        mBluetoothLeService.connect(address);
        // progressBar.setVisibility(View.VISIBLE);
        /*if (!TextUtils.isEmpty(address)) {
            mBluetoothLeService.connect(address);
            progressBar.setVisibility(View.VISIBLE);
        }*/
        /*if (connectBt.getText().toString().equals("连接")) {
            if (!TextUtils.isEmpty(address)) {
                mBluetoothLeService.connect(address);
                progressBar.setVisibility(View.VISIBLE);
            }
        } else if (connectBt.getText().toString().equals("断开连接")) {
            mBluetoothLeService.disconnect();
        }*/
    }

    public boolean medicionCorrecta = false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
//                connectBt.setText("DISCONNECT");
//                imgConecct.setBackgroundResource(R.drawable.band_connected);
//                tv_connect_state.setText("Conectado");
//                progressBar.setVisibility(View.GONE);

                /**
             *
             * INICIO MEDICIÓN AUTOMÁTICA DEL NIVEL DE BATERÍA
             **/
//                Timer timer;
//                timer = new Timer();
//
//                TimerTask batteryInfo = new TimerTask() {
//                    @Override
//                    public void run() {
//                        commandManager.getBatteryInfo();
//                    }
//                };
//                timer.schedule(batteryInfo, 0, 600000);


             /**
              *
              * * INICIO MEDICIÓN AUTOMÁTICA CADA HORA
              **/
//                commandManager.openHourlyMeasure(1);


//                commandManager.setTimeSync();


            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
//                connectBt.setText("CONNECTION");
//                imgConecct.setBackgroundResource(R.drawable.band_unconnect);
                tv_connect_state.setText("Desconectado");

                //progressBar.setVisibility(View.GONE);

                

            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");


            } else {

//            measure();
            }
            if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                        + " at " + (calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                        (calendar.get(Calendar.MINUTE)) + ":" +
                        (calendar.get(Calendar.SECOND)));
                List<Integer> datas = DataHandUtils.bytesToArrayList(txValue);
                if (datas.size() == 0) {
                    return;
                }

                if (datas.get(0) == 0xAB) {
                    switch (datas.get(4)) {
                        case 0x91:
                            //BATTERY POWER
//                            Battery battery = (Battery) dataPasrse.parseData(datas);
//                            Log.i(TAG, battery.toString());
                            break;
                        case 0x92:
                            //BRACELET DATA
//                            bandInfo = (BandInfo) dataPasrse.parseData(datas);
//                            Log.i(TAG, bandInfo.toString());
//                            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);


//                            if (bandInfo.getBandType() == 0x0B
//                                    || bandInfo.getBandType() == 0x0D
//                                    || bandInfo.getBandType() == 0x0E
//                                    || bandInfo.getBandType() == 0x0F) {
//
//                                Config.hasContinuousHeart = true;

//                            }


//                            break;
                        case 0x51:
//
                            switch (datas.get(5)) {
                                case 0x11:
//                                    STAND-ALONE MEASUREMENT OF HEART RATE DATA
//                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
//                                    Log.i(TAG, heartRateBean.toString());
                                    break;
                                case 0x12:
                                    //STAND-ALONE MEASUREMENT OF BLOOD OXYGEN RATE DATA
                                    BloodOxygenBean bloodOxygenBean = (BloodOxygenBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodOxygenBean.toString());
                                    break;
                                case 0x14:
                                    //STAND-ALONE MEASUREMENT OF BLOOD PREASURE
                                    BloodPressureBean bloodPressureBean = (BloodPressureBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bloodPressureBean.toString());
                                    break;
                                case 0x08:
                                    //CURRENT DATA
                                    CurrentDataBean currentDataBean = (CurrentDataBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, currentDataBean.toString());
                                    break;

                                case 0x20:
                                    //RETURN HOURLY DATA
                                    HourlyMeasureDataBean hourlyMeasureDataBean = (HourlyMeasureDataBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, hourlyMeasureDataBean.toString());
                                    break;
                                case 0x21:
                                    //BODY TEMPERATURE AND IMMUNITY DATA
                                    BodytempAndMianyiBean bodytempAndMianyiBean = (BodytempAndMianyiBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bodytempAndMianyiBean.toString());
                                    break;

                                case 0x13:
                                    //STAND-ALONE BODY TEMPERATURE MEASUREMENT
                                    BodyTempBean bodyTempBean = (BodyTempBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, bodyTempBean.toString());
                                    break;

                                case 0x18:
                                    //RETURN TO STAND-ALONE IMMUNITY MEASUREMENT
                                    MianyiBean mianyiBean = (MianyiBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, mianyiBean.toString());
                                    break;

                            }


                            break;
                        case 0x52:
                            //SLEEP TIME RECORD
                            SleepData sleepData = (SleepData) dataPasrse.parseData(datas);
                            Log.i(TAG, sleepData.toString());

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
                            OneButtonMeasurementBean oneButtonMeasurementBean = (OneButtonMeasurementBean) dataPasrse.parseData(datas);

                            System.out.println("-----------" + datas);

                            System.out.println("---------" + datas);

                            Log.i(TAG, oneButtonMeasurementBean.toString());

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


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        address = SPUtils.getString(HomeActivity.this, SPUtils.ADDRESS, "");
        // mTextMessage.setText(address);

    }

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
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
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
    public synchronized void measure() {
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
        timer.schedule(startMeasure,0);
        timer.schedule(finishMeasure,45000);


    }
}