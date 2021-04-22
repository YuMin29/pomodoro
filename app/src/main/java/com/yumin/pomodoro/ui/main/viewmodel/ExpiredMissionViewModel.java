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
    private final String TAG = "[ExpiredMissionViewModel]";
    private DataRepository dataRepository;
    private LiveData<List<UserMission>> missions;
    private MutableLiveData<List<UserMission>> pastNoneRepeatMissions = new MutableLiveData<>();
    private MutableLiveData<List<UserMission>> pastRepeatEveryMissions = new MutableLiveData<>();
    private MutableLiveData<List<UserMission>> pastRepeatDefineMissions = new MutableLiveData<>();
    private LiveData<List<UserMission>> pastFinishedMissions;
    private LiveData<List<MissionState>> missionStates;

    public ExpiredMissionViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new RoomRepository(new RoomApiServiceImpl(application));
        fetchData();
    }

    private void fetchData(){
        missions = dataRepository.getMissions();
        pastFinishedMissions = dataRepository.getPastFinishedMission(TimeToMillisecondUtil.getTodayStartTime());
        missionStates = dataRepository.getMissionStateList();
    }

    public LiveData<List<UserMission>> getMissions(){
        return this.missions;
    }

    public void fetchPastMissions(){
        getPastNoneRepeatMissions();
        getPastRepeatEverydayMissions();
        getPastRepeatDefineMissions();
    }

    public LiveData<List<UserMission>> getPastFinishedMission(){
        return this.pastFinishedMissions;
    }

    public LiveData<List<MissionState>> getMissionStates(){
        return this.missionStates;
    }

    public MediatorLiveData<Result> getPastMissions() {
        // observe today missions
        MediatorLiveData<Result> pastMissions = new MediatorLiveData<Result>();
        final Result current = new Result();
        pastMissions.addSource(pastNoneRepeatMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByOperateDay [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByOperateDay(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(pastRepeatEveryMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatType [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByRepeatType(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(pastRepeatDefineMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatRange [onChanged] SIZE = "+userMissions.size());
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
        if (missions.getValue() == null) {
            pastNoneRepeatMissions.setValue(new ArrayList<>());
        } else {
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : missions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_NONE &&
                        userMission.getOperateDay() < TimeToMillisecondUtil.getTodayStartTime()){
                    missionList.add(userMission);
                }
            }
            pastNoneRepeatMissions.setValue(missionList);
        }
        return pastNoneRepeatMissions;
    }

    /**
     * 每日重複： everyday && 執行日為今天或今天以前
     * @return
     */
    private LiveData<List<UserMission>> getPastRepeatEverydayMissions(){
        if (missions.getValue() == null) {
            pastRepeatEveryMissions.setValue(new ArrayList<>());
        } else {
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : missions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_EVERYDAY &&
                        userMission.getOperateDay() < TimeToMillisecondUtil.getTodayStartTime())
                    missionList.add(userMission);
            }
            pastRepeatEveryMissions.setValue(missionList);
        }
        return pastRepeatEveryMissions ;
    }

    /**
     * 特定範圍重複： 判斷今天有無在範圍區間內  repeat start <---- today start
     * @return
     */
    private LiveData<List<UserMission>> getPastRepeatDefineMissions(){
        if (missions.getValue() == null) {
            pastRepeatDefineMissions.setValue(new ArrayList<>());
        } else {
            LogUtil.logE(TAG,"TimeMilli.getTodayStartTime() = "+ TimeToMillisecondUtil.getTodayStartTime()+
                    " , TimeMilli.getTodayEndTime() = "+ TimeToMillisecondUtil.getTodayEndTime());
            List<UserMission> missionList = new ArrayList<>();
            for (UserMission userMission : missions.getValue()) {
                if (userMission.getRepeat() == UserMission.TYPE_DEFINE &&
                        userMission.getRepeatStart()  <= TimeToMillisecondUtil.getTodayStartTime()) {
                    missionList.add(userMission);
                }
                LogUtil.logE(TAG,"userMission.getRepeatStart() = "+userMission.getRepeatStart()+
                        " ,  userMission.getRepeatEnd() = "+  userMission.getRepeatEnd());
            }

            pastRepeatDefineMissions.setValue(missionList);
        }
        return pastRepeatDefineMissions;
    }

    public class Result {
        public List<UserMission> missionsByOperateDay = new ArrayList<>();
        public List<UserMission> missionsByRepeatType = new ArrayList<>();
        public List<UserMission> missionsByRepeatRange = new ArrayList<>();

        public Result() {}

        public void setMissionsByOperateDay(List<UserMission> missions){
            this.missionsByOperateDay = missions;
        }

        public void setMissionsByRepeatType(List<UserMission> missions){
            this.missionsByRepeatType = missions;
        }

        public void setMissionsByRepeatRange(List<UserMission> missions){
            this.missionsByRepeatRange = missions;
        }

        public boolean isComplete() {
            return (missionsByOperateDay != null && missionsByRepeatType != null && missionsByRepeatRange != null);
        }
    }
}
