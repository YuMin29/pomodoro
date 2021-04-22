package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionSettings;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;
import com.yumin.pomodoro.ui.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = "[TimerViewModel]";
    private DataRepository dataRepository;
    private String missionStrId;
    private MediatorLiveData<UserMission> mMission = new MediatorLiveData<>();
    private MediatorLiveData<Integer> mMissionNumberOfCompletion = new MediatorLiveData<>();
    private MediatorLiveData<MissionState> mMissionState = new MediatorLiveData<>();

    private MissionSettings mMissionSettings;
    private LiveData<Boolean> mAutoStartNextMission;
    private LiveData<Boolean> mAutoStartBreak;
    private LiveData<Integer> mMissionBackgroundRingtone;
    private LiveData<Integer> mMissionFinishedRingtone;
    private LiveData<Boolean> mDisableBreak;

    public TimerViewModel(@NonNull Application application) {
        super(application);

//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        this.missionStrId = MissionManager.getInstance().getStrOperateId();

        mMissionSettings = new MissionSettings(application);

        fetchMission();
    }

    private void fetchMission(){
        if (missionStrId.equals("quick_mission")) {
            mMission.setValue(dataRepository.getQuickMission());
            mMissionNumberOfCompletion.setValue(-1);
        } else {
            mMission.addSource(dataRepository.getMissionById(missionStrId), new Observer<UserMission>() {
                @Override
                public void onChanged(UserMission userMission) {
                    mMission.setValue(userMission);
                }
            });

            mMissionNumberOfCompletion.addSource(dataRepository.getNumberOfCompletionById(missionStrId, TimeToMillisecondUtil.getTodayStartTime()), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    LogUtil.logE(TAG,"[fetchMission] mNumberOfCompletion = "+integer);
                    mMissionNumberOfCompletion.setValue(integer == null ? 0 : integer);
                }
            });

            mMissionState.addSource(dataRepository.getMissionStateById(missionStrId, TimeToMillisecondUtil.getTodayStartTime()), new Observer<MissionState>() {
                @Override
                public void onChanged(MissionState missionState) {
                    LogUtil.logE(TAG,"[fetchMission] missionState = "+missionState);
                    mMissionState.setValue(missionState);
                }
            });
        }

        mAutoStartNextMission = mMissionSettings.getAutoStartNextMission();
        mAutoStartBreak = mMissionSettings.getAutoStartBreak();
        mMissionBackgroundRingtone = mMissionSettings.getIndexOfBackgroundRingtone();
        mMissionFinishedRingtone = mMissionSettings.getIndexOfFinishedRingtone();
        mDisableBreak = mMissionSettings.getDisableBreak();
    }

    public LiveData<UserMission> getMission(){
        return this.mMission;
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

    public LiveData<Integer> getIndexOfMissionBackgroundRingtone(){
        return mMissionBackgroundRingtone;
    }

    public LiveData<Integer> getIndexOfFinishedMissionRingtone(){
        return mMissionFinishedRingtone;
    }

    public void initMissionState(){
        LogUtil.logE(TAG,"[initMissionState] INIT MISSION STATE");
        if (!missionStrId.equals("quick_mission"))
            dataRepository.initMissionState(missionStrId);
    }

    public LiveData<Boolean> getAutoStartNextMission(){
        return this.mAutoStartNextMission;
    }

    public LiveData<Boolean> getAutoStartBreak(){
        return this.mAutoStartBreak;
    }

    public LiveData<Boolean> getDisableBreak(){
        return this.mDisableBreak;
    }
}
