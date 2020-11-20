package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public interface ApiService {
    public LiveData<List<Mission>> getMissions(Context context);
    public void addMission(Context context, Mission mission);
    public Mission getInitMission();
    public LiveData<List<Mission>> getMissionsByOperateDay(long start, long end);
}
