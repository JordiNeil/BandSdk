package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class CurrentDataBean {
    private int steps;
    private int calory;
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
        return "CurrentDataBean{" +
                "steps=" + steps +
                ", calory=" + calory +
                ", shallowSleep=" + shallowSleep +
                ", deepSleep=" + deepSleep +
                ", wakeupTimes=" + wakeupTimes +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
