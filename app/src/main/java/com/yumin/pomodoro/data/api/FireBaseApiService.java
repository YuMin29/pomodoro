package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

public interface FireBaseApiService<T> extends ApiService<T>{
    // mission id -> user uid
    public LiveData<T> getMissionById(String id);
    public void updateNumberOfCompletionById(String id, int num);
    public void updateIsFinishedById(String id, boolean finished);
    public LiveData<Long> getMissionRepeatStart(String id);
    public LiveData<Long> getMissionRepeatEnd(String id);
    public LiveData<Long> getMissionOperateDay(String id);
}
