package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

// shared view model between AddMissionFragment/EditMissionFragment/RangeCalenderViewFragment
public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = "[RangeCalenderViewModel]";
    private RoomRepository roomRepository;
    private int mMissionId;
    private LiveData<Long> repeatStart = new LiveData<Long>(-1L){};
    private LiveData<Long> repeatEnd = new LiveData<Long>(-1L) {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>(-1L) {};

    public RangeCalenderViewModel(@NonNull Application application, RoomRepository roomRepository) {
        super(application);
        this.roomRepository = roomRepository;
        this.mMissionId = MissionManager.getInstance().getRangeCalenderId();
        LogUtil.logD(TAG,"missionId = "+ mMissionId);

        if (mMissionId != -1) {
            fetchMission(mMissionId);
        }
    }

    private void fetchMission(int id){
        LogUtil.logD(TAG,"[fetchMission]");
        repeatStart = roomRepository.getMissionRepeatStart(id);
        repeatEnd = roomRepository.getMissionRepeatEnd(id);
        missionOperateDay = roomRepository.getMissionOperateDay(id);
    }

    public LiveData<Long> getRepeatStart(){
        return this.repeatStart;
    }

    public LiveData<Long> getRepeatEnd(){
        return this.repeatEnd;
    }

    public LiveData<Long> getMissionOperateDay(){
        return this.missionOperateDay;
    }
}
