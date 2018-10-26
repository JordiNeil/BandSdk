package com.wakeup.mylibrary.command;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.DataHandUtils;

import java.nio.ByteBuffer;


/**
 * Created by Harry on 2018/5/22.
 */

public class CommandManager {
    private static final String TAG = CommandManager.class.getSimpleName();
    private static Context mContext;
    private static CommandManager instance;


    public static synchronized CommandManager getInstance(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;

    }


    /**
     * 应答
     *
     * @param data6
     * @param data7
     */
    public void anwser(int data6, int data7) {
        Log.i(TAG,"应答");

        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x10;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x00;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) data6;
        bytes[7] = (byte) data7;
        broadcastData(bytes);
    }

    /**
     * 应答错误
     */
    private void anwserForError() {
        Log.i(TAG,"应答错误");

        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x30;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x00;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        broadcastData(bytes);
    }





    /**
     * 验证密码
     *
     * @param password
     */
    public void verifyPassword(String password) {
        Log.i(TAG,"验证密码");

        if (TextUtils.isEmpty(password)) {
            return;
        }
        byte[] bytes2 = password.getBytes();
        int length = bytes2.length;

        byte[] bytes1 = new byte[13];
        bytes1[0] = (byte) 0xDB;
        bytes1[1] = (byte) 0x00;
        bytes1[2] = (byte) 0x00;
        bytes1[3] = (byte) (length + 5);
        bytes1[4] = (byte) 0x01;
        bytes1[5] = (byte) 0x01;
        bytes1[6] = (byte) 0x00;
        bytes1[7] = (byte) 0x00;

        bytes1[8] = (byte) 0x01;
        bytes1[9] = (byte) 0x00;
        bytes1[10] = (byte) 0x05;
        bytes1[11] = (byte) 0x00;
        bytes1[12] = (byte) 0x08;

        byte[] combined = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, combined, 0, bytes1.length);
        System.arraycopy(bytes2, 0, combined, bytes1.length, bytes2.length);

        broadcastData(combined);
    }




    /**
     * 发送钱包序列号，名字，私钥，到设备
     *
     * @param serialNumber
     * @param name
     * @param privateKey
     * @param coinType
     */
    public void setPrivateKey(String password, String serialNumber, String name, String privateKey, int coinType) {
        Log.i(TAG,"发送钱包序列号，名字，私钥，到设备");

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(serialNumber) || TextUtils.isEmpty(name) || TextUtils.isEmpty
                (privateKey)) {
            return;
        }
        byte[] passwordBytes = password.getBytes();//密码
        byte[] serialNumberBytes = serialNumber.getBytes();//序列号
        byte[] nameBytes = name.getBytes();//名称
        byte[] keyBytes = privateKey.getBytes();//私钥

        byte[] nameLengthBytes = new byte[2];
        nameLengthBytes[0] = (byte) 0x00;
        nameLengthBytes[1] = (byte) nameBytes.length;

        byte[] keyBytesLength = new byte[2];
        keyBytesLength[0] = (byte) coinType;
        keyBytesLength[1] = (byte) keyBytes.length;

        byte[] bytes1 = new byte[13];
        bytes1[0] = (byte) 0xDB;
        bytes1[1] = (byte) 0x00;
        bytes1[2] = (byte) 0x00;
        bytes1[3] = (byte) (passwordBytes.length + serialNumberBytes.length + 2 + nameBytes.length + 2 + keyBytes.length + 5);//第二个包的总长度
        bytes1[4] = (byte) 0x00;
        bytes1[5] = (byte) 0x00;
        bytes1[6] = (byte) 0x00;
        bytes1[7] = (byte) 0x00;

        bytes1[8] = (byte) 0x02;//command id
        bytes1[9] = (byte) 0x00;
        bytes1[10] = (byte) 0x01;//key
        bytes1[11] = (byte) 0x00;
        bytes1[12] = (byte) (passwordBytes.length + serialNumberBytes.length + 2 + nameBytes.length + 2 + keyBytes.length);
        //后面的所有长度


        byte[] bigByteArray = new byte[bytes1.length + passwordBytes.length+serialNumberBytes.length + 2 + nameBytes
                .length + 2 +
                keyBytes
                .length];
        Log.i(TAG, "bigByteArray: " + bigByteArray.length);

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes1);
        target.put(passwordBytes);
        target.put(serialNumberBytes);
        target.put(nameLengthBytes);
        target.put(nameBytes);
        target.put(keyBytesLength);
        target.put(keyBytes);

        byte[] array = target.array();
        Log.i(TAG, String.valueOf(array.length));
        Log.i(TAG, DataHandUtils.bytesToHexStr(array));
        broadcastData(array);
    }

    /**
     * 通过序列号获取设备存的私钥
     */
    public void getPrivateKey(String password, String serialNumber) {
        Log.i(TAG,"输入密码 通过序列号获取设备存的私钥");

        if (TextUtils.isEmpty(serialNumber)) {
            return;
        }
        byte[] serialNumberBytes = serialNumber.getBytes();
        byte[] passwordBytes = password.getBytes();


        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) (passwordBytes.length+serialNumberBytes.length + 5);
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x02;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x03;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) (passwordBytes.length+serialNumberBytes.length);


        byte[] bigByteArray = new byte[bytes.length + passwordBytes.length+serialNumberBytes.length];

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes);
        target.put(passwordBytes);
        target.put(serialNumberBytes);
        byte[] array = target.array();

        broadcastData(array);
    }


    //---------------------------

    /**
     * 获取初始化状态
     */
    public void getInitStatus(){
        Log.i(TAG,"获取初始化状态");
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x05;
        bytes[4] = (byte) 0x01;
        bytes[5] = (byte) 0x01;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x01;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x01;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x00;
        broadcastData(bytes);
    }


    /**
     * 设置密码
     *
     * @param password
     */
    public void setPassword(String password) {
        Log.i(TAG,"设置密码");

        if (TextUtils.isEmpty(password)) {
            return;
        }
        byte[] passwordBytes = password.getBytes();
        int length = passwordBytes.length;

        byte[] bytes1 = new byte[13];
        bytes1[0] = (byte) 0xDB;
        bytes1[1] = (byte) 0x00;
        bytes1[2] = (byte) 0x00;
        bytes1[3] = (byte) (length + 5);
        bytes1[4] = (byte) 0x00;
        bytes1[5] = (byte) 0x00;
        bytes1[6] = (byte) 0x00;
        bytes1[7] = (byte) 0x00;

        bytes1[8] = (byte) 0x01;
        bytes1[9] = (byte) 0x00;
        bytes1[10] = (byte) 0x03;
        bytes1[11] = (byte) 0x00;
        bytes1[12] = (byte) length;

        byte[] combined = new byte[bytes1.length + passwordBytes.length];
        System.arraycopy(bytes1, 0, combined, 0, bytes1.length);
        System.arraycopy(passwordBytes, 0, combined, bytes1.length, passwordBytes.length);

        broadcastData(combined);
    }


    /**
     * 获取硬件钱包唯一标识
     */
    public void getDeviceIdentifier(){
        Log.i(TAG,"获取硬件钱包唯一标识");
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x05;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x01;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x05;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x00;
        broadcastData(bytes);
    }
    /**
     * 删除钱包
     * @param password
     * @param serialNumber
     */
    public void deletePrivateKey(String password, String serialNumber){
        Log.i(TAG,"删除钱包");
        if (TextUtils.isEmpty(serialNumber)||TextUtils.isEmpty(password)) {
            return;
        }
        byte[] serialNumberBytes = serialNumber.getBytes();
        byte[] passwordBytes = password.getBytes();

        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) (passwordBytes.length+serialNumberBytes.length + 5);
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x02;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x05;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) (passwordBytes.length+serialNumberBytes.length);

        byte[] bigByteArray = new byte[bytes.length + passwordBytes.length+serialNumberBytes.length];

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes);
        target.put(passwordBytes);
        target.put(serialNumberBytes);
        byte[] array = target.array();

        broadcastData(array);

    }

    /**
     * 恢复出厂设置
     * @param password
     */
    public void reset(String password){
        Log.i(TAG,"恢复出厂设置");
        if (TextUtils.isEmpty(password)) {
            return;
        }
        byte[] passwordBytes = password.getBytes();
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) (passwordBytes.length+5);
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x03;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x01;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) passwordBytes.length;

        byte[] bigByteArray = new byte[bytes.length + passwordBytes.length];

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes);
        target.put(passwordBytes);
        byte[] array = target.array();

        broadcastData(array);

    }

    public void otaMode(String password){
        Log.i(TAG,"进入ota模式");
        if (TextUtils.isEmpty(password)) {
            return;
        }
        byte[] passwordBytes = password.getBytes();
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) (passwordBytes.length+5);
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x04;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x01;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) passwordBytes.length;

        byte[] bigByteArray = new byte[bytes.length + passwordBytes.length];

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes);
        target.put(passwordBytes);
        byte[] array = target.array();

        broadcastData(array);
    }

    /**
     * 没有初始化的ota
     */
    public void otaMode2(){
        Log.i(TAG,"无密码进入ota模式");
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x0d;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x04;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x01;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x08;

        byte[] bigByteArray = new byte[bytes.length + 8];

        ByteBuffer target = ByteBuffer.wrap(bigByteArray);
        target.put(bytes);
        byte[] array = target.array();

        Log.i(TAG,DataHandUtils.bytesToHexStr(array));
        broadcastData(array);
    }


    /**
     * 切换硬件语言0x03
     */
    public void switchLang(int control){
        Log.i(TAG,"切换硬件语言 "+control);
        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x06;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x05;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x03;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x01;
        bytes[13] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 获取版本号
     */
    public void getVersion(){
        Log.i(TAG,"获取版本号");

        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0xDB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) 0x05;
        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x05;
        bytes[9] = (byte) 0x00;
        bytes[10] = (byte) 0x01;
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x00;
        broadcastData(bytes);
    }

    /**
     * @brief Broadcast intent with pointed bytes.
     * @param[in] bytes Array of byte to send on BLE.
     */
    private void broadcastData(byte[] bytes) {
        final Intent intent = new Intent(BluetoothService.ACTION_SEND_DATA_TO_BLE);
        intent.putExtra(BluetoothService.EXTRA_SEND_DATA_TO_BLE, bytes);
        try {
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


}
