package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yumin.pomodoro.data.MissionState;

import java.util.List;

@Dao
public interface MissionStateDao {
    @Insert
    public void insert(MissionState missionState);

    @Query("UPDATE MissionState SET numberOfCompletion=:value WHERE missionId=:id")
    public void updateNumberOfCompletionsById(int id, int value);

    @Query("UPDATE MissionState SET isFinished=:value WHERE missionId=:id")
    public void updateIsFinishedById(int id, boolean value);

    @Query("UPDATE MissionState SET finishedDay=:value WHERE missionId=:id")
    public void updateFinishedDayById(int id, long value);

    @Query("SELECT * FROM MissionState WHERE recordDay =:today AND isFinished = 1")
    public List<MissionState> getFinishedMissions(long today);

    @Query("SELECT numberOfCompletion FROM MissionState WHERE missionId=:id AND recordDay =:today")
    public LiveData<Integer> getNumberOfCompletionById(int id, long today);

    @Query("SELECT * FROM MissionState WHERE missionId=:id AND recordDay =:today")
    public LiveData<MissionState> getMissionStateById(int id, long today);
}
