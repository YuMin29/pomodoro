package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

public interface RoomApiService<T> extends ApiService<T>{
    // mission id -> sql generate id
    public LiveData<T> getMissionById(int id);
    public void updateNumberOfCompletionById(int id, int num);
    public void updateIsFinishedById(int id, boolean finished);
    public LiveData<Long> getMissionRepeatStart(int id);
    public LiveData<Long> getMissionRepeatEnd(int id);
    public LiveData<Long> getMissionOperateDay(int id);
}
