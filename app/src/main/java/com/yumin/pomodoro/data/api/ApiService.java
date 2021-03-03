package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface ApiService<T> {
    public LiveData<List<T>> getMissions();
    public void addMission(T mission);
    public T getInitMission();
    public T getQuickMission(int time, int shortBreakTime, int color);
    public LiveData<List<T>> getTodayMissionsByOperateDay(long start, long end);
    public LiveData<List<T>> getTodayMissionsByRepeatType(long start, long end);
    public LiveData<List<T>> getTodayMissionsByRepeatRange(long start, long end);
    public LiveData<List<T>> getComingMissionsByOperateDay(long today);
    public LiveData<List<T>> getComingMissionsByRepeatType(long today);
    public LiveData<List<T>> getComingMissionsByRepeatRange(long today);
    public void updateMission(T mission);
    public void deleteMission(T mission);
}
