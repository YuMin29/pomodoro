package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

public interface DataRepository {

    public LiveData<List<UserMission>> getMissions();

    public UserMission getInitMission();

    public UserMission getQuickMission();

    public LiveData<UserMission> getMissionById(String id);

    public void addMission(UserMission mission);

    public void updateMission(UserMission mission);

    public void updateMissionNumberOfCompletion(String id, int num);

    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber);

    public void deleteMission(UserMission mission);

    public LiveData<Long> getMissionRepeatStart(String id);

    public LiveData<Long> getMissionRepeatEnd(String id);

    public LiveData<Long> getMissionOperateDay(String id);

    public void initMissionState(String id);

    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end);

    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart);

    public LiveData<MissionState> getMissionStateById(String id, long todayStart);
}
