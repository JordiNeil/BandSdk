package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class HeartRateBean {
    private int heartRate;
    private long timeInMillis;

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

    @Override
    public String toString() {
        return "HeartRateBean{" +
                "heartRate=" + heartRate +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
