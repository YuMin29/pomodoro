package com.yumin.pomodoro.data.api;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.room.MissionDBManager;
import com.yumin.pomodoro.data.repository.room.MissionDao;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class ApiServiceImpl implements ApiService {
    private static final String TAG = "[ApiServiceImpl]";
    private List<Mission> missions;
    private MissionDao missionDao;
    private LiveData<List<Mission>> allMissions;
    private LiveData<List<Mission>> todayMissions = new LiveData<List<Mission>>(){};
    private LiveData<List<Mission>> comingMissions = new LiveData<List<Mission>>(){};
    private LiveData<Mission> missionById = new LiveData<Mission>(){};

    public ApiServiceImpl(Application application){
        LogUtil.logD(TAG,"[ApiServiceImpl] constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        missionDao = missionDBManager.getMissionDao();
        allMissions = missionDao.getAllMissions();
    }

    @Override
    public LiveData<List<Mission>> getTodayMissions(long start, long end) {
        todayMissions = missionDao.getTodayMissions(start,end);
        return todayMissions;
    }


    @Override
    public LiveData<List<Mission>> getComingMissions(long today) {
        comingMissions = missionDao.getComingMissions(today);
        return comingMissions;
    }

    @Override
    public LiveData<Mission> getMissionById(int id) {
        missionById = missionDao.getMissionById(id);
        return missionById;
    }

    @Override
    public LiveData<List<Mission>> getMissions(Context context) {
        Log.d("[ApiServiceImpl]","[getMissions]");
        return allMissions;
    }

    @Override
    public void addMission(Context context, Mission mission) {
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
}
