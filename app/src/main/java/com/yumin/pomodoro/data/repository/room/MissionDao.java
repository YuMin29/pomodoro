package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT * FROM MyMission")
    LiveData<List<Mission>> getAllMissions();

    @Query("SELECT * FROM MyMission WHERE operateDay BETWEEN :dayst AND :dayet")
    LiveData<List<Mission>> getTodayMissions(long dayst, long dayet);

    @Query("SELECT * FROM MyMission WHERE operateDay > :current")
    LiveData<List<Mission>> getComingMissions(long current);

    @Query("SELECT * FROM MyMission WHERE id=:id")
    LiveData<Mission> getMissionById(int id);

    @Insert
    void insert(Mission... missions);

    @Update
    void update(Mission... missions);

    @Query("UPDATE MyMission SET numberOfCompletions=:num WHERE id=:id")
    void updateNumberOfCompletionsById(int id, int num);

    @Query("UPDATE MyMission SET isFinished=:finished WHERE id=:id")
    void updateIsFinishedById(int id, boolean finished);

    @Delete
    void delete(Mission... missions);

}
