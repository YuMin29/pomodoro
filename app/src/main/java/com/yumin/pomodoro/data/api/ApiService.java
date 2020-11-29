package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public interface ApiService {
    public LiveData<List<Mission>> getMissions(Context context);
    public void addMission(Context context, Mission mission);
    public Mission getInitMission();
    public LiveData<List<Mission>> getTodayMissions(long start, long end);
    public LiveData<List<Mission>> getComingMissions(long today);
    public LiveData<Mission> getMissionById(int id);
    public void updateMission(Mission mission);
    public void deleteMission(Mission mission);
}
