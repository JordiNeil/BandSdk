package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class OneButtonMeasurementBean {
    private int heartRate;
    private int bloodOxygen;
    private int bloodPressure_h;
    private int bloodPressure_l;
    private long timeInMillis;

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public int getBloodPressure_h() {
        return bloodPressure_h;
    }

    public void setBloodPressure_h(int bloodPressure_h) {
        this.bloodPressure_h = bloodPressure_h;
    }

    public int getBloodPressure_l() {
        return bloodPressure_l;
    }

    public void setBloodPressure_l(int bloodPressure_l) {
        this.bloodPressure_l = bloodPressure_l;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "OneButtonMeasurementBean{" +
                "heartRate=" + heartRate +
                ", bloodOxygen=" + bloodOxygen +
                ", bloodPressure_h=" + bloodPressure_h +
                ", bloodPressure_l=" + bloodPressure_l +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
