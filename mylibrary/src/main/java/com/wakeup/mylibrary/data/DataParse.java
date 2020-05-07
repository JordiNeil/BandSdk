package com.wakeup.mylibrary.data;

import com.wakeup.mylibrary.Config;
import com.wakeup.mylibrary.bean.BandInfo;
import com.wakeup.mylibrary.bean.Battery;
import com.wakeup.mylibrary.bean.BloodOxygenBean;
import com.wakeup.mylibrary.bean.BloodPressureBean;
import com.wakeup.mylibrary.bean.CurrentDataBean;
import com.wakeup.mylibrary.bean.HeartRateBean;
import com.wakeup.mylibrary.bean.HourlyMeasureDataBean;
import com.wakeup.mylibrary.bean.OneButtonMeasurementBean;
import com.wakeup.mylibrary.bean.SleepData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 解析蓝牙发过来的数据
 */
public class DataParse {
    private static final String TAG = DataParse.class.getSimpleName();

    private static DataParse instance;


    public static synchronized DataParse getInstance() {

        if (instance == null) {
            instance = new DataParse();
        }
        return instance;

    }

    public Object parseData(List<Integer> datas) {
        Object object = null;

        if (datas.get(0) == 0xAB) {



            switch (datas.get(4)) {
                case 0x91:
                    //电池电量
                    Battery battery = new Battery();
                    battery.setBattery(datas.get(7));
                    object = battery;
                    break;
                case 0x92:
                    BandInfo bandInfo = new BandInfo();
                    bandInfo.setFirmwareVersionCode(datas.get(6) + (float) datas.get(7) / 100);
                    bandInfo.setBandVersionCode(datas.get(8));
                    if (datas.size() > 15) {
                        int type = datas.get(15);
                        bandInfo.setCanSetStepLength(((type >> 0) & 0x01) == 0);
                        bandInfo.setCanSetSleepTime(((type >> 1) & 0x01) == 0);
                        bandInfo.setCanSet12Hours(((type >> 2) & 0x01) == 0);
                        bandInfo.setHasWeixinSport(((type >> 3) & 0x01) == 0);
                        bandInfo.setHasHeartWarn(((type >> 4) & 0x01) == 1);
                        bandInfo.setNordic(((type >> 5) & 0x01) == 0);
                        bandInfo.setNeedPhoneSerialNumber(((type >> 6) & 0x01) == 1);
                    }


                    //长度超过16字节
                    if (datas.size() > 16) {
                        bandInfo.setBandType(datas.get(16));

                        if (bandInfo.getBandType() == 0x0B
                                || bandInfo.getBandType() == 0x0D
                                || bandInfo.getBandType() == 0x0E
                                || bandInfo.getBandType() == 0x0F) {
                            //带连续心率的手环
                            Config.hasContinuousHeart = true;

                        } else if (bandInfo.getBandType() == 0x0c) {
                            //心电手环
                            Config.hasECG = true;
                        } else {
                            //普通手环
                            Config.general = true;
                        }


                        //长度超过17字节
                        if (datas.size() > 17) {
                            int type1 = datas.get(17);
                            bandInfo.setHasPagesManager(((type1 >> 0) & 0x01) == 1);
                            bandInfo.setHasInstagram(((type1 >> 1) & 0x01) == 1);
                            bandInfo.setHasJiuzuotixing(((type1 >> 2) & 0x01) == 1);
                            bandInfo.setHeartRateSaveBattery(((type1 >> 3) & 0x01) == 1);
                            bandInfo.setHeartRateHongwai(((type1 >> 4) & 0x01) == 1);
                            bandInfo.setMoreMessage(((type1 >> 5) & 0x01) == 1);
                        }
                    }


                    object = bandInfo;

                    break;
                case 0x9B:
                    BandInfo bandInfo1 = new BandInfo();
                    //判断有没有体温，免疫力
                    int a = datas.get(5);
                    //bit0 判断免疫力功能
                    boolean hasMianyi = (a & 0x01) == 0x01;
                    //bit1 判断体温功能
                    boolean hasTiWen = (a & 0x02) == 0x02;
                    //bit2 判断连续体温功能
                    boolean hasNewTiWen = (a & 0x04) == 0x04;
                    bandInfo1.setHasTiwen(hasTiWen);
                    bandInfo1.setHasLianxuTiwen(hasNewTiWen);
                    object = bandInfo1;
                    break;

                case 0x51:

                    //记录数据的时间
                    int year = datas.get(6) + 2000;
                    int month = datas.get(7);
                    int day = datas.get(8);
                    int hour = datas.get(9);
                    int min = datas.get(10);
                    String time = year + String.format("%02d", month)
                            + String.format("%02d", day)
                            + String.format("%02d", hour)
                            + String.format("%02d", min);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                    long timeInMillis = 0;
                    try {
                        timeInMillis = sdf.parse(time).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Config.hasContinuousHeart) {
                        //如果是连续心率手环
                        object = parse51(datas, object, timeInMillis);


                    } else if (Config.hasECG) {
                        //如果是心电手环

                    } else {
                        //如果是普通手环
                        object = parse51(datas, object, timeInMillis);
                    }


                    break;
                case 0x52:
                    //入睡时间记录

                    //记录数据的时间
                    int year1 = datas.get(6) + 2000;
                    int month1 = datas.get(7);
                    int day1 = datas.get(8);
                    int hour1 = datas.get(9);
                    int min1 = datas.get(10);
                    String time1 = year1 + String.format("%02d", month1)
                            + String.format("%02d", day1)
                            + String.format("%02d", hour1)
                            + String.format("%02d", min1);

                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm");
                    long timeInMillis1 = 0;
                    try {
                        timeInMillis1 = sdf1.parse(time1).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int sleepId = datas.get(11);//1睡眠时间  2深睡
                    int sleepTime = datas.get(12) * 16 * 16 + datas.get(13);
                    SleepData sleepData = new SleepData();
                    sleepData.setSleepId(sleepId);
                    sleepData.setSleepTime(sleepTime);
                    sleepData.setTimeInMillis(timeInMillis1);

                    object = sleepData;


                    break;

                case 0x31:
                    //单次测量、实时测量
                    switch (datas.get(5)) {
                        case 0x09:
                            //心率（单次）
                            int hrValue = datas.get(6);

                            HeartRateBean heartRateBean = new HeartRateBean();
                            heartRateBean.setHeartRate(hrValue);
                            heartRateBean.setTimeInMillis(System.currentTimeMillis());
                            heartRateBean.setType(1);

                            object = heartRateBean;
                            break;
                        case 0x11:
                            //血氧（单次）
                            int bloodOxygen = datas.get(6);

                            BloodOxygenBean bloodOxygenBean = new BloodOxygenBean();
                            bloodOxygenBean.setBloodOxygen(bloodOxygen);
                            bloodOxygenBean.setTimeInMillis(System.currentTimeMillis());
                            bloodOxygenBean.setType(1);

                            object = bloodOxygenBean;
                            break;
                        case 0x21:
                            //血压（单次）
                            int bloodPressureHigh = datas.get(6);
                            int bloodPressureLow = datas.get(7);

                            BloodPressureBean bloodPressureBean = new BloodPressureBean();
                            bloodPressureBean.setBloodPressureHigh(bloodPressureHigh);
                            bloodPressureBean.setBloodPressureLow(bloodPressureLow);
                            bloodPressureBean.setType(1);

                            object = bloodPressureBean;

                            break;
                        case 0X0A:
                            //心率（实时）
                            int hrValue1 = datas.get(6);

                            HeartRateBean heartRateBean1 = new HeartRateBean();
                            heartRateBean1.setHeartRate(hrValue1);
                            heartRateBean1.setTimeInMillis(System.currentTimeMillis());
                            heartRateBean1.setType(2);

                            object = heartRateBean1;
                            break;
                        case 0x12:
                            //血氧（实时）
                            int bloodOxygen1 = datas.get(6);

                            BloodOxygenBean bloodOxygenBean1 = new BloodOxygenBean();
                            bloodOxygenBean1.setBloodOxygen(bloodOxygen1);
                            bloodOxygenBean1.setTimeInMillis(System.currentTimeMillis());
                            bloodOxygenBean1.setType(2);

                            object = bloodOxygenBean1;
                            break;
                        case 0x22:
                            //血压（实时）
                            int bloodPressureHigh1 = datas.get(6);
                            int bloodPressureLow1 = datas.get(7);

                            BloodPressureBean bloodPressureBean1 = new BloodPressureBean();
                            bloodPressureBean1.setBloodPressureHigh(bloodPressureHigh1);
                            bloodPressureBean1.setBloodPressureLow(bloodPressureLow1);
                            bloodPressureBean1.setType(2);

                            object = bloodPressureBean1;
                            break;
                    }



                    break;

                case 0x32:
                    //一键测量
                    int heartRate = datas.get(6);
                    int bloodOxygen = datas.get(7);
                    int bloodPressure_h = datas.get(8);
                    int bloodPressure_l = datas.get(9);

                    OneButtonMeasurementBean oneButtonMeasurementBean = new OneButtonMeasurementBean();
                    oneButtonMeasurementBean.setHeartRate(heartRate);
                    oneButtonMeasurementBean.setBloodOxygen(bloodOxygen);
                    oneButtonMeasurementBean.setBloodPressure_h(bloodPressure_h);
                    oneButtonMeasurementBean.setBloodPressure_l(bloodPressure_l);
                    oneButtonMeasurementBean.setTimeInMillis(System.currentTimeMillis());

                    object = oneButtonMeasurementBean;

                    break;


                case 0x84:
                    //连续心率手环 实时心率返回
                    int hrValue1 = datas.get(6);

                    HeartRateBean heartRateBean = new HeartRateBean();
                    heartRateBean.setHeartRate(hrValue1);
                    heartRateBean.setTimeInMillis(System.currentTimeMillis());
                    heartRateBean.setType(3);

                    object = heartRateBean;

                    break;
                default:

                    break;
            }
        }


        return object;
    }

    private Object parse51(List<Integer> datas, Object object, long timeInMillis) {
        if (datas.get(5) == 0x11) {
            //单机测量 连续心率数据
            int hrValue = datas.get(11);
            HeartRateBean heartRateBean = new HeartRateBean();
            heartRateBean.setTimeInMillis(timeInMillis);
            heartRateBean.setHeartRate(hrValue);
            heartRateBean.setType(0);
            object = heartRateBean;

        } else if (datas.get(5) == 0x12) {
            //单机测量 血氧数据
            int bloodOxygen = datas.get(11);
            BloodOxygenBean bloodOxygenBean = new BloodOxygenBean();
            bloodOxygenBean.setBloodOxygen(bloodOxygen);
            bloodOxygenBean.setTimeInMillis(timeInMillis);
            bloodOxygenBean.setType(0);
            object = bloodOxygenBean;


        } else if (datas.get(5) == 0x14) {
            //单机测量 血压数据
            int bloodPressure_h = datas.get(11);
            int bloodPressure_l = datas.get(12);
            BloodPressureBean bloodPressureBean = new BloodPressureBean();
            bloodPressureBean.setBloodPressureHigh(bloodPressure_h);
            bloodPressureBean.setBloodPressureLow(bloodPressure_l);
            bloodPressureBean.setTimeInMillis(timeInMillis);
            bloodPressureBean.setType(0);
            object = bloodPressureBean;


        } else if (datas.get(5) == 0x20) {
            //整点数据
            int steps = (datas.get(10) << 16) + (datas.get(11) << 8) +
                    datas.get(12);

            int calory = (datas.get(13) << 16) + (datas.get(14) << 8) +
                    datas.get(15);

            int heartRate = datas.get(16);
            int bloodOxygen = datas.get(17);
            int bloodPressure_high = datas.get(18);
            int bloodPressure_low = datas.get(19);


            HourlyMeasureDataBean hourlyMeasureDataBean = new HourlyMeasureDataBean();
            hourlyMeasureDataBean.setSteps(steps);
            hourlyMeasureDataBean.setCalory(calory);
            hourlyMeasureDataBean.setHeartRate(heartRate);
            hourlyMeasureDataBean.setBloodOxygen(bloodOxygen);
            hourlyMeasureDataBean.setBloodPressure_high(bloodPressure_high);
            hourlyMeasureDataBean.setBloodPressure_low(bloodPressure_low);
            hourlyMeasureDataBean.setTimeInMillis(timeInMillis + 3600 * 1000);//整点数据时间加一个小时

//            int shallowSleep = datas.get(21) * 60 + datas.get(22);
//            int deepSleep = datas.get(23) * 60 + datas.get(24);
//            int wakeupTimes = datas.get(25);
//            hourlyMeasureDataBean.setShallowSleep(shallowSleep);
//            hourlyMeasureDataBean.setDeepSleep(deepSleep);
//            hourlyMeasureDataBean.setWakeupTimes(wakeupTimes);


            object = hourlyMeasureDataBean;

        } else if (datas.get(5) == 0x08) {
            //当前计步、卡路里、睡眠值

            int steps = (datas.get(6) << 16) + (datas.get(7) << 8) + datas.get(8);
            int calory = (datas.get(9) << 16) + (datas.get(10) << 8) + datas.get(11);
            int shallowSleep = datas.get(12) * 60 + datas.get(13);//分
            int deepSleep = datas.get(14) * 60 + datas.get(15);
            int wakeupTimes = datas.get(16);

            CurrentDataBean currentDataBean = new CurrentDataBean();
            currentDataBean.setSteps(steps);
            currentDataBean.setCalory(calory);
            currentDataBean.setShallowSleep(shallowSleep);
            currentDataBean.setDeepSleep(deepSleep);
            currentDataBean.setWakeupTimes(wakeupTimes);
            currentDataBean.setTimeInMillis(System.currentTimeMillis());

            object = currentDataBean;


        }
        return object;
    }


}
