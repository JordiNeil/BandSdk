package com.wakeup.bandsdk.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.adapter.LeDeviceListAdapter;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Harry
 */
public class DeviceScanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = DeviceScanActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    public String mDeviceAddress;
    public BluetoothDevice mSelectedDevice;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    private LeDeviceListAdapter leDeviceListAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private Button getFisiometria;
    private Retrofit retrofit;
    private ServiceFisiometria service;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device);
        getSupportActionBar().setTitle(R.string.device_list);

        mHandler = new Handler();





        //getFisiometria = findViewById(R.id.getFisiometria);




        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.not_support, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        scanLeDevice(true);

        listView = (ListView) findViewById(R.id.listview);

        bluetoothDevices = new ArrayList<>();
        leDeviceListAdapter = new LeDeviceListAdapter(this, bluetoothDevices);

        listView.setAdapter(leDeviceListAdapter);

        listView.setOnItemClickListener(this);


    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            };
            mHandler.postDelayed(runnable, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_list, menu);

        if (!mScanning) {
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);

        } else {
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                leDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //申请蓝牙权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Log.i(TAG, "Show an explanation to the user");
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.location_permission);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "重新申请一次");
                            ActivityCompat.requestPermissions(DeviceScanActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "用户再次拒绝了");
                        }
                    });
                    builder.create().show();

                } else {
                    Log.i(TAG, "No explanation needed, we can request the permission.");
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothDevice.getName() != null) {
                        if (bluetoothDevice.getName().contains("T1") || bluetoothDevice.getName().contains("T1S")) {
                            Log.i(TAG, "Name: " + bluetoothDevice.getName() + " Address: " + bluetoothDevice.getAddress() + " Alias: " + bluetoothDevice.getAlias());
                            leDeviceListAdapter.addDevice(bluetoothDevice);
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BluetoothDevice device = leDeviceListAdapter.getDevice(i);
        if (device == null) {
            return;
        }


        Intent intent = new Intent();
        intent.putExtra("address", device.getAddress());
        intent.putExtra("name", device.getName());
        setResult(RESULT_OK, intent);
        if (mScanning) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        finish();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getFisiometria() {
        System.out.println("fisionetria");
        service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU5NDc0MTIxMX0.PQzJdCA1wvd-6Fncr29lDiYkC0uIjK_HjmxjqJfG2L7XJd-U2jg3klF5noaZzDA-hj975aGLnbCdcgOHxIs1wQ";
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryData("Bearer " + token, 3);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
             @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {
                 System.out.println("-----------------------"+response.body());
                /*if (response.isSuccessful()) {
                    System.out.println(response.body());
                    *//*for (int i = 0; response.body() > i; i++){

                    }*//*
                    DataFisiometria res = response.body();
                    for (DataFisiometria c : response) {
                        System.out.println(c.getUserData().getFirstName());

                    }
                }*/

            }

            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {
                System.out.println("error");
                System.out.println(t.getMessage());
            }
        });

    }
}
