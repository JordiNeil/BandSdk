package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class BloodPressureBean {
    private int bloodPressureHigh;
    private int bloodPressureLow;
    private long timeInMillis;

    public int getBloodPressureHigh() {
        return bloodPressureHigh;
    }

    public void setBloodPressureHigh(int bloodPressureHigh) {
        this.bloodPressureHigh = bloodPressureHigh;
    }

    public int getBloodPressureLow() {
        return bloodPressureLow;
    }

    public void setBloodPressureLow(int bloodPressureLow) {
        this.bloodPressureLow = bloodPressureLow;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "BloodPressureBean{" +
                "bloodPressureHigh=" + bloodPressureHigh +
                ", bloodPressureLow=" + bloodPressureLow +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}

