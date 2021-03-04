package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.UserMission;

import java.util.List;

public interface DataRepository {

    public LiveData<List<UserMission>> getMissions();

    public UserMission getInitMission();

    public UserMission getQuickMission();

    public LiveData<UserMission> getMissionById(String id);

    public void addMission(UserMission mission);

    public void updateMission(UserMission mission);

    public void updateNumberOfCompletionById(String id, int num);

    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber);

    public void deleteMission(UserMission mission);

    public LiveData<Long> getMissionRepeatStart(String id);

    public LiveData<Long> getMissionRepeatEnd(String id);

    public LiveData<Long> getMissionOperateDay(String id);

//    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end);
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end);
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end);
//
//    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today);
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today);
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today);
//
//    public LiveData<List<UserMission>> getFinishedMissions(long start, long end);
//
//    public LiveData<List<UserMission>> getUnfinishedMissions(long start, long end);
}
