package com.wakeup.mylibrary.utils;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Wade 时间操作工具类
 */
public class DateUtils {
    public static String FORMAT_MDHM_CN = "MM月dd日 HH:mm";
    public static String FORMAT_DATE = "MM月dd日";
    public static String FORMAT_TIME = "dd日 HH";
    public static long MILLISECOND_OF_MINUTE = 60 * 1000;
    public static long MILLISECOND_OF_HOUR = MILLISECOND_OF_MINUTE * 60;
    public static long MILLISECOND_OF_DAY = MILLISECOND_OF_HOUR * 24;

    /**
     * @param date
     * @return 当日的初始时间戳
     */
    public static long getStartTimeStampOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        long timeStamp = date.getTime();
        long hourDiff = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
        long minuteDiff = c.get(Calendar.MINUTE) * 60 * 1000;
        long secondDiff = c.get(Calendar.SECOND) * 1000;
        long milliSecondDiff = c.get(Calendar.MILLISECOND);
        return timeStamp - hourDiff - minuteDiff - secondDiff - milliSecondDiff;
    }

    /**
     * @param timeInMillis
     * @return 时间戳对应的小时和分钟数 格式: 11:35
     */
    public static String getHourAndMinutes(long timeInMillis) {
        Date date = new Date(timeInMillis);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    }

    /**
     * @param timeInMillis
     * @return 时间戳对应的日期小时和分钟数 格式:3-28 12:12
     */
    public static String getDayHourMinutes(long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    /**
     *
     * 判断两个时间是否一天
     * */

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    /**
     *
     *
     * 是否是今天
     * */

    public static boolean isToday(long timeInMillis){
        long l1 = System.currentTimeMillis();
        return formatTime(l1,"yyyy.MM.dd").equals(formatTime(timeInMillis,"yyyy.MM.dd"));

    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    /**
     * @param timeInMillis
     * @return 时间戳对应的日期小时和分钟数 格式:3月28日 12:12
     */
    public static String getHourMinutes( long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    public static int getHour( long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        return Integer.parseInt(simpleDateFormat.format(date));
    }

    public static String getADate(Context context) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        return sdf.format(date);
    }

    //获取当前时间 26号 03
    public static String getTimeForLong(long longTime) {
        Date date = new Date(longTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_TIME);
        return simpleDateFormat.format(date);
    }

    /**
     * @param timeInMillis
     * @return 下一分钟对应的时间戳
     */
    public static long getNextMinuteTimeStamp(long timeInMillis) {
        return timeInMillis + getNextMinuteTimeStampDiff(timeInMillis);
    }

    /**
     * @param timeInMillis
     * @return 下一小时对应的时间戳
     */
    public static long getNextHourTimeStamp(long timeInMillis) {
        return timeInMillis + getNextHourTimeStampDiff(timeInMillis);
    }

    /**
     * @param timeInMillis
     * @return 到下一分钟的时间差
     */
    public static long getNextMinuteTimeStampDiff(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        long secondDiff = c.get(Calendar.SECOND) * 1000;
        long milliSecondDiff = c.get(Calendar.MILLISECOND);
        long addition = MILLISECOND_OF_MINUTE - secondDiff - milliSecondDiff;
        return addition;
    }

    /**
     * @param timeInMillis
     * @return 到下一小时的时间差
     */
    public static long getNextHourTimeStampDiff(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        long minuteDiff = c.get(Calendar.MINUTE) * 60 * 1000;
        long secondDiff = c.get(Calendar.SECOND) * 1000;
        long milliSecondDiff = c.get(Calendar.MILLISECOND);
        long addition = MILLISECOND_OF_HOUR - minuteDiff - secondDiff
                - milliSecondDiff;
        return addition;
    }

    /**
     * @param timeDiff
     * @return 时间差多包涵的分钟数
     */
    public static int getContainedMinutes(long timeDiff) {
        if (timeDiff > 0) {
            return (int) (timeDiff / (60 * 1000));
        }
        return 0;
    }

    /**
     * @param timeInMillis
     * @return 毫秒值对应的日期
     */
    public static String getDateFromLong(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        String format = simpleDateFormat.format(new Date(timeInMillis));
        return format;
    }

    /**
     * @param timeInMillis
     * @return 毫秒值对应的日期
     */
    public static String getYearDateFromLong(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String format = simpleDateFormat.format(new Date(timeInMillis));
        return format;
    }

    /**
     * @param timeInMillis
     * @return 毫秒值对应的日期
     */
    public static String getTimeFromLong(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String format = simpleDateFormat.format(new Date(timeInMillis));
        return format;
    }

    /**
     * @param timeInMillis
     * @return 毫秒数对应的星期几
     */
    public static int getDayOfWeekFromLong(long timeInMillis, Context context) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timeInMillis));
        int whichDay = cal.get(Calendar.DAY_OF_WEEK);
        return whichDay;
    }

    public static String getNextHourFromLong(Long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 23) {
            hour = 0;
        } else {
            hour = hour + 1;
        }
        return hour + ":00";
    }

    public static int getHourFromLong(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteFromLong(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.YEAR);
    }

    public static int getHourFromSec(long sec) {
        return (int) (sec / 3600);
    }

    public static int getMinuteFromSec(long sec) {
        return (int) ((sec - (getHourFromSec(sec) * 3600)) / 60);
    }

    //将时间转换为毫秒值
    public static long getMilliSecondFromTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        long millionSeconds = System.currentTimeMillis();
        try {
            millionSeconds = sdf.parse(time).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
    }


    private static long dayTime = 24 * 60 * 60 * 1000;




    //获取月的数据
    public static List<String> getMonthTimeData(Context context) {
        ArrayList<String> monthDatas = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int monthCount = calendar.get(Calendar.MONTH) + 1;//本月
        String a ;
        for (int j = year; j >= 2016; j--) {
            if (j == year){
                for (int i = monthCount; i >= 1; i--) {
                    a = j   +"."+i ;

                    monthDatas.add(a);
                }
            }else{
                for (int i = 12; i >= 1; i--) {
                    a = j   +"."+i ;

                    monthDatas.add(a);
                }
            }
        }
        return monthDatas;
    }

    /**
     * 毫秒值对应的星期几
     *
     * @param ctx
     * @param time long时间
     * @return int 型
     */
    public static int getLongForWeek(Context ctx, long time) {
        int dayOfWeek = 0;
        int dayOfWeekFromLong = DateUtils.getDayOfWeekFromLong(time, ctx);
        Log.e("lqq","dayOfWeekFromLong:"+dayOfWeekFromLong);
        switch (dayOfWeekFromLong) {
            case 1:
                dayOfWeek = 1;
                break;
            case 2:
                dayOfWeek = 2;
                break;
            case 3:
                dayOfWeek = 3;
                break;
            case 4:
                dayOfWeek = 4;
                break;
            case 5:
                dayOfWeek = 5;
                break;
            case 6:
                dayOfWeek = 6;
                break;
            case 7:
                dayOfWeek = 7;
                break;
        }
        return dayOfWeek;
    }

    /**
     * 毫秒值对应的几号
     *
     * @param ctx
     * @param time
     * @return
     */
    public static int getLongForMonth(Context ctx, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        String format = simpleDateFormat.format(new Date(time));
        return Integer.valueOf(format);
    }

    /**
     * 得到当月的天数
     *
     * @param time
     * @return
     */
    public static int getDayCountForMonth(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //   根据date赋值
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.i("test", actualMaximum + "天");
        return actualMaximum;
    }

    /**
     * 获取当月的天数
     * @return
     */
    public static int getCurrentMonthDayCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDate = calendar.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * @param timeInMillis
     * @return 毫秒值对应的日期
     */
    public static String getDetailTimeFromLong(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = simpleDateFormat.format(new Date(timeInMillis));
        return format;
    }

    /**
     * @return 毫秒值对应的日期
     */
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String format = simpleDateFormat.format(new Date());
        return format;
    }

    /**
     *  获取当前日期的小时整点 例如：2017/2/11 18:16:0  返回18
     */
    public static int getTimeMillisHour(long timeMillis){
        Date date1 = new Date(timeMillis);
        return date1.getHours();

    }
    /**
     *  获取当前日期的对应的位置
     */
    public static int getTimeMillisHours(long timeMillis){
        Date date1 = new Date(timeMillis);
        int hour = date1.getHours();
        int min = date1.getMinutes();
        int time = hour*12+min/5;
        return time;

    }







    /**
     * 获取平均入睡时间
     * @param starTime
     * @return
     */
    public static String getAverageSleepTime(List starTime){
        int h=0;
        int m=0;
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        List list=new ArrayList();
        for (int i = 0; i < starTime.size(); i++) {
            if ((long)starTime.get(i)!=0){
                String time = sdf.format((long) starTime.get(i));
                list.add(time);
            }
        }

        Log.i("zst",list.toString());
        if (list.size()!=0){

            List<String[]> strings=new ArrayList<String[]>();
            for (int i = 0; i < list.size(); i++) {
                String s= (String) list.get(i);
                String[] split = s.split(":");
                if (Integer.parseInt(split[0])<21){
                    split[0]=String.valueOf(Integer.parseInt(split[0])+24);
                }
                strings.add(split);
            }


            int hours=0;
            int min=0;
            int total;
            int average;
            for (int i = 0; i < strings.size(); i++) {
                String[] s=strings.get(i);
                for (String s1 : s) {
                    Log.i("zst",s1);
                }

                hours+=Integer.parseInt(strings.get(i)[0]);
                min+=Integer.parseInt(strings.get(i)[1]);
            }


            total=hours*60+min;


            average=total/list.size();

             h =  average/60>=24?average/60-24:average/60;
             m=average%60;
            Log.i("zst", h +":"+m);
        }


        return (h<10?("0"+h):h)+":"+(m<10?("0"+m):m);
    }

    public static String formatTime(long startTimeStampOfDay,String string) {
        SimpleDateFormat sdf=new SimpleDateFormat(string);
        return sdf.format(startTimeStampOfDay);
    }
}
