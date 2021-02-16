package com.yumin.pomodoro.data.repository.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.api.RoomApiService;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class RoomApiServiceImpl implements RoomApiService<Mission> {
    private static final String TAG = "[ApiServiceImpl]";
    private List<Mission> missions;
    private MissionDao missionDao;
    private LiveData<List<Mission>> allMissions;
    private LiveData<List<Mission>> todayMissionsByOperateDay = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> todayMissionsByRepeatType = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> todayMissionsByRepeatRange = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> comingMissionsByOperateDay = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> comingMissionsByRepeatType = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> comingMissionsByRepeatRange = new LiveData<List<Mission>>(){};
    private LiveData<Mission> missionById = new LiveData<Mission>(){};
    private LiveData<Long> missionRepeatStart = new LiveData<Long>() {};
    private LiveData<Long> missionRepeatEnd = new LiveData<Long>() {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>() {};
    private LiveData<List<Mission>> finishedMissions = new LiveData<List<Mission>>() {};
    private LiveData<List<Mission>> unfinishedMissions = new LiveData<List<Mission>>() {};

    public RoomApiServiceImpl(Application application){
        LogUtil.logD(TAG,"[ApiServiceImpl] constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        missionDao = missionDBManager.getMissionDao();
        allMissions = missionDao.getAllMissions();
    }

    @Override
    public LiveData<List<Mission>> getTodayMissionsByOperateDay(long start, long end) {
        todayMissionsByOperateDay = missionDao.getTodayMissionsByOperateDay(start,end);
        return todayMissionsByOperateDay;
    }

    @Override
    public LiveData<List<Mission>> getTodayMissionsByRepeatType(long start, long end) {
        todayMissionsByRepeatType = missionDao.getTodayMissionsByRepeatType(start);
        return todayMissionsByRepeatType;
    }

    @Override
    public LiveData<List<Mission>> getTodayMissionsByRepeatRange(long start, long end) {
        todayMissionsByRepeatRange = missionDao.getTodayMissionsByRepeatRange(start);
        return todayMissionsByRepeatRange;
    }


    @Override
    public LiveData<List<Mission>> getComingMissionsByOperateDay(long today) {
        comingMissionsByOperateDay = missionDao.getComingMissionsByOperateDay(today);
        return comingMissionsByOperateDay;
    }

    @Override
    public LiveData<List<Mission>> getComingMissionsByRepeatType(long today) {
        comingMissionsByRepeatType = missionDao.getComingMissionsByRepeatType();
        return comingMissionsByRepeatType;
    }

    @Override
    public LiveData<List<Mission>> getComingMissionsByRepeatRange(long today) {
        comingMissionsByRepeatRange = missionDao.getComingMissionsByRepeatRange(today);
        return comingMissionsByRepeatRange;
    }

    @Override
    public LiveData<Mission> getMissionById(int id) {
        missionById = missionDao.getMissionById(id);
        return missionById;
    }

    @Override
    public LiveData<List<Mission>> getMissions() {
        Log.d("[ApiServiceImpl]","[getMissions]");
        return allMissions;
    }

    @Override
    public void addMission(Mission mission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.insert(mission);
            }
        });
    }

    public void updateMission(Mission mission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.update(mission);
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
    public LiveData<Long> getMissionRepeatStart(int id) {
        missionRepeatStart = missionDao.getMissionRepeatStart(id);
        return missionRepeatStart;
    }

    @Override
    public LiveData<Long> getMissionRepeatEnd(int id) {
        missionRepeatEnd = missionDao.getMissionRepeatEnd(id);
        return missionRepeatEnd;
    }

    @Override
    public LiveData<Long> getMissionOperateDay(int id) {
        missionOperateDay = missionDao.getMissionOperateDay(id);
        return missionOperateDay;
    }

    @Override
    public LiveData<List<Mission>> getFinishedMissions() {
        finishedMissions = missionDao.getFinishedMissions();
        return finishedMissions;
    }

    @Override
    public LiveData<List<Mission>> getUnFinishedMissions() {
        unfinishedMissions = missionDao.getUnfinishedMissions();
        return unfinishedMissions;
    }

    public void deleteMission(Mission mission){
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionDao.delete(mission);
            }
        });
    }

    @Override
    public Mission getInitMission() {
        return new Mission();
    }

    @Override
    public Mission getQuickMission(int time,int shortBreakTime,int color) {
        return new Mission(time,shortBreakTime, color);
    }
}
