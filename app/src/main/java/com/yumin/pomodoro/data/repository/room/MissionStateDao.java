package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

@Dao
public interface MissionStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(MissionState missionState);

    @Query("UPDATE MissionState SET numberOfCompletion=:value WHERE missionId=:id AND recordDay=:today")
    public void updateNumberOfCompletionsById(int id, int value, long today);

    @Query("UPDATE MissionState SET finished=:value WHERE missionId=:id")
    public void updateIsFinishedById(int id, boolean value);

    @Query("UPDATE MissionState SET finishedDay=:value WHERE missionId=:id")
    public void updateFinishedDayById(int id, long value);

    @Query("SELECT MyMission.id,name,time,shortBreakTime,longBreakTime,color,operateDay,goal,repeat,repeatStart,repeatEnd,enableNotification,enableSound,enableVibrate,keepScreenOn,createdTime,firebaseMissionId" +
            " FROM MyMission INNER JOIN MissionState ON MissionState.missionId=MyMission.id" +
            " WHERE recordDay =:today AND finished = 1")
    public LiveData<List<UserMission>> getTodayFinishedMissions(long today);

    @Query("SELECT MyMission.id,name,time,shortBreakTime,longBreakTime,color,operateDay,goal,repeat,repeatStart,repeatEnd,enableNotification,enableSound,enableVibrate,keepScreenOn,createdTime,firebaseMissionId" +
            " FROM MyMission INNER JOIN MissionState ON MissionState.missionId=MyMission.id" +
            " WHERE recordDay <:today AND finished = 1")
    public LiveData<List<UserMission>> getPastFinishedMissions(long today);

    @Query("SELECT numberOfCompletion FROM MissionState WHERE missionId=:id AND recordDay =:today")
    public LiveData<Integer> getNumberOfCompletionById(int id, long today);

    @Query("SELECT * FROM MissionState WHERE missionId=:id AND recordDay =:today")
    public LiveData<MissionState> getMissionStateById(int id, long today);

    @Query("SELECT * FROM MissionState")
    public LiveData<List<MissionState>> getAllMissionStates();
}
