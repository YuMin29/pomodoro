package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private RoomRepository roomRepository;
    private int missionId;
    private String missionStrId;
    private MediatorLiveData<UserMission> mMission = new MediatorLiveData<>();
    private MutableLiveData<String> mMissionTime = new MutableLiveData<>();
    private MutableLiveData<String> mMissionBreakTime = new MutableLiveData<>();

    public TimerViewModel(@NonNull Application application, RoomRepository roomRepository) {
        super(application);
        this.roomRepository = roomRepository;
//        this.missionId = MissionManager.getInstance().getOperateId();
        this.missionStrId = MissionManager.getInstance().getOperateStrId();
        fetchMission();
    }

    private void fetchMission(){
//        if (missionId == -1) {
//            mMission.setValue(roomRepository.getQuickMission());
//        } else {
//            LiveData<Mission> fetchMission = roomRepository.getMissionById(missionId);
//            mMission.addSource(fetchMission, new Observer<Mission>() {
//                @Override
//                public void onChanged(Mission getmission) {
//                    mMission.setValue(getmission);
//                }
//            });
//        }

        if (missionStrId.equals("quick_mission")) {
            mMission.setValue(new FirebaseApiServiceImpl().getQuickMission(25,5,
                    Color.parseColor("#e57373")));
        } else {
            LiveData<UserMission> fetchMission = new FirebaseApiServiceImpl().getMissionById(missionStrId);
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
        roomRepository.updateNumberOfCompletionById(missionId,num);
    }

    public void updateIsFinishedById(boolean finished){
        roomRepository.updateIsFinishedById(missionId,finished);
    }
}
