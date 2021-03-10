package com.yumin.pomodoro.data.repository.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: 3/3/21 Use Firebase getCurrentUser to distinguish use Room or Firebase
public class RoomApiServiceImpl implements ApiService<UserMission,MissionState> {
    private static final String TAG = "[ApiServiceImpl]";
    private List<UserMission> missions;
    private MissionDao missionDao;
    private MissionStateDao missionStateDao;
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
    private MutableLiveData<List<Integer>> finishedMissions = new MutableLiveData<List<Integer>>() {};
    private LiveData<List<UserMission>> unfinishedMissions = new LiveData<List<UserMission>>() {};

    public RoomApiServiceImpl(Application application){
        LogUtil.logD(TAG,"[ApiServiceImpl] constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        missionDao = missionDBManager.getMissionDao();
        missionStateDao = missionDBManager.getMissionStateDao();
        allMissions = missionDao.getAllMissions();
    }



    @Override
    public LiveData<UserMission> getMissionById(String id) {
        missionById = missionDao.getMissionById(Integer.valueOf(id));
        return missionById;
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionStateDao.updateNumberOfCompletionsById(Integer.valueOf(id),num);
//                missionDao.updateNumberOfCompletionsById(Integer.valueOf(id),num);
            }
        });
    }

    @Override
    public void updateMissionFinishedState(String id, boolean isFinished, int completeOfNumber) {
        LogUtil.logE(TAG,"[updateIsFinishedById] ID = " + id + ", finished = "+isFinished);

        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                missionStateDao.updateIsFinishedById(Integer.valueOf(id), isFinished);
                missionStateDao.updateFinishedDayById(Integer.valueOf(id), isFinished ? new Date().getTime() : -1);
            }
        });
    }

    private void saveMissionState(String missionId, int completeOfNumber, boolean isFinish){
        LogUtil.logE(TAG,"[saveMissionState] id = "+missionId
                +" ,completeOfNumber = "+completeOfNumber
                +" ,isFinish = "+isFinish);
        long todayMilli = TimeMilli.getTodayInitTime();

        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.logE(TAG,"INSERT mission state");
                MissionState missionState = new MissionState(completeOfNumber,isFinish,
                        todayMilli,-1,missionId);
                missionStateDao.insert(missionState);
            }
        });
    }

    @Override
    public void initMissionState(String missionId){
        Log.d("[RoomApiServiceImpl]","[getInitMissionState]");
        long todayMilli = TimeMilli.getTodayInitTime();

        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.logE(TAG,"INSERT mission state");
                MissionState missionState = new MissionState(0,false,
                        todayMilli,-1,missionId);
                missionStateDao.insert(missionState);
            }
        });
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
                userMission.setCreatedTime(new Date().getTime());
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
    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end) {
        return  missionStateDao.getFinishedMissions(start);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return missionStateDao.getNumberOfCompletionById(Integer.valueOf(id),todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        return missionStateDao.getMissionStateById(Integer.valueOf(id),todayStart);
    }

//    @Override
//    public LiveData<List<UserMission>> getUnFinishedMissions(long start, long end) {
//        unfinishedMissions = missionDao.getUnfinishedMissions();
//        return unfinishedMissions;
//    }

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
        return new UserMission(time,shortBreakTime,color);
    }

//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end) {
//        todayMissionsByOperateDay = missionDao.getTodayMissionsByOperateDay(start,end);
//        return todayMissionsByOperateDay;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
//        todayMissionsByRepeatType = missionDao.getTodayMissionsByRepeatType(start);
//        return todayMissionsByRepeatType;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end) {
//        todayMissionsByRepeatRange = missionDao.getTodayMissionsByRepeatRange(start);
//        return todayMissionsByRepeatRange;
//    }
//
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today) {
//        comingMissionsByOperateDay = missionDao.getComingMissionsByOperateDay(today);
//        return comingMissionsByOperateDay;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today) {
//        comingMissionsByRepeatType = missionDao.getComingMissionsByRepeatType();
//        return comingMissionsByRepeatType;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
//        comingMissionsByRepeatRange = missionDao.getComingMissionsByRepeatRange(today);
//        return comingMissionsByRepeatRange;
//    }
}
