package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

public class SleepData {
    private int sleepId;//ID=1:进入睡眠时间 ID=2：进入深睡时间
    private int sleepTime;//持续时间
    private long timeInMillis;//开始时间

    public int getSleepId() {
        return sleepId;
    }

    public void setSleepId(int sleepId) {
        this.sleepId = sleepId;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }



    @Override
    public String toString() {
        return "SleepData{" +
                "sleepId=" + sleepId +
                ", sleepTime=" + sleepTime +
                ", timeInMillis=" + CommonUtils.toStrTime(timeInMillis) +
                '}';
    }
}
