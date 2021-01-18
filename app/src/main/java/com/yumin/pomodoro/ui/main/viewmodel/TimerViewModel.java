package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private MainRepository mainRepository;
    private int missionId;
    private MediatorLiveData<Mission> mission = new MediatorLiveData<>();
    private MutableLiveData<String> missionTime = new MutableLiveData<>();
    private MutableLiveData<String> missionBreakTime = new MutableLiveData<>();

    public TimerViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mainRepository = mainRepository;
        this.missionId = MissionManager.getInstance().getOperateId();
        fetchMission();
    }

    private void fetchMission(){
        if (missionId == -1) {
            mission.setValue(mainRepository.getQuickMission());
        } else {
            LiveData<Mission> fetchMission = mainRepository.getMissionById(missionId);
            mission.addSource(fetchMission, new Observer<Mission>() {
                @Override
                public void onChanged(Mission getmission) {
                    mission.setValue(getmission);
                }
            });
        }
    }

    public LiveData<Mission> getMission(){
        return this.mission;
    }

    public LiveData<String> getMissionTime(){
        return this.missionTime;
    }

    public void setMissionTime(String missionTime){
        this.missionTime.postValue(missionTime);
    }

    public LiveData<String> getMissionBreakTime(){
        return this.missionBreakTime;
    }

    public void setMissionBreakTime(String breakTime){
        this.missionBreakTime.postValue(breakTime);
    }

    public void updateNumberOfCompletionById(int num){
        mainRepository.updateNumberOfCompletionById(missionId,num);
    }

    public void updateIsFinishedById(boolean finished){
        mainRepository.updateIsFinishedById(missionId,finished);
    }
}
