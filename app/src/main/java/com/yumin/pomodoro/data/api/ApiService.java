package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface ApiService<T> {
    public LiveData<List<T>> getMissions();
    public void addMission(T mission);
    public T getInitMission();
    public T getQuickMission(int time, int shortBreakTime, int color);
    public void updateMission(T mission);
    public void deleteMission(T mission);
    public LiveData<T> getMissionById(String id);
    public void updateNumberOfCompletionById(String id, int num);
    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber);
    public LiveData<Long> getMissionRepeatStart(String id);
    public LiveData<Long> getMissionRepeatEnd(String id);
    public LiveData<Long> getMissionOperateDay(String id);
    public LiveData<List<T>> getFinishedMissions(long start, long end);
    public LiveData<List<T>> getUnFinishedMissions(long start, long end);
//    public LiveData<List<T>> getTodayMissionsByOperateDay(long start, long end);
//    public LiveData<List<T>> getTodayMissionsByRepeatType(long start, long end);
//    public LiveData<List<T>> getTodayMissionsByRepeatRange(long start, long end);
//    public LiveData<List<T>> getComingMissionsByOperateDay(long today);
//    public LiveData<List<T>> getComingMissionsByRepeatType(long today);
//    public LiveData<List<T>> getComingMissionsByRepeatRange(long today);
}
