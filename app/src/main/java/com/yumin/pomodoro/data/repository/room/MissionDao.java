package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yumin.pomodoro.data.MissionState;
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

    @Delete
    void delete(UserMission... missions);

    @Query("DELETE FROM MyMission")
    void deleteAll();

    @Query("SELECT repeatStart FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatStart(int id);

    @Query("SELECT repeatEnd FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionRepeatEnd(int id);

    @Query("SELECT operateDay FROM MyMission WHERE id=:id")
    LiveData<Long> getMissionOperateDay(int id);
}
