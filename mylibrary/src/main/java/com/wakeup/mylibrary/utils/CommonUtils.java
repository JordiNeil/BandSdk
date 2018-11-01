package com.wakeup.mylibrary.utils;

import java.text.SimpleDateFormat;

public class CommonUtils {
    public static String toStrTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(timeInMillis);
        return format;
    }
}
