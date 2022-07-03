package com.yumin.pomodoro.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yumin.pomodoro.ui.timer.TimerFragment;

public class PrefUtils {
    private static final String preferenceName = "missionSettings";
    private static final String KEY_AUTO_START_NEXT_MISSION = "auto_start_next_mission";
    private static final String KEY_AUTO_STAR_BREAK = "auto_start_break";
    private static final String KEY_INDEX_OF_BACKGROUND_RINGTONE = "index_of_background_ringtone";
    private static final String KEY_INDEX_OF_FINISHED_RINGTONE = "index_of_finished_ringtone";
    private static final String KEY_DISABLE_BREAK = "disable_break";
    private static final String KEY_TIMER_SERVICE_STATUS = "timer_service_status";

    public static boolean getAutoMission(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_AUTO_START_NEXT_MISSION,false);
    }

    public static boolean getAutoBreak(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_AUTO_STAR_BREAK,false);
    }

    public static boolean getDisableBreak(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_DISABLE_BREAK,false);
    }

    public static int getIndexOfBackgroundRingtone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_INDEX_OF_BACKGROUND_RINGTONE,1);
    }

    public static int getIndexOfFinishedRingtone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_INDEX_OF_FINISHED_RINGTONE,1);
    }

    public static void setTimerServiceStatus(Context context, TimerFragment.TimerStatus status){
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_TIMER_SERVICE_STATUS,status.ordinal());
        editor.apply();
    }

    public static void clearTimerServiceStatus(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_TIMER_SERVICE_STATUS,0);
        editor.apply();
    }
}
