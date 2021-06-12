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
    void insert(MissionState missionState);

    @Query("UPDATE MissionState SET numberOfCompletion=:value WHERE missionId=:id AND recordDay=:today")
    void updateNumberOfCompletionsById(int id, int value, long today);

    @Query("UPDATE MissionState SET isCompleted=:value WHERE missionId=:id AND recordDay =:today")
    void updateIsFinishedById(int id, boolean value, long today);

    @Query("UPDATE MissionState SET completedDay=:value WHERE missionId=:id AND recordDay =:today")
    void updateFinishedDayById(int id, long value, long today);

    @Query("SELECT MyMission.id,name,time,shortBreakTime,longBreakTime,color,operateDay,goal,repeat,repeatStart,repeatEnd,enableNotification,enableSound,enableVibrate,keepScreenOn,createdTime,firebaseMissionId" +
            " FROM MyMission INNER JOIN MissionState ON MissionState.missionId=MyMission.id" +
            " WHERE recordDay =:today AND isCompleted = 1")
    LiveData<List<UserMission>> getTodayCompletedMissions(long today);

    @Query("SELECT MyMission.id,name,time,shortBreakTime,longBreakTime,color,operateDay,goal,repeat,repeatStart,repeatEnd,enableNotification,enableSound,enableVibrate,keepScreenOn,createdTime,firebaseMissionId" +
            " FROM MyMission INNER JOIN MissionState ON MissionState.missionId=MyMission.id" +
            " WHERE recordDay <:today AND isCompleted = 1")
    LiveData<List<UserMission>> getPastCompletedMissions(long today);

    @Query("SELECT numberOfCompletion FROM MissionState WHERE missionId=:id AND recordDay =:today")
    LiveData<Integer> getNumberOfCompletionById(int id, long today);

    @Query("SELECT * FROM MissionState WHERE missionId=:id AND recordDay =:today")
    LiveData<MissionState> getMissionStateByToday(int id, long today);

    @Query("SELECT * FROM MissionState WHERE missionId=:id")
    LiveData<MissionState> getMissionStateById(int id);

    @Query("SELECT * FROM MissionState")
    LiveData<List<MissionState>> getAllMissionStates();
}
