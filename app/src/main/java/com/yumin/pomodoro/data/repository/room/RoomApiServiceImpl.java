package com.yumin.pomodoro.data.repository.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.RoomApiService;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

// TODO: 3/3/21 Use Firebase getCurrentUser to distinguish use Room or Firebase
public class RoomApiServiceImpl implements RoomApiService<UserMission> {
    private static final String TAG = "[ApiServiceImpl]";
    private List<UserMission> missions;
    private MissionDao missionDao;
    private LiveData<List<UserMission>> allMissions;
    private LiveData<List<UserMission>> todayMissionsByOperateDay = new LiveData<List<UserMission>>(){};
    private LiveData<List<UserMission>> todayMissionsByRepeatType = new LiveData<List<UserMission>>(){};
    private LiveData<List<UserMission>> todayMissionsByRepeatRange = new LiveData<List<UserMission>>(){};
    private LiveData<List<UserMission>> comingMissionsByOperateDay = new LiveData<List<UserMission>>(){};
    private LiveData<List<UserMission>> comingMissionsByRepeatType = new LiveData<List<UserMission>>(){};
    private LiveData<List<UserMission>> comingMissionsByRepeatRange = new LiveData<List<UserMission>>(){};
    private LiveData<UserMission> missionById = new LiveData<UserMission>(){};
    private LiveData<Long> missionRepeatStart = new LiveData<Long>() {};
    private LiveData<Long> missionRepeatEnd = new LiveData<Long>() {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>() {};
    private LiveData<List<UserMission>> finishedMissions = new LiveData<List<UserMission>>() {};
    private LiveData<List<UserMission>> unfinishedMissions = new LiveData<List<UserMission>>() {};

    public RoomApiServiceImpl(Application application){
        LogUtil.logD(TAG,"[ApiServiceImpl] constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        missionDao = missionDBManager.getMissionDao();
        allMissions = missionDao.getAllMissions();
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end) {
        todayMissionsByOperateDay = missionDao.getTodayMissionsByOperateDay(start,end);
        return todayMissionsByOperateDay;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
        todayMissionsByRepeatType = missionDao.getTodayMissionsByRepeatType(start);
        return todayMissionsByRepeatType;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end) {
        todayMissionsByRepeatRange = missionDao.getTodayMissionsByRepeatRange(start);
        return todayMissionsByRepeatRange;
    }


    @Override
    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today) {
        comingMissionsByOperateDay = missionDao.getComingMissionsByOperateDay(today);
        return comingMissionsByOperateDay;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today) {
        comingMissionsByRepeatType = missionDao.getComingMissionsByRepeatType();
        return comingMissionsByRepeatType;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
        comingMissionsByRepeatRange = missionDao.getComingMissionsByRepeatRange(today);
        return comingMissionsByRepeatRange;
    }

    @Override
    public LiveData<UserMission> getMissionById(String id) {
        missionById = missionDao.getMissionById(Integer.valueOf(id));
        return missionById;
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {

    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        Log.d("[ApiServiceImpl]","[getMissions]");
        return allMissions;
    }

    @Override
    public void addMission(UserMission userMission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.insert(userMission);
            }
        });
    }

    public void updateMission(UserMission userMission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.update(userMission);
            }
        });
    }

    public void updateNumberOfCompletionById(int id, int num){
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.updateNumberOfCompletionsById(id,num);
            }
        });
    }

    public void updateIsFinishedById(int id, boolean finished){
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.updateIsFinishedById(id,finished);
            }
        });
    }

    @Override
        public LiveData<Long> getMissionRepeatStart(String id) {
        missionRepeatStart = missionDao.getMissionRepeatStart(Integer.valueOf(id));
        return missionRepeatStart;
    }

    @Override
    public LiveData<Long> getMissionRepeatEnd(String id) {
        missionRepeatEnd = missionDao.getMissionRepeatEnd(Integer.valueOf(id));
        return missionRepeatEnd;
    }

    @Override
    public LiveData<Long> getMissionOperateDay(String id) {
        missionOperateDay = missionDao.getMissionOperateDay(Integer.valueOf(id));
        return missionOperateDay;
    }

    @Override
    public LiveData<List<UserMission>> getFinishedMissions() {
        finishedMissions = missionDao.getFinishedMissions();
        return finishedMissions;
    }

    @Override
    public LiveData<List<UserMission>> getUnFinishedMissions() {
        unfinishedMissions = missionDao.getUnfinishedMissions();
        return unfinishedMissions;
    }

    public void deleteMission(UserMission userMission){
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.delete(userMission);
            }
        });
    }

    @Override
    public UserMission getInitMission() {
        return new UserMission();
    }

    @Override
    public UserMission getQuickMission(int time,int shortBreakTime,int color) {
        return new UserMission(time,shortBreakTime, color);
    }
}
