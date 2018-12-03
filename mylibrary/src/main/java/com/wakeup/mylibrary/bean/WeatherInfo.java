package com.wakeup.mylibrary.bean;

public class WeatherInfo {
    private String weatherType;
    private int temperature;

    public WeatherInfo(String weatherType, int temperature) {
        this.weatherType = weatherType;
        this.temperature = temperature;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "{" +
                "0x" + weatherType +
                "," + temperature +
                '}';
    }
}
