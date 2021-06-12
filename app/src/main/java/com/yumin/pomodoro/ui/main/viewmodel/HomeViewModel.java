package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;
import com.yumin.pomodoro.ui.base.BaseApplication;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private RoomRepository mDataRepository;
    LiveData<List<UserMission>> mTodayNoneRepeatMissions;
    LiveData<List<UserMission>> mTodayRepeatEverydayMissions;
    LiveData<List<UserMission>> mTodayRepeatCustomizeMissions;
    LiveData<List<UserMission>> mComingNoneRepeatMissions;
    LiveData<List<UserMission>> mComingRepeatEverydayMissions;
    LiveData<List<UserMission>> mComingRepeatCustomizeMissions;
    LiveData<List<UserMission>> mCompletedMissions;

    public HomeViewModel(Application application) {
        super(application);
        mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));
        fetchData();
    }

    public void refreshDataWhenLogout() {
        mDataRepository = new RoomRepository(new RoomApiServiceImpl(BaseApplication.getApplication()));
        mDataRepository.deleteAllMission();
        fetchData();
    }

    private void fetchData() {
        LogUtil.logE(TAG, "[fetchData]");
        mCompletedMissions = mDataRepository.getCompletedMissionList(TimeToMillisecondUtil.getTodayStartTime(),
                TimeToMillisecondUtil.getTodayEndTime());
        mTodayNoneRepeatMissions = mDataRepository.getTodayNoneRepeatMissions();
        mTodayRepeatEverydayMissions = mDataRepository.getTodayRepeatEverydayMissions();
        mTodayRepeatCustomizeMissions = mDataRepository.getTodayRepeatCustomizeMissions();
        mComingNoneRepeatMissions = mDataRepository.getComingNoneRepeatMissions();
        mComingRepeatEverydayMissions = mDataRepository.getComingRepeatEverydayMissions();
        mComingRepeatCustomizeMissions = mDataRepository.getComingRepeatCustomizeMissions();
    }

    public MediatorLiveData<Result> getTodayMissions() {
        // observe today missions
        MediatorLiveData<Result> todayMissions = new MediatorLiveData<Result>();
        final Result result = new Result();
        todayMissions.addSource(mTodayNoneRepeatMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mTodayNoneRepeatMissions [onChanged] SIZE = " + userMissions.size());
                result.setNoneRepeatMissions(userMissions);
                todayMissions.setValue(result);
            }
        });
        todayMissions.addSource(mTodayRepeatEverydayMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mTodayRepeatEverydayMissions [onChanged] SIZE = " + userMissions.size());
                result.setRepeatEverydayMissions(userMissions);
                todayMissions.setValue(result);
            }
        });
        todayMissions.addSource(mTodayRepeatCustomizeMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mTodayRepeatCustomizeMissions [onChanged] SIZE = " + userMissions.size());
                result.setRepeatCustomizeMissions(userMissions);
                todayMissions.setValue(result);
            }
        });
        return todayMissions;
    }

    public MediatorLiveData<Result> getComingMissions() {
        // observe coming missions
        MediatorLiveData<Result> comingMissions = new MediatorLiveData<>();
        final Result result = new Result();
        comingMissions.addSource(mComingNoneRepeatMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mComingNoneRepeatMissions [onChanged] SIZE = " + userMissions.size());
                result.setNoneRepeatMissions(userMissions);
                comingMissions.setValue(result);
            }
        });
        comingMissions.addSource(mComingRepeatEverydayMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mComingRepeatEverydayMissions [onChanged] SIZE = " + userMissions.size());
                result.setRepeatEverydayMissions(userMissions);
                comingMissions.setValue(result);
            }
        });
        comingMissions.addSource(mComingRepeatCustomizeMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG, "mComingRepeatCustomizeMissions [onChanged] SIZE = " + userMissions.size());
                result.setRepeatCustomizeMissions(userMissions);
                comingMissions.setValue(result);
            }
        });
        return comingMissions;
    }

    public LiveData<List<UserMission>> getCompletedMissions() {
        LogUtil.logE(TAG, "[getCompletedMissions]");
        return mCompletedMissions;
    }

    public class Result {
        public List<UserMission> mNoneRepeatMissions;
        public List<UserMission> mRepeatEverydayMissions;
        public List<UserMission> mRepeatCustomizeMissions;

        public void setNoneRepeatMissions(List<UserMission> missions) {
            mNoneRepeatMissions = missions;
        }

        public void setRepeatEverydayMissions(List<UserMission> missions) {
            mRepeatEverydayMissions = missions;
        }

        public void setRepeatCustomizeMissions(List<UserMission> missions) {
            mRepeatCustomizeMissions = missions;
        }

        public boolean isCompleted() {
            return (mNoneRepeatMissions != null && mRepeatEverydayMissions != null && mRepeatCustomizeMissions != null);
        }
    }

    public void deleteMission(UserMission mission) {
        mDataRepository.deleteMission(mission);
    }
}
