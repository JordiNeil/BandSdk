package com.wakeup.mylibrary.bean;

/**
 * 电池数据
 */
public class Battery {
    private int battery;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "Battery{" +
                "battery=" + battery +
                '}';
    }
}
