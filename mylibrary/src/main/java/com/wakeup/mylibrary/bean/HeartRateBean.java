package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class HeartRateBean {
    private int heartRate;
    private long timeInMillis;
    /**
     * 0 单机测量 1单次测量 2实时测量
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
