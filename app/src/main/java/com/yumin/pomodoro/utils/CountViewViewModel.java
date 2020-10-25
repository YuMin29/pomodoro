package com.yumin.pomodoro.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.ui.home.AddMissionViewModel;

public class CountViewViewModel {
    public MutableLiveData<Integer> missionCount = new MutableLiveData<>();


}
