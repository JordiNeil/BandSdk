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
    public void setResetBand() {
        Log.i(TAG, "setResetBand: ");
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
    public void setOnceOrRealTimeMeasure(int status, int control) {
        Log.i(TAG, "setOnceOrRealTimeMeasure: ");
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
    public void setOnceKeyMeasure(int control) {
        Log.i(TAG, "setOnceKeyMeasure: ");
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
        Log.i(TAG, "setTimeSync: ");
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
     * <p>
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
        int second = calendar.get(Calendar.SECOND);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timeInMillis2);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH) + 1;
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendar.get(Calendar.MINUTE);
        int second2 = calendar.get(Calendar.SECOND);
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
     * 获取实时心率
     *
     * @param bol 0-关 1-开
     */
    public void getTrueTimeRate(int bol) {
        Log.i(TAG, "getTrueTimeRate: ");
        byte[] data = new byte[7];
        data[0] = (byte) 0xAB;
        data[1] = (byte) 0;
        data[2] = (byte) 4;
        data[3] = (byte) 0xff;
        data[4] = (byte) 0x84;
        data[5] = (byte) 0x80;
        data[6] = (byte) bol;
//        data[6] = (byte)0;//占位符，没意义
        broadcastData(data);
    }


    /**
     * 查找手环
     */
    public void findBand() {
        Log.i(TAG, "findBand: ");
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
     * 抬手亮屏
     *
     * @param control 0关  1开
     */
    public void setUpHandLightScreen(int control) {
        Log.i(TAG, "setUpHandLightScreen: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x77;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        Log.i("lq", "抬手亮屏" + "--" + control);
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
     * 心率报警
     *
     * @param control 0关  1开
     */
    public void setHrWarn(int control) {
        Log.i(TAG, "setHrWarn: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x85;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 摇摇拍照指令
     *
     * @param control 0关  1开
     */
    public void setSharkTakePhoto(int control) {
        Log.i(TAG, "setSharkTakePhoto: ");
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
    public void setAntiLostAlert(int control) {
        Log.i(TAG, "setAntiLostAlert: ");
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
    public void setSwitchChineseOrEnglish(int control) {
        Log.i(TAG, "setSwitchChineseOrEnglish: ");
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
    public void set12HourSystem(int control) {
        Log.i(TAG, "set12HourSystem: ");
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
     * 同步天气信息
     *
     * @param weather 0多云 1晴天 2雪天 3雨天
     * @param temp    0(0度以上)  1(0度以下)
     */
    public void setSyncWeather(int weather, int temp) {
        Log.i(TAG, "setSyncWeather: ");
        byte[] bytes = new byte[9];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 6;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7E;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) weather;
        bytes[7] = (byte) temp;
        bytes[8] = (byte) (temp >= 0 ? 0 : 1);
        broadcastData(bytes);
    }


    /**
     * 挂断电话
     */
    public void setHangUpPhone() {
        Log.i(TAG, "setHangUpPhone: ");
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
    public void setSmartWarnNoContent(int MessageId, int type) {
        Log.i(TAG, "setSmartWarnNoContent: ");
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 5;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x72;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) MessageId;//来电提醒、短信提醒等
        bytes[7] = (byte) type;//0开 1关 2来消息通知
        broadcastData(bytes);
    }

    /**
     * 智能提醒,带消息内容
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
        Log.i(TAG,DataHandUtils.bytesToHexStr(bytes));
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
     * 心电开始测量
     */
    public void startMeasureEcg() {
        Log.i(TAG, "startMeasureEcg: ");
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0xAC;
        bytes[1] = (byte) 0x01;
        broadcastData(bytes);
    }

    /**
     * 心电停止测量
     */
    public void stoptMeasureEcg() {
        Log.i(TAG, "stoptMeasureEcg: ");
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0xAC;
        bytes[1] = (byte) 0x00;
        broadcastData(bytes);
    }


    /**
     * 设置闹钟
     * @param id 闹钟索引（最多开8个）
     * @param status 0：关闭闹钟提醒功能  1：开启闹钟提醒功能
     * @param hour  闹钟提醒时间之小时
     * @param minute  闹钟提醒时间之分钟
     * @param repeat
     */
    public void setAlarmClock(int id, int status, int hour, int minute, int repeat) {
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
        data[7] = (byte) status;
        data[8] = (byte) hour;
        data[9] = (byte) minute;
        data[10] = (byte) repeat;
        broadcastData(data);
    }


    /**
     * 校准时间
     */
    public void setCalibrationTime(int hour, int minute) {
        Log.i(TAG, "setCalibrationTime: ");
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 5;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7D;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) hour;//小时
        bytes[7] = (byte) minute;//分
        broadcastData(bytes);
    }

    /**
     * 血压参考值
     */
    public void setBloodPressureReference(int control, int systaltic_value, int diastolic_value) {
        Log.i(TAG, "setBloodPressureReference: ");
        byte[] data = new byte[9];
        data[0] = (byte) 0xAB;
        data[1] = (byte) 0;
        data[2] = (byte) 6;
        data[3] = (byte) 0xff;
        data[4] = (byte) 0x95;
        data[5] = (byte) 0x80;
        data[6] = (byte) control;
        data[7] = (byte) systaltic_value;
        data[8] = (byte) diastolic_value;
        broadcastData(data);
    }


    /**
     * 省电模式
     *
     * @param control 0关  1开
     */
    public void setPowerSaving(int control) {
        Log.i(TAG, "setPowerSaving: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x96;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) control;
        broadcastData(bytes);
    }

    /**
     * 红外参数设置
     *
     * @param num
     */
    public void sethongwai(int num) {
        Log.i(TAG, "红外参数设置: ");
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x98;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) num;
        broadcastData(bytes);
    }

    /**
     * 根据byte长度截取消息
     *
     * @param MessageId
     * @param type
     * @param content
     * @param length
     */
    public void setSmartWarn2(int MessageId, int type, String content, int length) {
        Log.i(TAG, "setSmartWarn2");

        if (TextUtils.isEmpty(content)) {
            Log.i(TAG, "content 空");
            return;
        }

        byte[] sendBytes;//要发送的bytes

        byte[] contentBytes = content.getBytes();
        int contentBytesLength = contentBytes.length;

        Log.i(TAG, "contentBytesLength: " + contentBytesLength + "  length: " + length);
        if (contentBytesLength > length) {
            //如果内容的长度大于规定的最大长度，就截取
            byte[] dest = new byte[length];
            System.arraycopy(contentBytes, 0, dest, 0, length);
            Log.i(TAG, "length:" + dest.length);
            sendBytes = dest;
        } else {
            sendBytes = contentBytes;
        }


        byte[] bytes2 = new byte[8];
        bytes2[0] = (byte) 0xAB;
        bytes2[1] = (byte) 0;
        bytes2[2] = (byte) (sendBytes.length + 5);
        bytes2[3] = (byte) 0xFF;
        bytes2[4] = (byte) 0x72;
        bytes2[5] = (byte) 0x80;
        bytes2[6] = (byte) MessageId;//来电提醒、短信提醒等
        bytes2[7] = (byte) type;//0开 1关 2来消息通知
        byte[] bytes = DataHandUtils.addBytes(bytes2, sendBytes);
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
