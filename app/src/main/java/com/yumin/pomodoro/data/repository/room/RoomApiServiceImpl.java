package com.yumin.pomodoro.data.repository.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.util.Date;
import java.util.List;

public class RoomApiServiceImpl implements ApiService<UserMission,MissionState> {
    private static final String TAG = RoomApiServiceImpl.class.getSimpleName();
    private MissionDao mMissionDao;
    private MissionStateDao mMissionStateDao;
    private LiveData<List<UserMission>> mAllMissions;
    private LiveData<UserMission> mMissionById = new LiveData<UserMission>(){};
    private LiveData<Long> mMissionRepeatStart = new LiveData<Long>() {};
    private LiveData<Long> mMissionRepeatEnd = new LiveData<Long>() {};
    private LiveData<Long> mMissionOperateDay = new LiveData<Long>() {};
    private LiveData<List<MissionState>> mAllMissionState;

    public RoomApiServiceImpl(Application application){
        LogUtil.logD(TAG,"constructor");
        MissionDBManager missionDBManager = MissionDBManager.getInstance(application);
        mMissionDao = missionDBManager.getMissionDao();
        mMissionStateDao = missionDBManager.getMissionStateDao();
        mAllMissions = mMissionDao.getAllMissions();
        mAllMissionState = mMissionStateDao.getAllMissionStates();
    }

    @Override
    public LiveData<UserMission> getMissionById(String id) {
        mMissionById = mMissionDao.getMissionById(Integer.valueOf(id));
        return mMissionById;
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionStateDao.updateNumberOfCompletionsById(Integer.valueOf(id),num, TimeToMillisecondUtil.getTodayInitTime());
            }
        });
    }

    @Override
    public void updateMissionState(String id, boolean isCompleted, int completeOfNumber) {
        LogUtil.logE(TAG,"[updateMissionState] ID = " + id + ", finished = "+isCompleted);
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionStateDao.updateIsFinishedById(Integer.valueOf(id), isCompleted);
                mMissionStateDao.updateFinishedDayById(Integer.valueOf(id), isCompleted ? new Date().getTime() : -1);
            }
        });
    }

    private void saveMissionState(String missionId, int completeOfNumber, boolean isFinish){
        LogUtil.logE(TAG,"[saveMissionState] id = "+missionId +" ,completeOfNumber = "+completeOfNumber
                +" ,isFinish = "+isFinish);

        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MissionState missionState = new MissionState();
                mMissionStateDao.insert(missionState);
            }
        });
    }

    @Override
    public void initMissionState(String missionId){
        long todayMilli = TimeToMillisecondUtil.getTodayInitTime();
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.logE(TAG,"[initMissionState]]");
                MissionState missionState = new MissionState(0,false,
                        todayMilli,-1,missionId);
                mMissionStateDao.insert(missionState);
            }
        });
    }

    @Override
    public void saveMissionState(String missionId,MissionState missionState) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionStateDao.insert(missionState);
            }
        });
    }

    @Override
    public LiveData<List<MissionState>> getMissionStateList() {
        return mAllMissionState;
    }

    @Override
    public LiveData<List<UserMission>> getPastCompletedMission(long today) {
        return mMissionStateDao.getPastCompletedMissions(today);
    }


    @Override
    public LiveData<List<UserMission>> getMissions() {
        return mAllMissions;
    }

    @Override
    public String addMission(UserMission userMission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userMission.setCreatedTime(new Date().getTime());
                mMissionDao.insert(userMission);
            }
        });
        return String.valueOf(userMission.getId());
    }

    public void updateMission(UserMission userMission) {
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionDao.update(userMission);
            }
        });
    }

    @Override
        public LiveData<Long> getMissionRepeatStart(String id) {
        mMissionRepeatStart = mMissionDao.getMissionRepeatStart(Integer.valueOf(id));
        return mMissionRepeatStart;
    }

    @Override
    public LiveData<Long> getMissionRepeatEnd(String id) {
        mMissionRepeatEnd = mMissionDao.getMissionRepeatEnd(Integer.valueOf(id));
        return mMissionRepeatEnd;
    }

    @Override
    public LiveData<Long> getMissionOperateDay(String id) {
        mMissionOperateDay = mMissionDao.getMissionOperateDay(Integer.valueOf(id));
        return mMissionOperateDay;
    }

    @Override
    public LiveData<List<UserMission>> getCompletedMissionList(long start, long end) {
        return  mMissionStateDao.getTodayCompletedMissions(start);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return mMissionStateDao.getNumberOfCompletionById(Integer.valueOf(id),todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        return mMissionStateDao.getMissionStateById(Integer.valueOf(id),todayStart);
    }

    public void deleteMission(UserMission userMission){
        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionDao.delete(userMission);
            }
        });
    }

    @Override
    public void deleteAllMission() {
        mMissionDao.deleteAll();
    }

    @Override
    public UserMission getInitMission() {
        return new UserMission();
    }

    @Override
    public UserMission getQuickMission(int time,int shortBreakTime,int color) {
        return new UserMission(time,shortBreakTime,color);
    }

    public LiveData<List<UserMission>> getTodayNoneRepeatMissions(){
        return mMissionDao.getTodayNoneRepeatMissions(UserMission.TYPE_NONE,TimeToMillisecondUtil.getTodayStartTime(),
                TimeToMillisecondUtil.getTodayEndTime());
    }

    public LiveData<List<UserMission>> getTodayRepeatEverydayMissions(){
        return mMissionDao.getTodayRepeatEverydayMissions(UserMission.TYPE_EVERYDAY,TimeToMillisecondUtil.getTodayEndTime());
    }

    public LiveData<List<UserMission>> getTodayRepeatDefineMissions(){
        return mMissionDao.getTodayRepeatDefineMissions(UserMission.TYPE_DEFINE,TimeToMillisecondUtil.getTodayEndTime());
    }

    public LiveData<List<UserMission>> getComingNoneRepeatMissions(){
        return mMissionDao.getComingNoneRepeatMissions(UserMission.TYPE_NONE,TimeToMillisecondUtil.getTodayEndTime());
    }

    public LiveData<List<UserMission>> getComingRepeatEverydayMissions(){
        return mMissionDao.getComingRepeatEverydayMissions(UserMission.TYPE_EVERYDAY);
    }

    public LiveData<List<UserMission>> getComingRepeatDefineMissions(){
        return mMissionDao.getComingRepeatDefineMissions(UserMission.TYPE_DEFINE, TimeToMillisecondUtil.getTodayEndTime());
    }
}
