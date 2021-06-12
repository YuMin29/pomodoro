package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;

public interface ApiService<T,E> {
    LiveData<List<T>> getMissions();
    T getInitMission();
    T getQuickMission(int time, int shortBreakTime, int color);
    void updateMission(T mission);
    void deleteMission(T mission);
    void deleteAllMission();
    LiveData<T> getMissionById(String id);
    void updateNumberOfCompletionById(String id, int num);
    void updateMissionState(String id, boolean finished, int completeOfNumber);
    LiveData<Long> getMissionRepeatStart(String id);
    LiveData<Long> getMissionRepeatEnd(String id);
    LiveData<Long> getMissionOperateDay(String id);
    LiveData<List<UserMission>> getCompletedMissionList(long start, long end);
    LiveData<Integer> getNumberOfCompletionById(String id, long todayStart);
    LiveData<E> getMissionStateByToday(String id, long todayStart);
    void initMissionState(String id);
    void saveMissionState(String missionId,MissionState missionState);
    LiveData<List<MissionState>> getMissionStateList();
    LiveData<List<UserMission>> getPastCompletedMission(long today);
}
