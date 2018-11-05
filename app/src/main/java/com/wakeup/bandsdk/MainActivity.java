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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wakeup.bandsdk.activity.DeviceScanActivity;
import com.wakeup.mylibrary.Config;
import com.wakeup.mylibrary.bean.BandInfo;
import com.wakeup.mylibrary.bean.Battery;
import com.wakeup.mylibrary.bean.CurrentDataBean;
import com.wakeup.mylibrary.bean.HeartRateBean;
import com.wakeup.mylibrary.bean.HourlyMeasureDataBean;
import com.wakeup.mylibrary.command.CommandManager;
import com.wakeup.mylibrary.data.DataParse;
import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.DataHandUtils;
import com.wakeup.mylibrary.utils.SPUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView mTextMessage;
    private static final int REQUEST_SEARCH = 1;
    private BluetoothService mBluetoothLeService;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };
    private String address;
    private Button connectBt;
    private ProgressBar progressBar;
    private CommandManager commandManager;
    private DataParse dataPasrse;
    private BandInfo bandInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextMessage = (TextView) findViewById(R.id.message);
        connectBt = findViewById(R.id.connect);
        progressBar = findViewById(R.id.progressBar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
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
        android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
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
        if (requestCode == REQUEST_SEARCH && resultCode == RESULT_OK) {
            address = data.getStringExtra("address");
            String name = data.getStringExtra("name");

            Log.i(TAG, "name: " + name + "\n" + "address: " + address);

            mTextMessage.setText("name: " + name + "\n" + "address: " + address);

            SPUtils.putString(MainActivity.this, SPUtils.ADDRESS, address);
        }
    }

    public void connect(View view) {
        if (connectBt.getText().toString().equals("连接")) {
            if (!TextUtils.isEmpty(address)) {
                mBluetoothLeService.connect(address);
                progressBar.setVisibility(View.VISIBLE);
            }
        } else if (connectBt.getText().toString().equals("断开连接")) {
            mBluetoothLeService.disconnect();
        }
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        /**拼接包的长度**/
        private int combineSize;
        /**开始拼接包**/
        private boolean combine;
        /**临时包**/
        private byte[] templeBytes = new byte[0];

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
                connectBt.setText("断开连接");
                progressBar.setVisibility(View.GONE);

            } else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
                connectBt.setText("连接");
                progressBar.setVisibility(View.GONE);


            } else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");


            } else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                Log.d(TAG, "接收的数据：" + DataHandUtils.bytesToHexStr(txValue));
                List<Integer> datas = DataHandUtils.bytesToArrayList(txValue);
                if (datas.size() == 0) {
                    return;
                }

                //如果是遇到整点数据第一个包，接下来的一个包就要拼接到前一个包上面
                if (datas.get(0) == 0xAB && datas.get(4) == 0x51 && datas.get(5) == 0x20) {
                    combineSize = 26;//两个包的总长度20+6
                    //开始拼接
                    combine = true;
                }
                if (combine) {
                    byte[] combined = new byte[templeBytes.length + txValue.length];
                    System.arraycopy(templeBytes, 0, combined, 0, templeBytes.length);
                    System.arraycopy(txValue, 0, combined, templeBytes.length, txValue.length);
                    templeBytes = combined;
                    Log.i(TAG, "length:" + combined.length);

                    if (combined.length == combineSize) {
                        List<Integer> combineList = DataHandUtils.bytesToArrayList(combined);
                        Log.i(TAG, "combined:" + DataHandUtils.bytesToHexStr(combined));

                        //返回整点数据
                        HourlyMeasureDataBean hourlyMeasureDataBean = (HourlyMeasureDataBean) dataPasrse.parseData(combineList);
                        Log.i(TAG, hourlyMeasureDataBean.toString());



                        //拼接完成 重置状态
                        combine = false;
                        //临时数组置空
                        templeBytes = new byte[0];
                    }


                }


                if (datas.get(0) == 0xAB) {
                    switch (datas.get(4)) {
                        case 0x91:
                            //电池电量
                            Battery battery = (Battery) dataPasrse.parseData(datas);
                            Log.i(TAG, battery.toString());
                            break;
                        case 0x92:
                            //手环信息
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
                                    //返回心率数据
                                    HeartRateBean heartRateBean = (HeartRateBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, heartRateBean.toString());
                                    break;
                                case 0x12:
                                    //返回血氧数据

                                    break;
                                case 0x14:
                                    //返回血氧数据

                                    break;
                                case 0x08:
                                    //当前数据
                                    CurrentDataBean currentDataBean = (CurrentDataBean) dataPasrse.parseData(datas);
                                    Log.i(TAG, currentDataBean.toString());
                                    break;


                            }


                            break;
                        case 0x52:


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

    public void findBand(View view) {
        commandManager.findBand();
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
            Toast.makeText(MainActivity.this, "请先获取手环的信息", Toast.LENGTH_SHORT).show();
            return;
        }
        //带连续心率的手环的同步数据的方式
        if (Config.hasContinuousHeart) {
            Log.i(TAG, "hasContinuousHeart:" + Config.hasContinuousHeart);

            commandManager.syncDataHr(System.currentTimeMillis() - 7 * 24 * 3600 * 1000,
                    System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

        } else {
            //不带连续心率的手环的同步数据的方式
            commandManager.syncData(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);

        }
    }
}
