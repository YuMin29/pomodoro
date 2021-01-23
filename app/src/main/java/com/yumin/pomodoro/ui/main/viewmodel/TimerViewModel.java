package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private MainRepository mainRepository;
    private int missionId;
    private MediatorLiveData<Mission> mMission = new MediatorLiveData<>();
    private MutableLiveData<String> mMissionTime = new MutableLiveData<>();
    private MutableLiveData<String> mMissionBreakTime = new MutableLiveData<>();

    public TimerViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mainRepository = mainRepository;
        this.missionId = MissionManager.getInstance().getOperateId();
        fetchMission();
    }

    private void fetchMission(){
        if (missionId == -1) {
            mMission.setValue(mainRepository.getQuickMission());
        } else {
            LiveData<Mission> fetchMission = mainRepository.getMissionById(missionId);
            mMission.addSource(fetchMission, new Observer<Mission>() {
                @Override
                public void onChanged(Mission getmission) {
                    mMission.setValue(getmission);
                }
            });
        }
    }

    public LiveData<Mission> getMission(){
        return this.mMission;
    }

    public LiveData<String> getMissionTime(){
        return this.mMissionTime;
    }

    public void setMissionTime(String mMissionTime){
        this.mMissionTime.postValue(mMissionTime);
    }

    public LiveData<String> getMissionBreakTime(){
        return this.mMissionBreakTime;
    }

    public void setMissionBreakTime(String breakTime){
        this.mMissionBreakTime.postValue(breakTime);
    }

    public void updateNumberOfCompletionById(int num){
        mainRepository.updateNumberOfCompletionById(missionId,num);
    }

    public void updateIsFinishedById(boolean finished){
        mainRepository.updateIsFinishedById(missionId,finished);
    }
}
