package com.coco.terminal.cocobizlog.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时区处理
 *
 * @author ckli01
 * @date 2019-04-10
 */
public class TimeZoneUtil {


    private static TimeZone sh = TimeZone.getTimeZone("Asia/Shanghai");
    private static TimeZone utc = TimeZone.getTimeZone("UTC");

    /**
     * 获取 上海 时区 时间
     *
     * @param date
     * @return
     */
    public static String getShangHaiTime(Date date) {
        return getTime(date, sh);
    }

    /**
     * 获取 世界统一时间 0 时区
     *
     * @param date
     * @return
     */
    public static String getUTCTime(Date date) {
        return getTime(date, utc);
    }


    private static String getTime(Date date, TimeZone zone) {
        if (null == date) {
            date = Calendar.getInstance().getTime();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(zone);
        return dateFormat.format(date);
    }




}

    
    
  