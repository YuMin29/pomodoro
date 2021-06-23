package com.yumin.pomodoro.data.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface MissionDao {
    @Query("SELECT * FROM MyMission")
    LiveData<List<UserMission>> getAllMissions();

    @Query("SELECT * FROM MyMission WHERE id=:id")
    LiveData<UserMission> getMissionById(int id);

    @Query("SELECT * FROM MyMission WHERE id=:id")
    UserMission getMissionByIdForService(int id);

    @Insert
    void insert(UserMission mission);

    @Insert
    Long insertAndGetId(UserMission mission);

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

    /**
     * 不重複 且 執行日為今天 [today start]<----operate day---->[today end]
     * @param type
     * @param todayStart
     * @param todayEnd
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat =:type AND operateDay >= :todayStart AND operateDay <= :todayEnd")
    LiveData<List<UserMission>> getTodayNoneRepeatMissions(int type, long todayStart, long todayEnd);

    /**
     * 每日重複： everyday && 執行日為今天或今天以前
     * @param type
     * @param todayEnd
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat=:type AND operateDay <= :todayEnd")
    LiveData<List<UserMission>> getTodayRepeatEverydayMissions(int type, long todayEnd);

    /**
     * 特定範圍重複： 判斷今天有無在範圍區間內  [start]<--- today --->[end]
     * @param type
     * @param todayEnd
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat=:type AND repeatStart <= :todayEnd AND repeatEnd >= :todayEnd")
    LiveData<List<UserMission>> getTodayRepeatCustomizeMissions(int type, long todayEnd);

    /**
     * 不重複： 執行日大於今天結束
     * @param type
     * @param todayEnd
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat =:type AND operateDay >:todayEnd")
    LiveData<List<UserMission>> getComingNoneRepeatMissions(int type, long todayEnd);

    /**
     * 每日重複 ： everyday
     * @param type
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat =:type")
    LiveData<List<UserMission>> getComingRepeatEverydayMissions(int type);

    /**
     * 特定範圍重複 ： 範例
     * 區間 4/1-4/10
     * <p>
     * 3/20 X 未來 以今天判斷未來有 4/1 開始 today end < [range start]
     * 4/1  O 未來 以今天判斷未來還有~4/10 today end <= [range end]
     * 4/10 O 未來 以今天判斷未來沒有 today end > [range end]
     * @param type
     * @param todayEnd
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat =:type AND repeatEnd >:todayEnd")
    LiveData<List<UserMission>> getComingRepeatCustomizeMissions(int type, long todayEnd);

    /**
     * 不重複 且 執行日為今天以前 operate day <---- [today start]
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat=:type AND operateDay < :todayStart")
    LiveData<List<UserMission>> getPastNoneRepeatMissions(int type, long todayStart);

    /**
     * 每日重複： everyday && 執行日為今天或今天以前
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat=:type AND operateDay < :todayStart")
    LiveData<List<UserMission>> getPastRepeatEverydayMissions(int type, long todayStart);

    /**
     * 特定範圍重複： 判斷今天有無在範圍區間內  repeat start <---- today start
     * @return
     */
    @Query("SELECT * FROM MyMission WHERE repeat=:type AND repeatStart < :todayStart")
    LiveData<List<UserMission>> getPastRepeatCustomizeMissions(int type, long todayStart);
}
