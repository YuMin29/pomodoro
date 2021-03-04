package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yumin.pomodoro.data.UserMission;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT * FROM MyMission")
    LiveData<List<UserMission>> getAllMissions();

    @Query("SELECT * FROM MyMission WHERE id=:id")
    LiveData<UserMission> getMissionById(int id);

    @Insert
    void insert(UserMission... missions);

    @Update
    void update(UserMission... missions);

    @Query("UPDATE MyMission SET numberOfCompletions=:num WHERE id=:id")
    void updateNumberOfCompletionsById(int id, int num);

    @Query("UPDATE MyMission SET isFinished=:finished WHERE id=:id")
    void updateIsFinishedById(int id, boolean finished);

    @Query("UPDATE MyMission SET finishedDay=:finishedDay WHERE id=:id")
    void updateFinishedDayById(int id, long finishedDay);

    @Delete
    void delete(UserMission... missions);

    @Query("SELECT repeatStart FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatStart(int id);

    @Query("SELECT repeatEnd FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatEnd(int id);

    @Query("SELECT operateDay FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionOperateDay(int id);

    @Query("SELECT * FROM MyMission WHERE isFinished = 1")
    LiveData<List<UserMission>> getFinishedMissions();

    @Query("SELECT * FROM MyMission WHERE isFinished = 0")
    LiveData<List<UserMission>> getUnfinishedMissions();

    @Query("SELECT * FROM MyMission WHERE operateDay BETWEEN :dayst AND :dayet")
    LiveData<List<UserMission>> getTodayMissionsByOperateDay(long dayst, long dayet);

    @Query("SELECT * FROM MyMission WHERE repeat = 1 AND operateDay < :dayst")
    LiveData<List<UserMission>> getTodayMissionsByRepeatType(long dayst);

    @Query("SELECT * FROM MyMission WHERE repeatStart <= :dayst AND repeatEnd >= :dayst")
    LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long dayst);

    @Query("SELECT * FROM MyMission WHERE operateDay > :current OR repeat = 1 OR :current <= repeatEnd")
    LiveData<List<UserMission>> getComingMissionsByOperateDay(long current);

    @Query("SELECT * FROM MyMission WHERE repeat = 1")
    LiveData<List<UserMission>> getComingMissionsByRepeatType();

    @Query("SELECT * FROM MyMission WHERE repeatEnd >= :current")
    LiveData<List<UserMission>> getComingMissionsByRepeatRange(long current);
}
