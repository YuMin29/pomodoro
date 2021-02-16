package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.utils.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
//    private RoomRepository roomRepository;
    private FirebaseRepository firebaseRepository;
    private int missionId;
    private String missionStrId;
    private MediatorLiveData<UserMission> mMission = new MediatorLiveData<>();
    private MutableLiveData<String> mMissionTime = new MutableLiveData<>();
    private MutableLiveData<String> mMissionBreakTime = new MutableLiveData<>();

    public TimerViewModel(@NonNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        this.firebaseRepository = firebaseRepository;
//        this.missionId = MissionManager.getInstance().getOperateId();
        this.missionStrId = MissionManager.getInstance().getStrOperateId();
        fetchMission();
    }

    private void fetchMission(){
        if (missionStrId.equals("quick_mission")) {
            mMission.setValue(firebaseRepository.getQuickMission());
        } else {
            LiveData<UserMission> fetchMission = firebaseRepository.getMissionById(missionStrId);
            mMission.addSource(fetchMission, new Observer<UserMission>() {
                @Override
                public void onChanged(UserMission getmission) {
                    mMission.setValue(getmission);
                }
            });
        }
    }

    public LiveData<UserMission> getMission(){
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
        firebaseRepository.updateNumberOfCompletionById(missionStrId,num);
    }

    public void updateIsFinishedById(boolean finished){
        firebaseRepository.updateIsFinishedById(missionStrId,finished);
    }
}
