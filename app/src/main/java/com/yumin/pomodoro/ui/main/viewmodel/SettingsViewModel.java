package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private Application mApplication;


    public SettingsViewModel(Application application) {
        this.mApplication = application;
    }

    public void getAutoStartMission(){

    }

    public void getAutoStartBreak(){

    }

    public void setAutoStartMission(){

    }

    public void setAutoStartBreak(){

    }

    public void getMissionBackgroundRingtone(){

    }

    public void getFinishedMissionRingtone(){

    }

    public void setMissionBackgroundRingtone(){

    }

    public void setFinishedMissionRingtone(){

    }
}