package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private MainRepository mainRepository;
    private int itemId;
    private LiveData<Mission> mission;
    private MutableLiveData<String> missionTime = new MutableLiveData<>();
    private MutableLiveData<String> missionBreakTime = new MutableLiveData<>();

    public TimerViewModel(@NonNull Application application, MainRepository mainRepository, int itemId) {
        super(application);
        this.mainRepository = mainRepository;
        this.itemId = itemId;
        fetchMission();
    }

    private void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] ");
        mission = mainRepository.getMissionById(itemId);
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
        mainRepository.updateNumberOfCompletionById(itemId,num);
    }

    public void updateIsFinishedById(boolean finished){
        mainRepository.updateIsFinishedById(itemId,finished);
    }
}
