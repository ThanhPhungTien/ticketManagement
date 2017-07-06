/*
 * Utils.java
 * Process common solution

 * Author  : tupn
 * Created : 2/16/2016
 * Modified: $Date: 2016-06-27 14:43:13 +0700 (Mon, 27 Jun 2016) $

 * Copyright © 2015 www.mdi-astec.vn
 **************************************************************************************************/

package com.dinhcv.ticketmanagement.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;


public class Utils {
    private Utils() {

    }

    public static String convertToStringAroundNumber(int number) {

        int year = Calendar.getInstance().get(Calendar.YEAR) % 100;

        DecimalFormat df2 = new DecimalFormat("000000");
        String format = df2.format(number);
        Debug.normal("TEST FORMAT: " + format);

        String format1 =  String.valueOf(year) + format;
        return format1;
    }


    public static final String SHARE_PREFS_LAST_ACCESS_TIME = "last.acces.time";
    public static void setLicenseCode(Context context, int code){
        //save to preference
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARE_PREFS_LAST_ACCESS_TIME, MODE_PRIVATE).edit();
        editor.putInt("Licensecode", code);
        editor.apply();
    }

    public static int getLicenseCode(Context context){
        //save to preference
        SharedPreferences sp = context.getSharedPreferences(SHARE_PREFS_LAST_ACCESS_TIME, MODE_PRIVATE);
        int code = sp.getInt("Licensecode", 0); //load from prefers
        Debug.normal("License code"+code);
        return code;
    }



    public static boolean isPantech()
    {
        //Sony
        String manufacturer = android.os.Build.MANUFACTURER;
        Debug.normal("Manufacture: "+manufacturer);
        if (manufacturer.contains("Pantech"))
            return true;
        else
            return false;
    }

    public static boolean isOppo()
    {
        //Sony
        String manufacturer = android.os.Build.MANUFACTURER;
        Debug.normal("Manufacture: "+manufacturer);
        if (manufacturer.contains("OPPO"))
            return true;
        else
            return false;
    }

    public static String getTotalTime(Date timeIn, Date timeOut) {
        boolean isOverDay = false;
        long subTime = (timeOut.getTime() - timeIn.getTime());
        int time = (int) (subTime / 1000); // giiay
        int day = 0;
        int hour = 0;
        int minute = 0;
        int DAY = 24 * 60 * 60;
        int HOUR = 3600;
        int MINUTE = 60;
        if (time > DAY) {
            isOverDay = true;
            day = time / DAY;
            int timeSub1 = time % DAY;
            if (timeSub1 > HOUR) {
                hour = timeSub1 / HOUR;
                int timeSub2 = time % MINUTE;
                if (minute > 60) {
                    minute = timeSub2 / MINUTE;
                } else minute = 1;
            } else {
                if (time > 60) {
                    minute = time / MINUTE;
                } else minute = 1;
            }
        } else {
            day = 0;
            if (time > HOUR) {
                hour = time / HOUR;
                int timeSub2 = time % MINUTE;
                if (minute > 60) {
                    minute = timeSub2 / MINUTE;
                } else minute = 1;
            } else {
                if (time > 60) {
                    minute = time / MINUTE;
                } else minute = 1;
            }
        }


        String totalTime = "";
        if (isOverDay) {
            totalTime = day + " Ngày " + hour + " Giờ " + minute + " Phút ";
        } else {
            totalTime = hour + " Giờ " + minute + " Phút ";
        }

        return totalTime;
    }


    public static long getRevenueTotal(Date timeIn, Date timeOut, m_setting_block settingBlock) {
        boolean isOverDay = false;
        long subTime = (timeOut.getTime() - timeIn.getTime());
        int time = (int) (subTime / 1000); // giiay
        int day = 0;
        int hour = 0;
        int minute = 0;
        int DAY = 24 * 60 * 60;
        int HOUR = 3600;
        int MINUTE = 60;
        if (time > DAY) {
            isOverDay = true;
            day = time / DAY;
            int timeSub1 = time % DAY;
            if (timeSub1 > HOUR) {
                hour = timeSub1 / HOUR;
                int timeSub2 = time % MINUTE;
                if (minute > 60) {
                    minute = timeSub2 / MINUTE;
                } else minute = 0;
            } else {
                if (time > 60) {
                    minute = time / MINUTE;
                } else minute = 0;
            }
        } else {
            day = 0;
            if (time > HOUR) {
                hour = time / HOUR;
                int timeSub2 = time % MINUTE;
                if (minute > 60) {
                    minute = timeSub2 / MINUTE;
                } else minute = 0;
            } else {
                if (time > 60) {
                    minute = time / MINUTE;
                } else minute = 0;
            }
        }


        long revenue = 0;

        if (minute > 0) {
            hour = hour + 1;
        }

        if (day > 0) {
            hour = hour + day * 24;
        }

        if (hour > settingBlock.time1) {
            int remainHour = hour - settingBlock.time1;
            if (remainHour >= settingBlock.time2) {
                int hex = remainHour / settingBlock.time2;
                if ((remainHour % settingBlock.time2) > 0) {
                    hex = hex + 1;
                }

                revenue = settingBlock.money1 + hex * settingBlock.money2;
            } else {
                revenue = settingBlock.money1 + settingBlock.money2;
            }
        } else {
            revenue = settingBlock.money1;
        }


        return revenue;
    }


    public static String convertFeeToString(long number) {
        DecimalFormat df2 = new DecimalFormat("###,###,##0");
        String format = df2.format(number);
        Debug.normal("TEST FORMAT: " + format);

        return format;
    }


    /**
     * * Convert date to string with format date month year
     *
     * @param hour  int
     * @param minute  int
     * @return date string
     */
    public static String convertTimeToString(int hour, int minute) {
        String hourStr = String.format("%02d", hour);
        String minuteStr = String.format("%02d", minute);

        String time = hourStr +":"+minuteStr +":00";
        Debug.normal("Time: "+time);
        return time;
    }


    /**
     * * Convert date to string with format date month year
     *
     * @param input date
     * @return date string
     */
    public static String convertDateInToString(Date input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return format.format(input);
    }


    /**
     * * Convert date to string with format date month year
     *
     * @param input date
     * @return date string
     */
    public static String convertOnlyDateToString(Date input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return format.format(input);
    }


    /**
     * Conver date to string
     *
     * @param input: date
     * @return String date by local
     */
    public static String convertDateToString(Date input) {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return format.format(input);
    }

    /**
     * Conver string to date
     *
     * @param input: String
     * @return date
     */
    public static Date convertStringToDate(String input) {
//        DateFormat format =  DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return format.parse(input);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * ISO8601 Date Format
     */
    private static final SimpleDateFormat s_iso8601_dateFormat = new SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

    /**
     * Convert Date to string by ISO8601 format<br>
     * etc. <b>2014-12-26T02:59:37+00:00</b>
     *
     * @param date date time
     * @return date by ISO8601 string
     */
    static public String dateToStringByISO8601(Date date) {
        s_iso8601_dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return s_iso8601_dateFormat.format(date);
    }

    /**
     * Convert Date to string by Japan local<br>
     * etc. <b>2014-12-26T02:59:37+00:00</b>
     *
     * @param date date time
     * @return date by ISO8601 string
     */
    static public String dateToStringByJapan(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }


}
/******************************************************************************
 * End of file
 *****************************************************************************/