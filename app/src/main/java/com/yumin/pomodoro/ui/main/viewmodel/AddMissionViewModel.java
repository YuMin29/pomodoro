package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private Application mApplication;
    private MainRepository mainRepository;
    private MutableLiveData<List<AdjustMissionItem>> adjustMissionItems = new MutableLiveData<>();
    private MutableLiveData<Mission> mission = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mApplication = application;
        this.mainRepository = mainRepository;
        fetchMission();
    }

    private void fetchMission(){
        // init adjust item
        mission.setValue(mainRepository.getInitMission());
    }

    public LiveData<Mission> getMission(){
        return this.mission;
    }

    public void saveMission(){
//        mainRepository.addMission(mMission);
        LogUtil.logD(TAG,"[saveMission] mission val = "+mission.getValue().dump());
    }

//    public void setMissionName(String name){
//        mMission.setName(name);
//        mission.postValue(mMission);
//    }
//    public void setTime(int time){
//        LogUtil.logD(TAG,"[setTime] time = "+time);
//        mMission.setTime(time);
//        mission.postValue(mMission);
//    }
//
//    public void setLongBreak(int time){
//        LogUtil.logD(TAG,"[setLongBreak] time = "+time);
//        mMission.setLongBreakTime(time);
//        mission.postValue(mMission);
//    }
//
//    public void setShortBreak(int time){
//        mMission.setShortBreakTime(time);
//        mission.postValue(mMission);
//    }
//
//    public void setGoal(int goal){
//        mMission.setGoal(goal);
//        mission.postValue(mMission);
//    }
//
//    public void setRepeat(int repeat){
//        mMission.setRepeat(repeat);
//        mission.postValue(mMission);
//    }
//
//    public void setOperateDay(Mission.Operate operateDay) {
//        mMission.setOperateDay(operateDay);
//        mission.postValue(mMission);
//    }
//
//    public void setColor(Mission.Color color){
//        mMission.setColor(color);
//        mission.postValue(mMission);
//    }
//
//    public void setEnableNotification(boolean enabled){
//        mMission.setEnableNotification(enabled);
//        mission.postValue(mMission);
//    }
//
//    public void setEnableSound(boolean enabled){
//        mMission.setEnableSound(enabled);
//        mission.postValue(mMission);
//    }
//
//    public void setVolume(Mission.Volume volume){
//        mMission.setVolume(volume);
//        mission.postValue(mMission);
//    }
//
//    public void setEnableVibrate(boolean enabled){
//        mMission.setEnableVibrate(enabled);
//        mission.postValue(mMission);
//    }
//
//    public void setKeepScreenOn(boolean enabled){
//        mMission.setKeepScreenOn(enabled);
//        mission.postValue(mMission);
//    }
}
