package com.wakeup.mylibrary.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wakeup.mylibrary.utils.DataHandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Semaphore;


/**
 * @author Harry
 * @date 2018/5/21
 * <p>
 * 1.连接蓝牙设备
 * 2.发现服务
 * 3.初始化mRXCharacteristic, mTXCharacteristic 并 enableNotification()
 * 4.onCharacteristicChanged 接收蓝牙设备发送过来的消息
 * 5.注册广播接收指令，将指令发送给蓝牙设备
 */

public class BluetoothService extends Service {
    private final static String TAG = BluetoothService.class.getSimpleName();

    /**
     * Service UUID
     */
    private final static UUID SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * RX characteristic UUID
     */
    private final static UUID RX_CHARACTERISTIC_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * TX characteristic UUID
     */
    private final static UUID TX_CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID
     */
    private final static UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    /**
     * The maximum packet size is 20 bytes.
     */
    private static final int MAX_PACKET_SIZE = 20;
    public static final String ACTION_SEND_DATA_TO_BLE = "com.wakeup.ourtoken.ACTION_SEND_DATA_TO_BLE";
    public static final String EXTRA_SEND_DATA_TO_BLE = "com.wakeup.ourtoken.EXTRA_SEND_DATA_TO_BLE";
    private BluetoothGattCharacteristic mRXCharacteristic, mTXCharacteristic;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.wakeup.ourtoken.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.wakeup.ourtoken.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.wakeup.ourtoken.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.wakeup.ourtoken.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.wakeup.ourtoken.bluetooth.le.EXTRA_DATA";

    private Handler mHandler;

    //------------------------------------------
    //汉天下
    public final static String OTA_RX_DAT_ACTION =
            "com.hs.bluetooth.le.OTA_RX_DAT_ACTION";

    public final static String OTA_RX_CMD_ACTION =
            "com.hs.bluetooth.le.OTA_RX_CMD_ACTION";

    public final static String OTA_RX_ISP_CMD_ACTION =
            "com.hs.bluetooth.le.OTA_RX_ISP_CMD_ACTION";

    public final static String ACTION_GATT_CHARACTER_NOTIFY =
            "com.example.bluetooth.le.ACTION_GATT_CHARACTER_NOTIFY";

    public final static UUID UUID_OTA_TX_CMD = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_OTA_TX_DAT = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_OTA_RX_CMD = UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_OTA_RX_DAT = UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_ISP_TX_CMD = UUID.fromString("6e40ff02-b5a3-f393-e0a9-e50e24dcca9e");
    public final static UUID UUID_ISP_RX_CMD = UUID.fromString("6e40ff03-b5a3-f393-e0a9-e50e24dcca9e");
    public final static String ARRAY_BYTE_DATA = "com.example.bluetooth.le.ARRAY_BYTE_DATA";

    //-------------------------------------------
    private static final int SEND_PACKET_SIZE = 20;
    private static final int FREE = 0;
    private static final int SENDING = 1;
    private static final int RECEIVING = 2;
    private int ble_status = FREE;
    private int packet_counter = 0;
    private int send_data_pointer = 0;
    private byte[] send_data = null;
    private boolean first_packet = false;
    private boolean final_packet = false;
    private boolean packet_send = false;
    private Timer mTimer;
    private int time_out_counter = 0;
    private int TIMER_INTERVAL = 100;
    private int TIME_OUT_LIMIT = 100;
    public ArrayList<byte[]> data_queue = new ArrayList<>();
    boolean sendingStoredData = false;
    public static Semaphore write_characer_lock = new Semaphore(1);//汉天下


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                /*
                 * The onConnectionStateChange event is triggered just after the Android connects to a device.
                 * In case of bonded devices, the encryption is reestablished AFTER this callback is called.
                 * Moreover, when the device has Service Changed indication enabled, and the list of services has changed (e.g. using the DFU),
                 * the indication is received few hundred milliseconds later, depending on the connection interval.
                 * When received, Android will start performing a service discovery operation on its own, internally,
                 * and will NOT notify the app that services has changed.
                 *
                 * If the gatt.discoverServices() method would be invoked here with no delay, if would return cached services,
                 * as the SC indication wouldn't be received yet.
                 * Therefore we have to postpone the service discovery operation until we are (almost, as there is no such callback) sure,
                 * that it has been handled.
                 * TODO: Please calculate the proper delay that will work in your solution.
                 * It should be greater than the time from LLCP Feature Exchange to ATT Write for Service Change indication.
                 * If your device does not use Service Change indication (for example does not have DFU) the delay may be 0.
                 */
                final boolean bonded = gatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED;
                final int delay = bonded ? 1600 : 0; // around 1600 ms is required when connection interval is ~45ms.
                if (delay > 0)
                    Log.d(TAG, "wait(" + delay + ")");
                mHandler.postDelayed(() -> {
                    if (mBluetoothGatt == null) {
                        return;
                    }
                    // Some proximity tags (e.g. nRF PROXIMITY) initialize bonding automatically when connected.
                    // TODO: 2018/6/7 BluetoothGatt.getDevice()' on a null object reference
                    if (mBluetoothGatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "Discovering Services...");
                        Log.d(TAG, "gatt.discoverServices()");
                        // Attempts to discover services after successful connection.
                        Log.i(TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());
                    }
                }, delay);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.close");
                broadcastUpdate(intentAction);

                close();

                mRXCharacteristic = null;
                mTXCharacteristic = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String intentAction;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered-- Services Discovered");
                final BluetoothGattService service = gatt.getService(SERVICE_UUID);
                //获取mRXCharacteristic 、mTXCharacteristic
                if (service != null) {
                    mRXCharacteristic = service.getCharacteristic(RX_CHARACTERISTIC_UUID);
                    mTXCharacteristic = service.getCharacteristic(TX_CHARACTERISTIC_UUID);
                    Log.d(TAG, "mRXCharacteristic mTXCharacteristic initialization");
                }
                //发送广播
                intentAction = ACTION_GATT_SERVICES_DISCOVERED;
                broadcastUpdate(intentAction);
                //enable notification
                internalEnableNotifications(mTXCharacteristic);

            } else {
                Log.e(TAG, "onServicesDiscovered error " + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Read Response received from " + characteristic.getUuid() + ", value: " + DataHandUtils.parse
                        (characteristic));


            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow.com/a/20093695/2115352
                    Log.w(TAG, "Phone has lost bonding information");

                }
            } else {
                Log.e(TAG, "onCharacteristicRead error " + status);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.i(TAG, "Data written to " + characteristic.getUuid() + ", value: " + DataHandUtils.parse(characteristic));
                // The value has been written. Notify the manager and proceed with the initialization queue.

            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    // This should never happen but it used to: http://stackoverflow.com/a/20093695/2115352
                    Log.w(TAG, "Phone has lost bonding information");
                }
            } else {
                Log.e(TAG, "onCharacteristicWrite error " + status);
            }

            //汉天下
            write_characer_lock.release(1);


        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final String data = DataHandUtils.parse(characteristic);

            final BluetoothGattDescriptor cccd = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;

            if (notifications) {
                Log.i(TAG, "Notification received from " + characteristic.getUuid() + ", value: " + data);
                //发送广播
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                //汉天下
                if (UUID_OTA_RX_DAT.toString().equals(characteristic.getUuid().toString())) {
                    broadcastUpdate(OTA_RX_DAT_ACTION, characteristic);
                    Log.i(TAG, "broadcastUpdate: OTA_RX_DAT_ACTION" + characteristic.getUuid().toString());
                }

                if (UUID_ISP_RX_CMD.toString().equals(characteristic.getUuid().toString())) {
                    broadcastUpdate(OTA_RX_ISP_CMD_ACTION, characteristic);
                }

                if (UUID_OTA_RX_CMD.toString().equals(characteristic.getUuid().toString())) {
                    broadcastUpdate(OTA_RX_CMD_ACTION, characteristic);
                }

            } else { // indications
                Log.i(TAG, "Indication received from " + characteristic.getUuid() + ", value: " + data);


            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is handling for the notification on TX Character of NUS service
        if (TX_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {

            Log.d(TAG, "received from ble:" + DataHandUtils.bytesToHexStr(characteristic.getValue()));
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        }

        //这是汉天下ota后面加的
        else if (UUID_OTA_RX_DAT.equals(characteristic.getUuid())) {
            final byte[] ota_rx_dat = characteristic.getValue();
            if (ota_rx_dat != null && ota_rx_dat.length > 0) {
                intent.putExtra(ARRAY_BYTE_DATA, ota_rx_dat);
            }
        } else if (UUID_OTA_RX_CMD.equals(characteristic.getUuid())) {
            final byte[] ota_rx_cmd = characteristic.getValue();
            if (ota_rx_cmd != null && ota_rx_cmd.length > 0) {
                intent.putExtra(ARRAY_BYTE_DATA, ota_rx_cmd);
            }
        } else if (UUID_ISP_RX_CMD.equals(characteristic.getUuid())) {
            byte[] value = characteristic.getValue();
            if (value != null && value.length > 0) {
                intent.putExtra(ARRAY_BYTE_DATA, value);
            }
        } else {
            Log.d(TAG, "For all other profiles");
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }

        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        mHandler = new Handler();
        registerReceiver(mServiceReceiver, new IntentFilter(ACTION_SEND_DATA_TO_BLE));
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mServiceReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_SEND_DATA_TO_BLE)) {//from commandManager
                byte[] send_data = intent.getByteArrayExtra(EXTRA_SEND_DATA_TO_BLE);
                if (send_data != null) {
                    Log.i(TAG,"from commandManager");
                    BLE_send_data_set(send_data, false);
                }

                //发送数据


            }
        }
    };

    private boolean internalEnableNotifications(final BluetoothGattCharacteristic characteristic) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0)
            return false;

        Log.d(TAG, "gatt.setCharacteristicNotification(" + characteristic.getUuid() + ", true)");
        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.d(TAG, "Enabling notifications for " + characteristic.getUuid());
            Log.d(TAG, "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x01-00)");
            return internalWriteDescriptorWorkaround(descriptor);
        }
        return false;
    }

    /**
     * There was a bug in Android up to 6.0 where the descriptor was written using parent
     * characteristic's write type, instead of always Write With Response, as the spec says.
     * <p>
     * See: <a href="https://android.googlesource.com/platform/frameworks/base/+/942aebc95924ab1e7ea1e92aaf4e7fc45f695a6c%5E%21/#F0">
     * https://android.googlesource.com/platform/frameworks/base/+/942aebc95924ab1e7ea1e92aaf4e7fc45f695a6c%5E%21/#F0</a>
     * </p>
     *
     * @param descriptor the descriptor to be written
     * @return the result of {@link BluetoothGatt#writeDescriptor(BluetoothGattDescriptor)}
     */
    private boolean internalWriteDescriptorWorkaround(final BluetoothGattDescriptor descriptor) {
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || descriptor == null)
            return false;

        final BluetoothGattCharacteristic parentCharacteristic = descriptor.getCharacteristic();
        final int originalWriteType = parentCharacteristic.getWriteType();
        parentCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        final boolean result = gatt.writeDescriptor(descriptor);
        parentCharacteristic.setWriteType(originalWriteType);
        return result;
    }

    private boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(SERVICE_UUID);
        if (service != null) {
            mRXCharacteristic = service.getCharacteristic(RX_CHARACTERISTIC_UUID);
            mTXCharacteristic = service.getCharacteristic(TX_CHARACTERISTIC_UUID);
            Log.d(TAG, "mRXCharacteristic mTXCharacteristic initialization");

        }

//        boolean writeRequest = false;
//        boolean writeCommand = false;
//        if (mRXCharacteristic != null) {
//            final int rxProperties = mRXCharacteristic.getProperties();
//            writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
//            writeCommand = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0;
//
//            // Set the WRITE REQUEST type when the characteristic supports it. This will allow to send long write (also if the characteristic support it).
//            // In case there is no WRITE REQUEST property, this manager will divide texts longer then 20 bytes into up to 20 bytes chunks.
//            if (writeRequest)
//                mRXCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//        }

        return mRXCharacteristic != null && mTXCharacteristic != null; // && (writeRequest || writeCommand)
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        //连接之前先断开之前的设备
        close();
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * 汉天下
     *
     * @param characteristic
     * @return
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        try {
            write_characer_lock.acquire(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (mBluetoothGatt == null) {
            Log.i(TAG, "mBluetoothGatt == null");
            return false;
        }
        boolean b = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i(TAG, "汉天下 writeCharacteristic: "+b);

        return b;
    }

    /**
     * 汉天下
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }

        return mBluetoothGatt.getServices();
    }

    /**
     * 汉天下
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                 boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return mBluetoothGatt.writeDescriptor(descriptor);


    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind close");
        close();
        return super.onUnbind(intent);
    }

    public void close() {
        Log.d(TAG, "close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }


    //------------------------------------------------------------------------


    /**
     * 设置数据到内部缓冲区对BLE发送数据
     */
    private void BLE_send_data_set(byte[] data, boolean retry_status) {
        if (ble_status != FREE || mConnectionState != STATE_CONNECTED) {
            //蓝牙没有连接或是正在接受或发送数据，此时将要发送的指令加入集合
            if (sendingStoredData) {
                if (!retry_status) {
                    data_queue.add(data);
                }
                return;
            } else {
                data_queue.add(data);
                start_timer();
            }

        } else {
            ble_status = SENDING;

            if (data_queue.size() != 0) {
                send_data = data_queue.get(0);
                sendingStoredData = false;
            } else {
                send_data = data;
            }
            packet_counter = 0;
            send_data_pointer = 0;
            //第一个包
            first_packet = true;
            BLE_data_send();

            if (data_queue.size() != 0) {
                data_queue.remove(0);
            }

            if (data_queue.size() == 0) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        }
    }


    /**
     * @brief Send data using BLE. 发送数据到蓝牙
     */
    private void BLE_data_send() {
        int err_count = 0;
        int send_data_pointer_save;
        int wait_counter;
        boolean first_packet_save;
        while (!final_packet) {
            //不是最后一个包
            byte[] temp_buffer;
            send_data_pointer_save = send_data_pointer;
            first_packet_save = first_packet;
            if (first_packet) {
                //第一个包

                if ((send_data.length - send_data_pointer) > (SEND_PACKET_SIZE)) {
                    temp_buffer = new byte[SEND_PACKET_SIZE];//20
                    for (int i = 0; i < SEND_PACKET_SIZE; i++) {
                        //将原数组加入新创建的数组
                        temp_buffer[i] = send_data[send_data_pointer];
                        send_data_pointer++;
                    }
                } else {
                    //发送的数据包不大于20
                    temp_buffer = new byte[send_data.length - send_data_pointer];
                    for (int i = 0; i < temp_buffer.length; i++) {
                        //将原数组未发送的部分加入新创建的数组
                        temp_buffer[i] = send_data[send_data_pointer];
                        send_data_pointer++;
                    }
                    final_packet = true;
                }
                first_packet = false;
            } else {
                //不是第一个包
                if (send_data.length - send_data_pointer >= SEND_PACKET_SIZE) {
                    temp_buffer = new byte[SEND_PACKET_SIZE];
//                    temp_buffer[0] = (byte) packet_counter;
                    for (int i = 0; i < SEND_PACKET_SIZE; i++) {
                        temp_buffer[i] = send_data[send_data_pointer];
                        send_data_pointer++;
                    }
                } else {
                    //最后一个包
                    final_packet = true;
                    temp_buffer = new byte[send_data.length - send_data_pointer];
//                    temp_buffer[0] = (byte) packet_counter;
                    for (int i = 0; i < temp_buffer.length; i++) {
                        temp_buffer[i] = send_data[send_data_pointer];
                        send_data_pointer++;
                    }
                }
                packet_counter++;
            }
            packet_send = false;

            boolean status = writeRXCharacteristic(temp_buffer);
            if ((status == false) && (err_count < 3)) {
                err_count++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                send_data_pointer = send_data_pointer_save;
                first_packet = first_packet_save;
                packet_counter--;
            }
            // Send Wait
            for (wait_counter = 0; wait_counter < 5; wait_counter++) {
                if (packet_send == true) {
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        final_packet = false;
        ble_status = FREE;
    }

    /**
     * 定时器
     */
    private void start_timer() {
        sendingStoredData = true;
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer_Tick();
            }
        }, 100, TIMER_INTERVAL);
    }

    /**
     * @brief Interval timer function.
     */
    private void timer_Tick() {

        if (data_queue.size() != 0) {
            sendingStoredData = true;
            BLE_send_data_set(data_queue.get(0), true);
        }

        if (time_out_counter < TIME_OUT_LIMIT) {
            time_out_counter++;
        } else {
            ble_status = FREE;
            time_out_counter = 0;
        }
        return;
    }

    /**
     * 向蓝牙设备写入字节数据
     *
     * @param value
     */
    public boolean writeRXCharacteristic(byte[] value) {
        Log.i(TAG, "writeRXCharacteristic: " + value.length);
        if (mRXCharacteristic == null) {
            Log.e(TAG, " mRXCharacteristic==null");
            return false;
        }

        mRXCharacteristic.setValue(value);
        //如果设置WRITE_TYPE_DEFAULT 需要回应。 第一个包发送成功，后面所有的包都发送失败。
//        mRXCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        if (mBluetoothGatt == null) {
            Log.e(TAG, "mBluetoothGatt == null");
            return false;
        }
        boolean status = mBluetoothGatt.writeCharacteristic(mRXCharacteristic);

        Log.d(TAG, "write TXchar - status=" + status + DataHandUtils.bytesToHexStr(value));
        return status;

    }

    /**
     * @brief enableTXNotification
     */
    @SuppressLint("InlinedApi")
    public void enableTXNotification() {
        Log.i(TAG, "enableTXNotification");
        BluetoothGattService RxService = mBluetoothGatt
                .getService(SERVICE_UUID);
        if (RxService == null) {
            return;
        }

        BluetoothGattCharacteristic TxChar = RxService
                .getCharacteristic(TX_CHARACTERISTIC_UUID);
        if (TxChar == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }
}
