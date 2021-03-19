package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

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

public class CalenderViewModel extends ViewModel {
    private Application mApplication;
    private DataRepository dataRepository;
    private LiveData<List<MissionState>> allMissionStates;
    private LiveData<List<UserMission>> allUserMissions;

    public CalenderViewModel(Application application) {
        this.mApplication = application;

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchData();
    }

    private void fetchData(){
        this.allMissionStates = dataRepository.getMissionStates();
        this.allUserMissions = dataRepository.getMissions();
    }

    public LiveData<List<MissionState>> getAllMissionStates(){
        return this.allMissionStates;
    }

    public LiveData<List<UserMission>> getAllUserMissions(){
        return this.allUserMissions;
    }
}