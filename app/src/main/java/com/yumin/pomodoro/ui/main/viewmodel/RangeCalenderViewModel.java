package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.MissionManager;

public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = RangeCalenderViewModel.class.getSimpleName();
    private DataRepository mDataRepository;
    private String mStrMissionId;
    private LiveData<Long> mRepeatStart = new LiveData<Long>(-1L){};
    private LiveData<Long> mRepeatEnd = new LiveData<Long>(-1L) {};
    private LiveData<Long> mMissionOperateDay = new LiveData<Long>(-1L) {};

    public RangeCalenderViewModel(@NonNull Application application) {
        super(application);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            mDataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        mStrMissionId = MissionManager.getInstance().getStrRangeCalenderId();
        LogUtil.logD(TAG,"missionId = "+ mStrMissionId);

        if (!mStrMissionId.equals("-1")) {
            fetchMission(mStrMissionId);
        }
    }

    private void fetchMission(String id){
        LogUtil.logD(TAG,"[fetchMission]");
        mRepeatStart = mDataRepository.getMissionRepeatStart(id);
        mRepeatEnd = mDataRepository.getMissionRepeatEnd(id);
        mMissionOperateDay = mDataRepository.getMissionOperateDay(id);
    }

    public LiveData<Long> getRepeatStart(){
        return mRepeatStart;
    }

    public LiveData<Long> getRepeatEnd(){
        return mRepeatEnd;
    }

    public LiveData<Long> getMissionOperateDay(){
        return mMissionOperateDay;
    }
}
