package com.yumin.pomodoro.ui.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionSettings;

public class SettingsViewModel extends AndroidViewModel {
    private LiveData<Boolean> mAutoStartNextMission;
    private LiveData<Boolean> mAutoStartBreak;
    private LiveData<Integer> mMissionBackgroundRingtone;
    private LiveData<Integer> mMissionFinishedRingtone;
    private LiveData<Boolean> mDisableBreak;
    private MissionSettings mMissionSettings;

    public SettingsViewModel(Application application) {
        super(application);
        mMissionSettings = new MissionSettings(application);
        fetchData();
    }

    private void fetchData(){
        mAutoStartNextMission = mMissionSettings.getAutoStartNextMission();
        mAutoStartBreak = mMissionSettings.getAutoStartBreak();
        mMissionBackgroundRingtone = mMissionSettings.getIndexOfBackgroundRingtone();
        mMissionFinishedRingtone = mMissionSettings.getIndexOfFinishedRingtone();
        mDisableBreak = mMissionSettings.getDisableBreak();
    }

    public LiveData<Boolean> getAutoStartNextMission(){
        return mAutoStartNextMission;
    }

    public LiveData<Boolean> getAutoStartBreak(){
        return mAutoStartBreak;
    }

    public void setAutoStartNextMission(boolean value){
        mMissionSettings.setAutoStartNextMission(value);
    }

    public void setAutoStartBreak(boolean value){
       mMissionSettings.settAutoStartBreak(value);
    }

    public LiveData<Integer> getIndexOfMissionBackgroundRingtone(){
        return mMissionBackgroundRingtone;
    }

    public LiveData<Integer> getIndexOfFinishedMissionRingtone(){
        return mMissionFinishedRingtone;
    }

    public void setMissionBackgroundRingtone(int index){
        mMissionSettings.setIndexOfBackgroundRingtone(index);
    }

    public void setFinishedMissionRingtone(int index){
        mMissionSettings.setIndexOfFinishedRingtone(index);
    }

    public void setDisableBreak(boolean value){
        mMissionSettings.setDisableBreak(value);
    }

    public LiveData<Boolean> getDisableBreak(){
        return mDisableBreak;
    }
}