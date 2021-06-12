package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

public interface DataRepository {
    LiveData<List<UserMission>> getMissions();

    UserMission getInitMission();

    UserMission getQuickMission();

    LiveData<UserMission> getMissionById(String id);

    String addMission(UserMission mission);

    void updateMission(UserMission mission);

    void updateMissionNumberOfCompletion(String id, int num);

    void updateMissionFinishedState(String id, boolean finished, int completeOfNumber);

    void deleteMission(UserMission mission);

    void deleteAllMission();

    LiveData<Long> getMissionRepeatStart(String id);

    LiveData<Long> getMissionRepeatEnd(String id);

    void initMissionState(String id);

    LiveData<List<UserMission>> getCompletedMissionList(long start, long end);

    LiveData<Integer> getNumberOfCompletionById(String id, long todayStart);

    LiveData<MissionState> getMissionStateById(String id, long todayStart);

    void saveMissionState(String missionId, MissionState missionState);

    LiveData<List<MissionState>> getMissionStateList();

    LiveData<List<UserMission>> getPastCompletedMission(long today);
}
