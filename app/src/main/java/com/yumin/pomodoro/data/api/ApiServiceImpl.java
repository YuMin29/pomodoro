package com.yumin.pomodoro.data.api;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
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
    private LiveData<List<Mission>> missionsByOperate = new LiveData<List<Mission>>(){};

    public ApiServiceImpl(Application application){
        LogUtil.logD(TAG,"[ApiServiceImpl] constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        missionDao = missionDBManager.getMissionDao();
        allMissions = missionDao.getAllMissions();
    }

    @Override
    public LiveData<List<Mission>> getMissionsByOperateDay(long start, long end) {
        missionsByOperate = missionDao.getMissionsByOperateDay(start,end);
        return missionsByOperate;
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

    @Override
    public Mission getInitMission() {
        return new Mission();
    }
}
