package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpiredMissionViewModel extends AndroidViewModel {
    private final String TAG = ExpiredMissionViewModel.class.getSimpleName();
    private DataRepository mDataRepository;
    private LiveData<List<UserMission>> mMissions;
    private MutableLiveData<List<UserMission>> mPastNoneRepeatMissions = new MutableLiveData<>();
    private MutableLiveData<List<UserMission>> mPastRepeatEveryMissions = new MutableLiveData<>();
    private MutableLiveData<List<UserMission>> mPastRepeatDefineMissions = new MutableLiveData<>();
    private LiveData<List<UserMission>> mPastFinishedMissions;
    private LiveData<List<MissionState>> mMissionStates;

    public ExpiredMissionViewModel(@NonNull Application application) {
        super(application);
        mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));
        fetchData();
    }

    private void fetchData(){
        mMissions = mDataRepository.getMissions();
        mPastFinishedMissions = mDataRepository.getPastCompletedMission(TimeToMillisecondUtil.getTodayStartTime());
        mMissionStates = mDataRepository.getMissionStateList();
    }

    public LiveData<List<UserMission>> getMissions(){
        return mMissions;
    }

    public void fetchPastMissions(){
        getPastNoneRepeatMissions();
        getPastRepeatEverydayMissions();
        getPastRepeatDefineMissions();
    }

    public LiveData<List<UserMission>> getPastFinishedMission(){
        return mPastFinishedMissions;
    }

    public LiveData<List<MissionState>> getMissionStates(){
        return mMissionStates;
    }

    public MediatorLiveData<Result> getPastMissions() {
        // observe today missions
        MediatorLiveData<Result> pastMissions = new MediatorLiveData<Result>();
        final Result current = new Result();
        pastMissions.addSource(mPastNoneRepeatMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"[getTodayMissionsByOperateDay] size = "+userMissions.size());
                current.setMissionsByOperateDay(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(mPastRepeatEveryMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"[getTodayMissionsByRepeatType] size = "+userMissions.size());
                current.setMissionsByRepeatType(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(mPastRepeatDefineMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"[getTodayMissionsByRepeatRange] size = "+userMissions.size());
                current.setMissionsByRepeatRange(userMissions);
                pastMissions.setValue(current);
            }
        });
        return pastMissions;
    }

    /**
     * 不重複 且 執行日為今天以前 operate day <---- [today start]
     * @return
     */
    private LiveData<List<UserMission>> getPastNoneRepeatMissions(){
        if (mMissions.getValue() == null) {
            mPastNoneRepeatMissions.setValue(new ArrayList<>());
        } else {
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : mMissions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_NONE &&
                        userMission.getOperateDay() < TimeToMillisecondUtil.getTodayStartTime()){
                    missionList.add(userMission);
                }
            }
            mPastNoneRepeatMissions.setValue(missionList);
        }
        return mPastNoneRepeatMissions;
    }

    /**
     * 每日重複： everyday && 執行日為今天或今天以前
     * @return
     */
    private LiveData<List<UserMission>> getPastRepeatEverydayMissions(){
        if (mMissions.getValue() == null) {
            mPastRepeatEveryMissions.setValue(new ArrayList<>());
        } else {
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : mMissions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_EVERYDAY &&
                        userMission.getOperateDay() < TimeToMillisecondUtil.getTodayStartTime())
                    missionList.add(userMission);
            }
            mPastRepeatEveryMissions.setValue(missionList);
        }
        return mPastRepeatEveryMissions;
    }

    /**
     * 特定範圍重複： 判斷今天有無在範圍區間內  repeat start <---- today start
     * @return
     */
    private LiveData<List<UserMission>> getPastRepeatDefineMissions(){
        if (mMissions.getValue() == null) {
            mPastRepeatDefineMissions.setValue(new ArrayList<>());
        } else {
            LogUtil.logE(TAG,"TimeMilli.getTodayStartTime() = "+ TimeToMillisecondUtil.getTodayStartTime()+
                    " , TimeMilli.getTodayEndTime() = "+ TimeToMillisecondUtil.getTodayEndTime());
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : mMissions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_DEFINE &&
                        userMission.getRepeatStart()  <= TimeToMillisecondUtil.getTodayStartTime()) {
                    missionList.add(userMission);
                }
                LogUtil.logE(TAG,"userMission.getRepeatStart() = "+userMission.getRepeatStart()+
                        " ,  userMission.getRepeatEnd() = "+  userMission.getRepeatEnd());
            }

            mPastRepeatDefineMissions.setValue(missionList);
        }
        return mPastRepeatDefineMissions;
    }

    public class Result {
        public List<UserMission> missionsByOperateDay = new ArrayList<>();
        public List<UserMission> missionsByRepeatType = new ArrayList<>();
        public List<UserMission> missionsByRepeatRange = new ArrayList<>();

        public Result() {}

        public void setMissionsByOperateDay(List<UserMission> missions){
            missionsByOperateDay = missions;
        }

        public void setMissionsByRepeatType(List<UserMission> missions){
            missionsByRepeatType = missions;
        }

        public void setMissionsByRepeatRange(List<UserMission> missions){
            missionsByRepeatRange = missions;
        }

        public boolean isComplete() {
            return (missionsByOperateDay != null && missionsByRepeatType != null && missionsByRepeatRange != null);
        }
    }
}
