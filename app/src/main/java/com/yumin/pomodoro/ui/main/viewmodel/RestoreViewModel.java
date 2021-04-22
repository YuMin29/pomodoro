package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class RestoreViewModel extends AndroidViewModel {
    private final String TAG = "[RestoreViewModel]";
    protected DataRepository mFirebaseRepository;
    protected DataRepository mRoomRepository;
    private LiveData<List<UserMission>> mFirebaseMissions;
    private LiveData<List<MissionState>> mFirebaseMissionStates;
    public MutableLiveData<Boolean> mProgress = new MutableLiveData<>();

    private MediatorLiveData<RestoreProgressResult> resultMediatorLiveData = new MediatorLiveData<>();

    public RestoreViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));

        mFirebaseMissions = mFirebaseRepository.getMissions();
        mFirebaseMissionStates = mFirebaseRepository.getMissionStateList();
        mProgress.setValue(true);

        RestoreProgressResult restoreProgressResult = new RestoreProgressResult();
        resultMediatorLiveData.addSource(mFirebaseMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                LogUtil.logE(TAG,"[resultMediatorLiveData] setRestoreMission SIZE = "+userMissionList.size());
                restoreProgressResult.setRestoreMission(userMissionList);
                resultMediatorLiveData.setValue(restoreProgressResult);
            }
        });

        resultMediatorLiveData.addSource(mFirebaseMissionStates, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStateList) {
                LogUtil.logE(TAG,"[resultMediatorLiveData] setRestoreMissionState SIZE = "+missionStateList.size());
                restoreProgressResult.setRestoreMissionState(missionStateList);
                resultMediatorLiveData.setValue(restoreProgressResult);
            }
        });
    }

    public MediatorLiveData<RestoreProgressResult> getResultMediatorLiveData(){
        return this.resultMediatorLiveData;
    }

    public void operateRestore(){
        // 1. delete room all mission data
        // 2. delete room all mission state
        // 3. sync mission from firebase
        // 4. sync mission state from firebase
        // sync Room to firebase
        mRoomRepository.deleteAllMission();
        syncMissionsToRoom();
    }

    private void syncMissionsToRoom() {
        LogUtil.logE(TAG,"[syncMissionsToRoom] mFirebaseMissions SIZE = "+mFirebaseMissions.getValue().size());
        for (UserMission userMission : mFirebaseMissions.getValue()) {
            mRoomRepository.addMission(userMission);
        }

        syncMissionStateToRoom();

        mProgress.setValue(false);
    }

    private void syncMissionStateToRoom() {
        LogUtil.logE(TAG,"[syncMissionStateToRoom] mFirebaseMissionStates SIZE = "+mFirebaseMissionStates.getValue().size());
        for (MissionState missionState : mFirebaseMissionStates.getValue()){
            for (UserMission userMission : mFirebaseMissions.getValue()) {
                if (missionState.getMissionId().equals(String.valueOf(userMission.getFirebaseMissionId()))) {
                    LogUtil.logE(TAG,"[syncMissionStateToRoom] userMission.getId() = "+userMission.getId());
                    missionState.setMissionId(String.valueOf(userMission.getId()));
                    mRoomRepository.saveMissionState(String.valueOf(userMission.getId()),missionState);
                }
            }
        }
    }

    public void setProgress(boolean value){
        this.mProgress.setValue(value);
    }

    public LiveData<Boolean> getProgress() {
        return mProgress;
    }

    public class RestoreProgressResult{
        private List<UserMission> restoreMission = null;
        private List<MissionState> restoreMissionState = null;

        public void setRestoreMission(List<UserMission> list){
            this.restoreMission = list;
        }

        public void setRestoreMissionState(List<MissionState> list){
            this.restoreMissionState = list;
        }

        public boolean isComplete(){
            LogUtil.logE(TAG,"[isComplete] RETURN = "+  String.valueOf(restoreMission != null && restoreMissionState != null));
            return restoreMission != null && restoreMissionState != null;
        }
    }
}