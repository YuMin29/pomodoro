package com.yumin.pomodoro.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.ui.home.AddMissionViewModel;

public class CountViewViewModel {
    public MutableLiveData<String> missionTime = new MutableLiveData<>();
    public MutableLiveData<String> missionBreak = new MutableLiveData<>();


}
