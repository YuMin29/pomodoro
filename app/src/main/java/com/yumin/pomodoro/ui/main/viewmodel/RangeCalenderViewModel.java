package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

// shared view model between AddMissionFragment/EditMissionFragment/RangeCalenderViewFragment
public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = "[RangeCalenderViewModel]";
//    private RoomRepository roomRepository;
    private FirebaseRepository firebaseRepository;
//    private int mMissionId;
    private String mStrMissionId;
    private LiveData<Long> repeatStart = new LiveData<Long>(-1L){};
    private LiveData<Long> repeatEnd = new LiveData<Long>(-1L) {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>(-1L) {};

    public RangeCalenderViewModel(@NonNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        this.firebaseRepository = firebaseRepository;
        this.mStrMissionId = MissionManager.getInstance().getStrRangeCalenderId();
        LogUtil.logD(TAG,"missionId = "+ mStrMissionId);

        if (!mStrMissionId.equals("-1")) {
            fetchMission(mStrMissionId);
        }
    }

    private void fetchMission(String id){
        LogUtil.logD(TAG,"[fetchMission]");
        repeatStart = firebaseRepository.getMissionRepeatStart(id);
        repeatEnd = firebaseRepository.getMissionRepeatEnd(id);
        missionOperateDay = firebaseRepository.getMissionOperateDay(id);
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
