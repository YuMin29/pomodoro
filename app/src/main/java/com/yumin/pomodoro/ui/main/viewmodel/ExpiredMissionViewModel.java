package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpiredMissionViewModel extends AndroidViewModel {
    private final String TAG = ExpiredMissionViewModel.class.getSimpleName();
    private RoomRepository mRoomRepository;
    private LiveData<List<UserMission>> mPastNoneRepeatMissions;
    private LiveData<List<UserMission>> mPastRepeatEveryMissions;
    private LiveData<List<UserMission>> mPastRepeatCustomizeMissions;
    private LiveData<List<MissionState>> mMissionStates;

    public ExpiredMissionViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        fetchData();
    }

    private void fetchData(){
        mMissionStates = mRoomRepository.getMissionStateList();
        mPastNoneRepeatMissions = mRoomRepository.getPastNoneRepeatMissions();
        mPastRepeatEveryMissions = mRoomRepository.getPastRepeatEverydayMissions();
        mPastRepeatCustomizeMissions = mRoomRepository.getPastRepeatDefineMissions();
    }

    public LiveData<List<MissionState>> getMissionStates(){
        return mMissionStates;
    }

    public MediatorLiveData<Result> getPastMissions() {
        MediatorLiveData<Result> pastMissions = new MediatorLiveData<Result>();
        final Result current = new Result();
        pastMissions.addSource(mPastNoneRepeatMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"mPastNoneRepeatMissions size = "+userMissions.size());
                current.setPastNoneRepeatMissions(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(mPastRepeatEveryMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"mPastRepeatEveryMissions size = "+userMissions.size());
                current.setPastRepeatEverydayMissions(userMissions);
                pastMissions.setValue(current);
            }
        });
        pastMissions.addSource(mPastRepeatCustomizeMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"mPastRepeatCustomizeMissions size = "+userMissions.size());
                current.setPastRepeatCustomizeMissions(userMissions);
                pastMissions.setValue(current);
            }
        });
        return pastMissions;
    }

    public class Result {
        public List<UserMission> mPastNoneRepeatMissions = new ArrayList<>();
        public List<UserMission> mPastRepeatEverydayMissions = new ArrayList<>();
        public List<UserMission> mPastRepeatCustomizeMissions = new ArrayList<>();

        public void setPastNoneRepeatMissions(List<UserMission> missions){
            mPastNoneRepeatMissions = missions;
        }

        public void setPastRepeatEverydayMissions(List<UserMission> missions){
            mPastRepeatEverydayMissions = missions;
        }

        public void setPastRepeatCustomizeMissions(List<UserMission> missions){
            mPastRepeatCustomizeMissions = missions;
        }

        public boolean isComplete() {
            return (mPastNoneRepeatMissions != null && mPastRepeatEverydayMissions != null && mPastRepeatCustomizeMissions != null);
        }
    }
}
