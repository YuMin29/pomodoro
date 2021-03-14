package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

public interface ApiService<T,E> {
    public LiveData<List<T>> getMissions();
    public String addMission(T mission);
    public T getInitMission();
    public T getQuickMission(int time, int shortBreakTime, int color);
    public void updateMission(T mission);
    public void deleteMission(T mission);
    public LiveData<T> getMissionById(String id);
    public void updateNumberOfCompletionById(String id, int num);
    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber);
    public LiveData<Long> getMissionRepeatStart(String id);
    public LiveData<Long> getMissionRepeatEnd(String id);
    public LiveData<Long> getMissionOperateDay(String id);
    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end);
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart);
    public LiveData<E> getMissionStateById(String id, long todayStart);
    public void initMissionState(String id);
    public void saveMissionState(String missionId,MissionState missionState);
    public LiveData<List<MissionState>> getMissionStates();
}
