package com.wakeup.mylibrary.bean;

import com.wakeup.mylibrary.utils.CommonUtils;

/**
 * ID = 52 指令获取的睡眠数据(整点测量数据和当前数据里面也有睡眠数据)
 */
public class SleepData {
    private int sleepId;//ID=1:进入睡眠时间 ID=2：进入深睡时间
    private int sleepTime;//持续时间 分钟
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
