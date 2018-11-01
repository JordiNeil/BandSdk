package com.wakeup.mylibrary.bean;

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
