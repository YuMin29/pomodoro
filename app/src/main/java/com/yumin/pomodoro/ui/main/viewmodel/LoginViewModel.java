package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private RoomRepository mRoomRepository;
    private FirebaseRepository mFirebaseRepository;
    private LiveData<List<UserMission>> mRoomMissions;
    private MutableLiveData<Boolean> mIsRoomMissionsExist = new MutableLiveData<>();
    private LiveData<List<MissionState>> mRoomMissionStates;
    private MutableLiveData<Boolean> mIsRoomMissionStatesExist = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        mRoomMissions = mRoomRepository.getMissions();
        mIsRoomMissionsExist.setValue(false);
        mRoomMissionStates = mRoomRepository.getMissionStateList();
        mIsRoomMissionStatesExist.setValue(false);
    }

    public LiveData<List<UserMission>> getRoomMissions(){
        return mRoomMissions;
    }

    public LiveData<List<MissionState>> getRoomMissionStates(){
        return mRoomMissionStates;
    }

    public void setIsRoomMissionStatesExist(boolean exist){
        this.mIsRoomMissionStatesExist.setValue(exist);
    }

    public boolean getIsRoomMissionStatesExist(){
        return this.mIsRoomMissionStatesExist.getValue();
    }

    public void setIsRoomMissionsExist(boolean missionsExist){
        this.mIsRoomMissionsExist.setValue(missionsExist);
    }

    public boolean getIsRoomMissionsExist(){
        return this.mIsRoomMissionsExist.getValue();
    }

    public void syncRoomMissionsToFirebase() {
        for (UserMission userMission : mRoomMissions.getValue()) {
            String firebaseMissionId = mFirebaseRepository.addMission(userMission);

            if (getIsRoomMissionStatesExist()) {
                for (MissionState missionState : mRoomMissionStates.getValue()){
                    if (missionState.getMissionId().equals(String.valueOf(userMission.getId()))) {
                        mFirebaseRepository.saveMissionState(firebaseMissionId,missionState);
                    }
                }
            }
//            mRoomRepository.deleteMission(userMission);
        }
    }
}
