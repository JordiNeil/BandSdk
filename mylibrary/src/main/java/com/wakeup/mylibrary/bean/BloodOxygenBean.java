package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class BloodOxygenBean {
    private int bloodOxygen;
    private long timeInMillis;
    /**
     * 0单机测量 1单次测量 2实时测量
     */
    private int type;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BloodOxygenBean{" +
                "bloodOxygen=" + bloodOxygen +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                ", type=" + type +
                '}';
    }
}
