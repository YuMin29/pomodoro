package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class CalenderViewModel extends AndroidViewModel {
    private RoomRepository mRoomRepository;
    private LiveData<List<MissionState>> mAllMissionStates;
    private LiveData<List<UserMission>> mAllMissions;
    private MediatorLiveData<MissionResult> mMissionResult = new MediatorLiveData<>();

    public CalenderViewModel(Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        fetchData();
    }

    private void fetchData(){
        this.mAllMissionStates = mRoomRepository.getMissionStateList();
        this.mAllMissions = mRoomRepository.getMissions();

        MissionResult missionResult = new MissionResult();
        mMissionResult.addSource(mAllMissionStates, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStates) {
                missionResult.setAllMissionStates(missionStates);
                mMissionResult.setValue(missionResult);
            }
        });
        mMissionResult.addSource(mAllMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                missionResult.setAllUserMissions(userMissions);
                mMissionResult.setValue(missionResult);
            }
        });
    }

    public MediatorLiveData<MissionResult> getMissionResult() {
        return mMissionResult;
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