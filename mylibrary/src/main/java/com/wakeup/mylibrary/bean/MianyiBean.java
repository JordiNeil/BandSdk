package com.wakeup.mylibrary.bean;


import com.wakeup.mylibrary.utils.DateUtils;

public class MianyiBean {
    private float data;
    private long timeInMillis;
    public MianyiBean(float data, long timeInMillis) {
        this.data = data;
        this.timeInMillis = timeInMillis;
    }



    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "MianyiBean{" +
                "data=" + data +
                ", timeInMillis=" + DateUtils.formatTime(timeInMillis,"yyyy-MM-dd HH:mm:ss") +
                '}';
    }
}
