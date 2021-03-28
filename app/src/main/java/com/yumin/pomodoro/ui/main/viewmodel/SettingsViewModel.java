package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.MissionSettings;

public class SettingsViewModel extends ViewModel {
    private Application mApplication;
    private LiveData<Boolean> mAutoStartNextMission;
    private LiveData<Boolean> mAutoStartBreak;
    private LiveData<Integer> mMissionBackgroundRingtone;
    private LiveData<Integer> mMissionFinishedRingtone;
    private MissionSettings mMissionSettings;

    public SettingsViewModel(Application application) {
        this.mApplication = application;
        mMissionSettings = new MissionSettings(application);
        fetchData();
    }

    private void fetchData(){
        mAutoStartNextMission = mMissionSettings.getAutoStartNextMission();
        mAutoStartBreak = mMissionSettings.getAutoStartBreak();
        mMissionBackgroundRingtone = mMissionSettings.getIndexOfBackgroundRingtone();
        mMissionFinishedRingtone = mMissionSettings.getIndexOfFinishedRingtone();
    }

    public LiveData<Boolean> getAutoStartNextMission(){
        return this.mAutoStartNextMission;
    }

    public LiveData<Boolean> getAutoStartBreak(){
        return this.mAutoStartBreak;
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
}