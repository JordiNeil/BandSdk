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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wakeup.bandsdk.activity.DeviceScanActivity;
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
    private BandInfo bandInfo;
    private ImageView imgConecct;


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
                connectBt.setText("DISCONNECT");
                imgConecct.setBackgroundResource(R.drawable.band_connected);
                tv_connect_state.setText("Conectado");
//                progressBar.setVisibility(View.GONE);

                /**
                 *
                 * INICIO MEDICIÓN AUTOMÁTICA DEL NIVEL DE BATERÍA
                 */
                Timer timer;
                timer = new Timer();

                TimerTask batteryInfo = new TimerTask() {
                    @Override
                    public void run() {
                        commandManager.getBatteryInfo();
                    }
                };
                timer.schedule(batteryInfo, 0, 600000);

                /**
                 *
                 * INICIO MEDICIÓN AUTOMÁTICA CADA HORA
                 */

                commandManager.openHourlyMeasure(1);

                /**
                 *
                 * SINCRONIZACIÓN DE TIEMPO
                 *
                 */
                commandManager.setTimeSync();


            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
                connectBt.setText("CONNECTION");
                imgConecct.setBackgroundResource(R.drawable.band_unconnect);
                tv_connect_state.setText("Desconectado");

                //progressBar.setVisibility(View.GONE);


            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");


            } else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Log.d(TAG, "RECEIVED DATA：" + DataHandUtils.bytesToHexStr(txValue)
                +" at "+(calendar.get(Calendar.HOUR_OF_DAY)) +":"+
                        (calendar.get(Calendar.MINUTE))+":"+
                        (calendar.get(Calendar.SECOND)));
                List<Integer> datas = DataHandUtils.bytesToArrayList(txValue);
                if (datas.size() == 0) {
                    return;
                }

                if (datas.get(0) == 0xAB) {
                    switch (datas.get(4)) {
                        case 0x91:
                            //BATTERY POWER
                            Battery battery = (Battery) dataPasrse.parseData(datas);
                            Log.i(TAG, battery.toString());
                            break;
                        case 0x92:
                            //BRACELET DATA
                            bandInfo = (BandInfo) dataPasrse.parseData(datas);
                            Log.i(TAG, bandInfo.toString());
                            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);


                            if (bandInfo.getBandType() == 0x0B
                                    || bandInfo.getBandType() == 0x0D
                                    || bandInfo.getBandType() == 0x0E
                                    || bandInfo.getBandType() == 0x0F) {

                                Config.hasContinuousHeart = true;

                            }


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
        address = SPUtils.getString(MainActivity.this, SPUtils.ADDRESS, "");
        mTextMessage.setText(address);

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

    public void version(View view) {
        commandManager.getVersion();

    }

    public void battery(View view) {
        commandManager.getBatteryInfo();
    }

    public void syncTime(View view) {
        commandManager.setTimeSync();
    }

    public void syncData(View view) {
        if (bandInfo == null) {
            Toast.makeText(MainActivity.this, "GET INFO OF BRACELET FIRST", Toast.LENGTH_SHORT).show();
            return;
        }
        //DATA SYNCHRONIZATION METHOD OF BRACELET WITH CONTINUOUS HEART RATE
        if (Config.hasContinuousHeart) {
            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);

            commandManager.syncDataHr(System.currentTimeMillis() - 7 * 24 * 3600 * 1000,
                    System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

        } else {
            //WAY OF SYNCHRONIZING DATA WITH BRACELET WITHOUT CONTINUOUS HEART RATE
            commandManager.syncData(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

        }
    }

    /**
     * TURN ON HOURLY MEASUREMENT
     *
     * @param view
     */
    public void openMeasure(View view) {
        commandManager.openHourlyMeasure(1);
    }

    /**
     * CLEAR BRACELET DATA
     *
     * @param view
     */
    public void clearData(View view) {
        commandManager.clearData();
    }

    /**
     * SEND MESSAGE
     *
     * @param view
     */
    public void sendMessage(View view) {
        //以QQ消息为例，可以传入不同的MessageID  例如MessageID.WECHAT
        commandManager.sendMessage(MessageID.QQ, MessageType.COMING_MESSAGES, "测试消息通知");
    }

    public void openMessage(View view) {
        commandManager.sendMessage(MessageID.QQ, MessageType.ON, null);

    }


    /**
     * SET ALARM
     *
     * @param view
     */
    public void alarm_clock(View view) {
        //闹钟id 为0 开启18:01闹钟，只响一次
        commandManager.setAlarmClock(0, 1, 18, 1, Constants.ALARM_CLOCK_TYPE1);

//        //闹钟id 为1 开启06:30闹钟，周一至周五
//        commandManager.setAlarmClock(1,1,6,30,Constants.ALARM_CLOCK_TYPE2);
//
//        //闹钟id 为2 开启08:03，每天
//        commandManager.setAlarmClock(2,1,8,3,Constants.ALARM_CLOCK_TYPE3);
    }

    /**
     * ONE-CLICK MEASUREMENT   ONE-CLICK MEASUREMENT. AFTER 1 MINUTE, THE SHUT DOWN COMMAND WILL BE SENT AND THE RESULT WILL BE RETURNED
     *
     * @param view
     */
    public void one_button_measurement(View view) throws InterruptedException {
        commandManager.oneButtonMeasurement(1);
        Thread.sleep(60000);
        //ONE MINUTE AFTER SENDING THE CLOSE COMMAND, THE MEASUREMENT WILL BE SENT
        commandManager.oneButtonMeasurement(0);

    }

    /**
     * SINGLE MEASUREMENT-AFTER 45S MEASUREMENT RESULTS WILL BE RETURNED
     *
     * @param view
     */
    private boolean transfer = true;

    public synchronized void single_heartRate(View view) {

        Meassure();


    }

    /**
     * REAL TIME MEASUREMENT
     *
     * @param view
     */
    public void real_time_heartRate(View view) throws InterruptedException {
        commandManager.singleRealtimeMeasure(0X09, 1);
//        commandManager.singleRealtimeMeasure(0X09, 0);
//        commandManager.singleRealtimeMeasure(0X0A,0); //关闭实时测量


    }

    public void getSleep(View view) {
        commandManager.syncSleepData(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
    }


    /**
     * CONTINUOUS HEART RATE BRACELET FOR REAL-TIME HEART RATE
     *
     * @param view
     */


    public synchronized void real_time_heartRate2_1(View view) {
//        while (!transfer){
//            try{
//                wait();
//            }catch (InterruptedException e){
//                Thread.currentThread().interrupt();
//                Log.i(TAG, "Thread Interrupted");
//            }
//        }
//        transfer = false;
        commandManager.getRealTimeHeartRate(1);
        notifyAll();


    }

    /**
     * 关闭 CLOSE CONTINUOUS HEART RATE BRACELET FOR REAL-TIME HEART RATE
     *
     * @param view
     */
    public synchronized void real_time_heartRate2_0(View view) {
        while (transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.i(TAG, "Thread Interrupted");
            }
        }
        transfer = true;
        notifyAll();
        commandManager.singleRealtimeMeasure(0X09, 0);
        //commandManager.getRealTimeHeartRate(0);
    }


    //-------------------------------------------OWN FUNCTIONS------------------------------------------
    public synchronized void Meassure() {
        /**
         *
         * DECLARACIÓN DEL TIMER PARA CORRER LAS MEDICIONES.
         */
        Timer timer;
        timer = new Timer();
//
//        /**
//         *
//         * DECLARACIÓN DE LAS TAREAS PARA INICIAR Y FINALIZAR CADA UNA DE LAS MEDICIONES.
//         */
//
//        TimerTask startHeartRate=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.singleRealtimeMeasure(0X09, 1);
//            }
//        };
//        TimerTask finishHeartRate=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.singleRealtimeMeasure(0X09, 0);
//                commandManager.singleRealtimeMeasure(0X11, 1);
//            }
//        };
//        TimerTask finishBloodOxygen=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.singleRealtimeMeasure(0X11, 0);
//                commandManager.singleRealtimeMeasure(0X21, 1);
//            }
//        };
//        TimerTask finishBloodPressure=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.singleRealtimeMeasure(0X21, 0);
//                commandManager.singleRealtimeMeasure(0X81, 1);
//            }
//        };
//        TimerTask finishTemperature=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.singleRealtimeMeasure(0X81, 0);
//            }
//        };
//
//        /**
//         *
//         * PROGRAMACIÓN DE TAREAS PARA INICIAR Y FINALIZAR LAS MEDICIONES
//         */
//
//
//        timer.schedule(startHeartRate,0);
//        timer.schedule(finishHeartRate,45000);
//        timer.schedule(finishBloodOxygen,90000);
//        timer.schedule(finishBloodPressure,135000);
//        timer.schedule(finishTemperature,180000);


//        while(System.currentTimeMillis()-|Time<=60000) {
//            Log.i(TAG, "---------MEASUREMENT IN PROGRESS------------");
//            notifyAll();
//        }
//        Log.i(TAG,"-----------MEASUREMENT FINISHED-----------");
//        pause(60000);
//        TimeUnit.MINUTES.sleep(1);
//        commandManager.oneButtonMeasurement(1);
//        commandManager.getRealTimeHeartRate(1);

        /**
         *
         *CREACIÓN DE LAS TAREAS PARA LAS MEDICIONES
         */


//        TimerTask startTemperature=new TimerTask() {
//            @Override
//            public void run() {
//                commandManager.oneButtonMeasurement( 0);
//                commandManager.singleRealtimeMeasure(0X81, 1);
//            }
//        };
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
//        timer.schedule(startTemperature,45000);
        timer.schedule(finishMeasure, 45000);


    }

}



