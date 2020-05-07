package com.wakeup.mylibrary.bean;


import com.wakeup.mylibrary.utils.DateUtils;

public class BodyTempBean{
    private float bodyTemp;
    private long timeInMillis;



    public BodyTempBean() {
    }

    public BodyTempBean(float bodyTemp, long timeInMillis) {
        this.bodyTemp = bodyTemp;
        this.timeInMillis = timeInMillis;
    }

    public float getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(float bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "BodyTempBean{" +
                "bodyTemp=" + bodyTemp +
                ", timeInMillis=" + DateUtils.formatTime(timeInMillis,"yyyy/MM/dd HH:mm:ss") +
                '}';
    }
}
