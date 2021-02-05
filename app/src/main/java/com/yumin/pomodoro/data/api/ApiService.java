package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

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
    public LiveData<T> getMissionById(int id);
    public void updateMission(T mission);
    public void deleteMission(T mission);
    public void updateNumberOfCompletionById(int id, int num);
    public void updateIsFinishedById(int id, boolean finished);
    public LiveData<Long> getMissionRepeatStart(int id);
    public LiveData<Long> getMissionRepeatEnd(int id);
    public LiveData<Long> getMissionOperateDay(int id);
    public LiveData<List<T>> getFinishedMissions();
    public LiveData<List<T>> getUnFinishedMissions();

}
