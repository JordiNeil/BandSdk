package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

/**
 * 心率数据
 */
public class HeartRateBean {
    private int heartRate;
    private long timeInMillis;
    /**
     * 0单机测量心率 1单次测量心率 2实时测量心率 3连续心率手环实时心率
     */
    private int type;

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HeartRateBean{" +
                "heartRate=" + heartRate +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                ", type=" + type +
                '}';
    }
}
