package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RoomApiService<T> extends ApiService<T>{
    // mission id -> sql generate id
    public LiveData<T> getMissionById(String id);
    public void updateNumberOfCompletionById(String id, int num);
    public void updateIsFinishedById(int id, boolean finished);
    public LiveData<Long> getMissionRepeatStart(String id);
    public LiveData<Long> getMissionRepeatEnd(String id);
    public LiveData<Long> getMissionOperateDay(String id);
    public LiveData<List<T>> getFinishedMissions();
    public LiveData<List<T>> getUnFinishedMissions();
}
