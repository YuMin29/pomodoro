package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.view.calender.CalenderFragment;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class CalenderViewModel extends ViewModel {
    private final String TAG = "[CalenderViewModel]";
    private Application mApplication;
    private DataRepository dataRepository;
    private LiveData<List<MissionState>> allMissionStates;
    private LiveData<List<UserMission>> allUserMissions;

    // observe coming missions
    private MediatorLiveData<MissionResult> mediatorMissions = new MediatorLiveData<>();

    public CalenderViewModel(Application application) {
        this.mApplication = application;

//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchData();
    }

    private void fetchData(){
        this.allMissionStates = dataRepository.getMissionStateList();
        this.allUserMissions = dataRepository.getMissions();

        MissionResult missionResult = new MissionResult();
        mediatorMissions.addSource(allMissionStates, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStates) {
                missionResult.setAllMissionStates(missionStates);
                mediatorMissions.setValue(missionResult);
            }
        });
        mediatorMissions.addSource(allUserMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                missionResult.setAllUserMissions(userMissions);
                mediatorMissions.setValue(missionResult);
            }
        });
    }

    public MediatorLiveData<MissionResult> getMediatorMissionFromViewModel() {
        return mediatorMissions;
    }

    public LiveData<List<MissionState>> getAllMissionStates(){
        return this.allMissionStates;
    }

    public LiveData<List<UserMission>> getAllUserMissions(){
        return this.allUserMissions;
    }

    public class MissionResult {
        public List<UserMission> allUserMissions;
        public List<MissionState> allMissionStates;

        public MissionResult() {}

        public void setAllUserMissions(List<UserMission> missions) {
            if (!missions.isEmpty())
                LogUtil.logE(TAG,"[MissionResult][setAllUserMissions] SIZE = "+missions.size());

            this.allUserMissions = missions;
        }

        public void setAllMissionStates(List<MissionState> missionStates) {
            if (!missionStates.isEmpty())
                LogUtil.logE(TAG,"[MissionResult][setAllMissionStates] SIZE = "+missionStates.size());

            this.allMissionStates = missionStates;
        }

        public boolean isComplete() {
            return (allMissionStates != null && allUserMissions != null);
        }
    }
}