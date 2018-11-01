package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class BloodPressureBean {
    private int bloodPressure;
    private long timeInMillis;

    public int getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(int bloodPressure) {
        this.bloodPressure = bloodPressure;
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
                "bloodPressure=" + bloodPressure +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}

