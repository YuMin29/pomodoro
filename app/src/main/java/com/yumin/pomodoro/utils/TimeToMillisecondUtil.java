package com.yumin.pomodoro.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeToMillisecondUtil {
    public static long getStartTime(int year, int month, int day){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(year,month-1,day);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    public static long getTodayInitTime(){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    public static long getTodayStartTime(){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    public static long getTodayEndTime(){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    public static String getYear(long milli){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static String getMonth(long milli){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("M");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static String getDay(long milli) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("d");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static long getStartTime(long milliSeconds) {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndTime(long milliSeconds) {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String getDateString(long milliSeconds){
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return format.format(calendar.getTime());
    }
}
