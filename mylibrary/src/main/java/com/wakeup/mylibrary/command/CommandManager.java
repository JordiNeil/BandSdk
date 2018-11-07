package com.wakeup.mylibrary.command;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.wakeup.mylibrary.service.BluetoothService;
import com.wakeup.mylibrary.utils.DataHandUtils;

import java.util.Calendar;


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
     * 清除数据
     */
    public void clearData() {
        Log.i(TAG, "clearData: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x23;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) 0x00;
        broadcastData(bytes);
    }

    /**
     * 恢复手环出厂设置
     */
    public void resetBand() {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0xFF;
        bytes[5] = (byte) 0x80;
        broadcastData(bytes);
    }


    /**
     * 单次、实时测量
     *
     * @param status  心率：0X09(单次) 0X0A(实时)
     *                血氧：0X11(单次) 0X12(实时)
     *                血压：0X21(单次) 0X22(实时)
     * @param control 0关  1开
     */
    public void onceOrRealTimeMeasure(int status, int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x31;
        bytes[5] = (byte) status;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 一键测量
     *
     * @param control 0(关)  1(开)
     */
    public void onceKeyMeasure(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x32;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 同步时间
     */
    public void setTimeSync() {
        //当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 11;
        bytes[3] = (byte) 0xff;
        bytes[4] = (byte) 0x93;
        bytes[5] = (byte) 0x80;
//        bytes[6] = (byte)0;//占位符
        bytes[7] = (byte) ((year & 0xff00) >> 8);
        bytes[8] = (byte) (year & 0xff);
        bytes[9] = (byte) (month & 0xff);
        bytes[10] = (byte) (day & 0xff);
        bytes[11] = (byte) (hour & 0xff);
        bytes[12] = (byte) (minute & 0xff);
        bytes[13] = (byte) (second & 0xff);
        broadcastData(bytes);
    }

    /**
     * 下拉同步数据
     *
     * @param timeInMillis 同步数据起始时间毫秒值
     */
    public void syncData(long timeInMillis) {
        Log.i(TAG, "syncData: ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        byte[] data = new byte[12];
        data[0] = (byte) 0xAB;
        data[1] = (byte) 0;
        data[2] = (byte) 9;
        data[3] = (byte) 0xff;
        data[4] = (byte) 0x51;
        data[5] = (byte) 0x80;
//        data[6] = (byte)0;//占位符，没意义
        data[7] = (byte) ((year - 2000));
        data[8] = (byte) (month);
        data[9] = (byte) (day);
        data[10] = (byte) (hour);
        data[11] = (byte) (minute);
        broadcastData(data);
    }

    /**
     * 下拉同步数据 带有连续心率手环
     *
     * Byte 7-11的时间值为APP发送给手环用来筛选需求的整点存储数据。
     * Byte 12-16的时间值为APP发送给手环用来筛选需求的心率存储数据。
     * 如2017/12/12 12:00，手环会将这时间之后的数据发送给APP
     * （如果有比这个时间更新的数据）
     *
     * @param timeInMillis 同步数据起始时间毫秒值
     */
    public void syncDataHr(long timeInMillis, long timeInMillis2) {
        Log.i(TAG, "syncDataHr: ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timeInMillis2);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH) + 1;
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendar.get(Calendar.MINUTE);
        byte[] data = new byte[17];
        data[0] = (byte) 0xAB;
        data[1] = (byte) 0;
        data[2] = (byte) 14;
        data[3] = (byte) 0xff;
        data[4] = (byte) 0x51;
        data[5] = (byte) 0x80;
//        data[6] = (byte)0;//占位符，没意义
        data[7] = (byte) ((year - 2000));
        data[8] = (byte) (month);
        data[9] = (byte) (day);
        data[10] = (byte) (hour);
        data[11] = (byte) (minute);

        data[12] = (byte) ((year2 - 2000));
        data[13] = (byte) (month2);
        data[14] = (byte) (day2);
        data[15] = (byte) (hour2);
        data[16] = (byte) (minute2);
        broadcastData(data);
    }





    /**
     * 震动手环
     */
    public void vibrate() {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x71;
        bytes[5] = (byte) 0x80;
        Log.i(TAG, "查找手环");
        broadcastData(bytes);
    }


    /**
     * 整点测量
     *
     * @param control 0关  1开
     */
    public void openHourlyMeasure(int control) {
        Log.i(TAG, "openHourlyMeasure: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x78;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }













    /**
     * 挂断电话
     */
    public void setHangUpPhone() {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x81;
        bytes[5] = (byte) 0;
        broadcastData(bytes);
    }



    /**
     * 智能提醒
     *
     * @param MessageId
     * @param type
     */
    public void sendMessage(int MessageId, int type, String content) {
        byte[] bytes1 = null;
        int length = 0;
        if (!TextUtils.isEmpty(content)) {
            bytes1 = content.getBytes();
            length = bytes1.length;
        }
        byte[] bytes2 = new byte[8];
        bytes2[0] = (byte) 0xAB;
        bytes2[1] = (byte) 0;
        bytes2[2] = (byte) (length + 5);
        bytes2[3] = (byte) 0xFF;
        bytes2[4] = (byte) 0x72;
        bytes2[5] = (byte) 0x80;
        bytes2[6] = (byte) MessageId;//来电提醒、短信提醒等
        bytes2[7] = (byte) type;//0开 1关 2来消息通知
        byte[] bytes = DataHandUtils.addBytes(bytes2, bytes1);
        Log.i(TAG, DataHandUtils.bytesToHexStr(bytes));
        broadcastData(bytes);
    }

    /**
     * 查看电量
     */
    public void getBatteryInfo() {
        Log.i(TAG, "getBatteryInfo: ");
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x91;
        bytes[5] = (byte) 0x80;
        broadcastData(bytes);
    }

    /**
     * 查看版本
     */
    public void getVersion() {
        Log.i(TAG, "getVersion: ");
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x92;
        bytes[5] = (byte) 0x80;
        broadcastData(bytes);
    }



    /**
     * 设置闹钟
     *
     * @param id     闹钟索引（最多开8个）
     * @param control 0：关闭闹钟提醒功能  1：开启闹钟提醒功能
     * @param hour   闹钟提醒时间之小时
     * @param minute 闹钟提醒时间之分钟
     * @param repeat
     */
    public void setAlarmClock(int id, int control, int hour, int minute, int repeat) {
        byte[] data = new byte[11];
        data[0] = (byte) 0xAB;
        data[1] = (byte) 0;
        data[2] = (byte) 8;
        //数据id + status 共 3 bytes
        data[3] = (byte) 0xff;
        data[4] = (byte) 0x73;
        data[5] = (byte) 0x80;
        //数据值
        data[6] = (byte) id;
        data[7] = (byte) control;
        data[8] = (byte) hour;
        data[9] = (byte) minute;
        data[10] = (byte) repeat;
        broadcastData(data);
    }










    /**
     * 发送用户信息给手环(使用于我司wearfit1.0的设备，具体情况请咨询我司固件开发人员)
     * @param stepLength
     * @param age
     * @param height
     * @param weight
     * @param distanceUnit
     * @param goal
     */
    public void sendUserInfo(int stepLength, int age, int height, int weight,
                              int distanceUnit, int goal) {

        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 11;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x74;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) stepLength;
        bytes[7] = (byte) age;
        bytes[8] = (byte) height;
        bytes[9] = (byte) weight;
        bytes[10] = (byte) 115;
        bytes[11] = (byte) 75;
        bytes[12] = (byte) distanceUnit;
        bytes[13] = (byte) (goal/1000);
        broadcastData(bytes);
    }


    /**
     * 发送用户信息给手环(使用于我司wearfit2.0的设备，具体情况请咨询我司固件开发人员)
     * @param stepLength
     * @param age
     * @param height
     * @param weight
     * @param distanceUnit
     * @param goal
     */
    public void sendUserInfo2(int stepLength, int age, int height, int weight,
                              int distanceUnit, int goal) {

        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 11;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x74;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) stepLength;
        bytes[7] = (byte) age;
        bytes[8] = (byte) height;
        bytes[9] = (byte) weight;
        bytes[10] = (byte) distanceUnit;
        bytes[11] = (byte) (goal/1000);
        broadcastData(bytes);
    }

    /**
     * 久坐提醒
     */
    public void sedentary(int control,int startHour,int startMinute,int endHour,int endMinute,int interval){
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 9;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x75;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        bytes[7] = (byte) startHour;
        bytes[8] = (byte) startMinute;
        bytes[9] = (byte) endHour;
        bytes[10] = (byte) endMinute;
        bytes[11] = (byte) interval;
        broadcastData(bytes);

    }


    /**
     * 勿扰模式
     */
    public void doNotDisturbModel(int control,int startHour,int startMinute,int endHour,int endMinute) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 8;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x76;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;//0关 1开
        bytes[7] = (byte) startHour;
        bytes[8] = (byte) startMinute;
        bytes[9] = (byte) endHour;
        bytes[10] = (byte) endMinute;
        broadcastData(bytes);
    }


    /**
     * 抬手亮屏
     *
     * @param control 0关  1开
     */
    public void upHandLightScreen(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x77;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 摇摇拍照指令
     *
     * @param control 0关  1开
     */
    public void sharkTakePhoto(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x79;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 防丢
     *
     * @param control 0关  1开
     */
    public void antiLostAlert(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7A;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }


    /**
     * 中英文切换
     *
     * @param control 0中文  1英文
     */
    public void switchChineseOrEnglish(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7B;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 时间制切换
     *
     * @param control 0（24小时制）  1(12小时制)
     */
    public void switch12Hour(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7C;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }
    /**
     * 手环查找手机
     *
     * @param control 0（关闭）  1(开启)
     */
    public void findPhone(int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7d;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 睡眠范围设置
     * @param control 0:关(关则为全天有效）1:开
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
    public void sleepRange(int control,int startHour,int startMinute,int endHour,int endMinute ){
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 8;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7F;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        bytes[7] = (byte) startHour;
        bytes[8] = (byte) startMinute;
        bytes[9] = (byte) endHour;
        bytes[10] = (byte) endMinute;
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
