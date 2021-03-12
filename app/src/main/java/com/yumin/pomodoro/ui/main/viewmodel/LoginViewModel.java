package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private RoomRepository mRoomRepository;
    private FirebaseRepository mFirebaseRepository;
    private LiveData<List<UserMission>> roomMissions;
    private MutableLiveData<Boolean> isRoomMissionsExist = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        roomMissions = mRoomRepository.getMissions();
        isRoomMissionsExist.setValue(false);
    }

    public LiveData<List<UserMission>> getRoomMissions(){
        return roomMissions;
    }

    public void setIsRoomMissionsExist(boolean missionsExist){
        this.isRoomMissionsExist.setValue(missionsExist );
    }

    public boolean getIsRoomMissionsExist(){
        return this.isRoomMissionsExist.getValue();
    }

    public void syncRoomMissionsToFirebase(){
        for (UserMission userMission : roomMissions.getValue()) {
            mFirebaseRepository.addMission(userMission);
            mRoomRepository.deleteMission(userMission);
        }
    }
}
