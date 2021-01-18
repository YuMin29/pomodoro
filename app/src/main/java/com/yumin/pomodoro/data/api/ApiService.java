package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public interface ApiService {
    public LiveData<List<Mission>> getMissions();
    public void addMission(Mission mission);
    public Mission getInitMission();
    public Mission getQuickMission(int time, int shortBreakTime, int color);
    public LiveData<List<Mission>> getTodayMissions(long start, long end);
    public LiveData<List<Mission>> getComingMissions(long today);
    public LiveData<Mission> getMissionById(int id);
    public void updateMission(Mission mission);
    public void deleteMission(Mission mission);
    public void updateNumberOfCompletionById(int id, int num);
    public void updateIsFinishedById(int id, boolean finished);
    public LiveData<Long> getMissionRepeatStart(int id);
    public LiveData<Long> getMissionRepeatEnd(int id);
    public LiveData<Long> getMissionOperateDay(int id);
    public LiveData<List<Mission>> getFinishedMissions();
    public LiveData<List<Mission>> getUnFinishedMissions();
}
