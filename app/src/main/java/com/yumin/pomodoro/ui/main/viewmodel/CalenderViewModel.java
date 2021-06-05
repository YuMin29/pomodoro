package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class CalenderViewModel extends AndroidViewModel {
    private final String TAG = CalenderViewModel.class.getSimpleName();
    private DataRepository mDataRepository;
    private LiveData<List<MissionState>> mAllMissionStates;
    private LiveData<List<UserMission>> mAllUserMissions;
    // observe coming missions
    private MediatorLiveData<MissionResult> mMissions = new MediatorLiveData<>();

    public CalenderViewModel(Application application) {
        super(application);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            this.mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchData();
    }

    private void fetchData(){
        this.mAllMissionStates = mDataRepository.getMissionStateList();
        this.mAllUserMissions = mDataRepository.getMissions();

        MissionResult missionResult = new MissionResult();
        mMissions.addSource(mAllMissionStates, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStates) {
                missionResult.setAllMissionStates(missionStates);
                mMissions.setValue(missionResult);
            }
        });
        mMissions.addSource(mAllUserMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                missionResult.setAllUserMissions(userMissions);
                mMissions.setValue(missionResult);
            }
        });
    }

    public MediatorLiveData<MissionResult> getMediatorMissionFromViewModel() {
        return mMissions;
    }

    public LiveData<List<MissionState>> getAllMissionStates(){
        return this.mAllMissionStates;
    }

    public LiveData<List<UserMission>> getAllUserMissions(){
        return this.mAllUserMissions;
    }

    public class MissionResult {
        private final String TAG = MissionResult.class.getSimpleName();
        public List<UserMission> mAllUserMissions;
        public List<MissionState> mAllMissionStates;

        public void setAllUserMissions(List<UserMission> missions) {
            if (!missions.isEmpty())
                LogUtil.logE(TAG,"[setAllUserMissions] size = "+missions.size());

            this.mAllUserMissions = missions;
        }

        public void setAllMissionStates(List<MissionState> missionStates) {
            if (!missionStates.isEmpty())
                LogUtil.logE(TAG,"[setAllMissionStates] size = "+missionStates.size());

            this.mAllMissionStates = missionStates;
        }

        public boolean isComplete() {
            return (mAllMissionStates != null && mAllUserMissions != null);
        }
    }
}