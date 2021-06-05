package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;

import java.util.List;

public class BackupViewModel extends AndroidViewModel {
    protected DataRepository mFirebaseRepository;
    protected DataRepository mRoomRepository;
    private LiveData<List<UserMission>> mRoomMissions;
    private MutableLiveData<Boolean> mIsRoomMissionsExist = new MutableLiveData<>();
    private LiveData<List<MissionState>> mRoomMissionStates;
    private MutableLiveData<Boolean> mIsRoomMissionStatesExist = new MutableLiveData<>();
    public MutableLiveData<Boolean> mProgress = new MutableLiveData<>();

    public BackupViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        mRoomMissions = mRoomRepository.getMissions();
        mIsRoomMissionsExist.setValue(false);
        mRoomMissionStates = mRoomRepository.getMissionStateList();
        mIsRoomMissionStatesExist.setValue(false);
        mProgress.setValue(true);
    }

    public void operateBackup(){
        mProgress.setValue(true);
        // 1. delete id's all mission data
        // 2. delete id's all mission state
        // 3. sync mission
        // 4. sync mission state
        if (mIsRoomMissionsExist.getValue()) {
            // sync Room to firebase
            mFirebaseRepository.deleteAllMission();
            syncRoomMissionsToFirebase();
        }
        mProgress.setValue(false);
    }

    private void syncRoomMissionsToFirebase() {
        for (UserMission userMission : mRoomMissions.getValue()) {
            String firebaseMissionId = mFirebaseRepository.addMission(userMission);
            if (mIsRoomMissionStatesExist.getValue()) {
                for (MissionState missionState : mRoomMissionStates.getValue()){
                    if (missionState.getMissionId().equals(String.valueOf(userMission.getId()))) {
                        mFirebaseRepository.saveMissionState(firebaseMissionId,missionState);
                    }
                }
            }
        }
    }

    public LiveData<List<UserMission>> getRoomMissions(){
        return mRoomMissions;
    }

    public LiveData<List<MissionState>> getRoomMissionStates(){
        return mRoomMissionStates;
    }

    public void setIsRoomMissionStatesExist(boolean exist){
        mIsRoomMissionStatesExist.setValue(exist);
    }

    public void setIsRoomMissionsExist(boolean missionsExist){
        mIsRoomMissionsExist.setValue(missionsExist);
    }

    public LiveData<Boolean> getProgress() {
        return mProgress;
    }
}