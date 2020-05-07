package com.wakeup.mylibrary.bean;

/**
 * ---------------------------------------------------------------------------------------------
 * 功能描述:
 * ---------------------------------------------------------------------------------------------
 * 时　　间: 2020/5/7 16:37
 * ---------------------------------------------------------------------------------------------
 * 代码创建: 张光耀
 * ---------------------------------------------------------------------------------------------
 * 代码备注:
 * ---------------------------------------------------------------------------------------------
 **/
public class BodytempAndMianyiBean {
    private float bodyTemp;
    private float mianyi;
    private long timeInMillis;

    public BodytempAndMianyiBean(float bodyTemp, float mianyi, long timeInMillis) {
        this.bodyTemp = bodyTemp;
        this.mianyi = mianyi;
        this.timeInMillis = timeInMillis;
    }

    public float getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(float bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public float getMianyi() {
        return mianyi;
    }

    public void setMianyi(float mianyi) {
        this.mianyi = mianyi;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public String toString() {
        return "BodytempAndMianyiBean{" +
                "bodyTemp=" + bodyTemp +
                ", mianyi=" + mianyi +
                ", timeInMillis=" + timeInMillis +
                '}';
    }
}
