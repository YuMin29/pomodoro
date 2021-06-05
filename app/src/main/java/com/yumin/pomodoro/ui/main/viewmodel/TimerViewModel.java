package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.MissionSettings;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;
import com.yumin.pomodoro.ui.base.MissionManager;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = TimerViewModel.class.getSimpleName();
    private DataRepository mDataRepository;
    private String mMissionStrId;
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
//            mDataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        mMissionStrId = MissionManager.getInstance().getStrOperateId();
        mMissionSettings = new MissionSettings(application);
        fetchMission();
    }

    private void fetchMission(){
        if (mMissionStrId.equals("quick_mission")) {
            mMission.setValue(mDataRepository.getQuickMission());
            mMissionNumberOfCompletion.setValue(-1);
        } else {
            mMission.addSource(mDataRepository.getMissionById(mMissionStrId), new Observer<UserMission>() {
                @Override
                public void onChanged(UserMission userMission) {
                    mMission.setValue(userMission);
                }
            });

            mMissionNumberOfCompletion.addSource(mDataRepository.getNumberOfCompletionById(mMissionStrId, TimeToMillisecondUtil.getTodayStartTime()), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    LogUtil.logE(TAG,"[fetchMission] mNumberOfCompletion = "+integer);
                    mMissionNumberOfCompletion.setValue(integer == null ? 0 : integer);
                }
            });

            mMissionState.addSource(mDataRepository.getMissionStateById(mMissionStrId, TimeToMillisecondUtil.getTodayStartTime()), new Observer<MissionState>() {
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
        return mMission;
    }

    public void updateMissionNumberOfCompletion(int num){
        if (mMissionState.getValue() == null) {
            LogUtil.logE(TAG,"[updateNumberOfCompletionById] init");
            mDataRepository.initMissionState(mMissionStrId);
        }
        LogUtil.logE(TAG,"[updateNumberOfCompletionById] num = "+num);
        mDataRepository.updateMissionNumberOfCompletion(mMissionStrId,num);
    }

    public void updateMissionState(boolean finished, int completeOfNumber){
        mDataRepository.updateMissionFinishedState(mMissionStrId,finished,completeOfNumber);
    }

    public LiveData<Integer> getMissionNumberOfCompletion(){
        return mMissionNumberOfCompletion;
    }

    public LiveData<MissionState> getMissionState(){
        return mMissionState;
    }

    public LiveData<Integer> getIndexOfMissionBackgroundRingtone(){
        return mMissionBackgroundRingtone;
    }

    public LiveData<Integer> getIndexOfFinishedMissionRingtone(){
        return mMissionFinishedRingtone;
    }

    public void initMissionState(){
        LogUtil.logE(TAG,"[initMissionState] init mission");
        if (!mMissionStrId.equals("quick_mission"))
            mDataRepository.initMissionState(mMissionStrId);
    }

    public LiveData<Boolean> getAutoStartNextMission(){
        return mAutoStartNextMission;
    }

    public LiveData<Boolean> getAutoStartBreak(){
        return mAutoStartBreak;
    }

    public LiveData<Boolean> getDisableBreak(){
        return mDisableBreak;
    }
}
