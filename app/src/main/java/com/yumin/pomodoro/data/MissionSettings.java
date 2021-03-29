package com.yumin.pomodoro.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.yumin.pomodoro.utils.LiveSharedPreference;

public class MissionSettings {
    private SharedPreferences mSharedPreferences;
    private final String preferenceName = "missionSettings";
    private final String KEY_AUTO_START_NEXT_MISSION = "auto_start_next_mission";
    private final String KEY_AUTO_STAR_BREAK = "auto_start_break";
    private final String KEY_INDEX_OF_BACKGROUND_RINGTONE = "index_of_background_ringtone";
    private final String KEY_INDEX_OF_FINISHED_RINGTONE = "index_of_finished_ringtone";
    private final String KEY_DISABLE_BREAK = "disable_break";

    public MissionSettings(Context context){
        mSharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public LiveSharedPreference<Boolean> getAutoStartNextMission(){
        return new LiveSharedPreference<Boolean>(mSharedPreferences, KEY_AUTO_START_NEXT_MISSION, false);
    }

    public void setAutoStartNextMission(boolean value){
        setBooleanValue(KEY_AUTO_START_NEXT_MISSION,value);
    }

    public LiveSharedPreference<Boolean> getAutoStartBreak(){
        return new LiveSharedPreference<Boolean>(mSharedPreferences, KEY_AUTO_STAR_BREAK, false);
    }

    public void settAutoStartBreak(boolean value){
        setBooleanValue(KEY_AUTO_STAR_BREAK,value);
    }

    public LiveSharedPreference<Integer> getIndexOfBackgroundRingtone(){
        return new LiveSharedPreference<Integer>(mSharedPreferences, KEY_INDEX_OF_BACKGROUND_RINGTONE, 1);
    }

    public void setIndexOfBackgroundRingtone(int value){
        setIntegerValue(KEY_INDEX_OF_BACKGROUND_RINGTONE,value);
    }

    public LiveSharedPreference<Integer> getIndexOfFinishedRingtone(){
        return new LiveSharedPreference<Integer>(mSharedPreferences, KEY_INDEX_OF_FINISHED_RINGTONE, 1);
    }

    public void setIndexOfFinishedRingtone(int value){
        setIntegerValue(KEY_INDEX_OF_FINISHED_RINGTONE,value);
    }

    public void setDisableBreak(boolean value) {
        setBooleanValue(KEY_DISABLE_BREAK,value);
    }

    public LiveSharedPreference<Boolean> getDisableBreak(){
        return new LiveSharedPreference<Boolean>(mSharedPreferences, KEY_DISABLE_BREAK, false);
    }

    private void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    private void setIntegerValue(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
}
