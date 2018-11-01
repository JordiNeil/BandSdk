package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class HourlyMeasureDataBean {
    private int steps;
    private int calory;
    private int heartRate;
    private int bloodOxygen;
    private int bloodPressure_high;
    private int bloodPressure_low;
    private int shallowSleep;
    private int deepSleep;
    private int wakeupTimes;

    private long timeInMillis;


    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalory() {
        return calory;
    }

    public void setCalory(int calory) {
        this.calory = calory;
    }

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

    public int getBloodPressure_high() {
        return bloodPressure_high;
    }

    public void setBloodPressure_high(int bloodPressure_high) {
        this.bloodPressure_high = bloodPressure_high;
    }

    public int getBloodPressure_low() {
        return bloodPressure_low;
    }

    public void setBloodPressure_low(int bloodPressure_low) {
        this.bloodPressure_low = bloodPressure_low;
    }

    public int getShallowSleep() {
        return shallowSleep;
    }

    public void setShallowSleep(int shallowSleep) {
        this.shallowSleep = shallowSleep;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public int getWakeupTimes() {
        return wakeupTimes;
    }

    public void setWakeupTimes(int wakeupTimes) {
        this.wakeupTimes = wakeupTimes;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "HourlyMeasureDataBean{" +
                "steps=" + steps +
                ", calory=" + calory +
                ", heartRate=" + heartRate +
                ", bloodOxygen=" + bloodOxygen +
                ", bloodPressure_high=" + bloodPressure_high +
                ", bloodPressure_low=" + bloodPressure_low +
                ", shallowSleep=" + shallowSleep +
                ", deepSleep=" + deepSleep +
                ", wakeupTimes=" + wakeupTimes +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
