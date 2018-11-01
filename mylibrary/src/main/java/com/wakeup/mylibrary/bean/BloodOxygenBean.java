package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class BloodOxygenBean {
    private int bloodOxygen;
    private long timeInMillis;

    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "BloodOxygenBean{" +
                "bloodOxygen=" + bloodOxygen +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
