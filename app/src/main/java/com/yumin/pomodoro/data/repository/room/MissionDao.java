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

    @Query("SELECT * FROM MyMission WHERE operateDay BETWEEN :dayst AND :dayet OR repeat = 1 OR repeatStart <= :dayst <= repeatEnd")
    LiveData<List<Mission>> getTodayMissions(long dayst, long dayet);

    @Query("SELECT * FROM MyMission WHERE operateDay > :current OR repeat = 1 OR :current <= repeatEnd")
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

    @Query("SELECT repeatStart FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatStart(int id);

    @Query("SELECT repeatEnd FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatEnd(int id);

    @Query("SELECT operateDay FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionOperateDay(int id);

    @Query("SELECT * FROM MyMission WHERE isFinished = 1")
    LiveData<List<Mission>> getFinishedMissions();

    @Query("SELECT * FROM MyMission WHERE isFinished = 0")
    LiveData<List<Mission>> getUnfinishedMissions();
}
