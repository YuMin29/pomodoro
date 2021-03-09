package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;
import com.yumin.pomodoro.utils.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private DataRepository dataRepository;
    private String missionStrId;
    private MediatorLiveData<UserMission> mMission = new MediatorLiveData<>();
    private MutableLiveData<String> mMissionTime = new MutableLiveData<>();
    private MutableLiveData<String> mMissionBreakTime = new MutableLiveData<>();
    private MediatorLiveData<Integer> mMissionNumberOfCompletion = new MediatorLiveData<>();
    private MediatorLiveData<MissionState> mMissionState = new MediatorLiveData<>();

    public TimerViewModel(@NonNull Application application) {
        super(application);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        this.missionStrId = MissionManager.getInstance().getStrOperateId();
        fetchMission();
    }

    private void fetchMission(){
        if (missionStrId.equals("quick_mission")) {
            mMission.setValue(dataRepository.getQuickMission());
            mMissionNumberOfCompletion.setValue(-1);
        } else {
            LiveData<UserMission> sourceMission = dataRepository.getMissionById(missionStrId);
            mMission.addSource(sourceMission, new Observer<UserMission>() {
                @Override
                public void onChanged(UserMission userMission) {
                    mMission.setValue(userMission);
                }
            });

            LiveData<Integer> sourceMissionNumberOfCompletion  = dataRepository.getNumberOfCompletionById(missionStrId,TimeMilli.getTodayStartTime());
            mMissionNumberOfCompletion.addSource(sourceMissionNumberOfCompletion, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    LogUtil.logE(TAG,"[fetchMission] mNumberOfCompletion = "+integer);
                    mMissionNumberOfCompletion.setValue(integer == null ? 0 : integer);
                }
            });

            LiveData<MissionState> sourceMissionState = dataRepository.getMissionStateById(missionStrId,TimeMilli.getTodayStartTime());
            mMissionState.addSource(sourceMissionState, new Observer<MissionState>() {
                @Override
                public void onChanged(MissionState missionState) {
                    LogUtil.logE(TAG,"[fetchMission] missionState = "+missionState);
                    mMissionState.setValue(missionState);
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

    public void updateMissionNumberOfCompletion(int num){
        if (null == mMissionState.getValue()) {
            LogUtil.logE(TAG,"[updateNumberOfCompletionById] INIT MISSION STATE");
            dataRepository.initMissionState(missionStrId);
        }
        LogUtil.logE(TAG,"[updateNumberOfCompletionById] num = "+num);
        dataRepository.updateMissionNumberOfCompletion(missionStrId,num);
    }

    public void updateMissionFinishedState(boolean finished, int completeOfNumber){
        dataRepository.updateMissionFinishedState(missionStrId,finished,completeOfNumber);
    }

    public LiveData<Integer> getMissionNumberOfCompletion(){
        return mMissionNumberOfCompletion;
    }

    public LiveData<MissionState> getMissionState(){
        return this.mMissionState;
    }
}
