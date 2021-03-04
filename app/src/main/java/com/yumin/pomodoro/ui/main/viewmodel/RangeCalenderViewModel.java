package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

// shared view model between AddMissionFragment/EditMissionFragment/RangeCalenderViewFragment
public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = "[RangeCalenderViewModel]";
//    private RoomRepository roomRepository;
//    private FirebaseRepository firebaseRepository;
//    private int mMissionId;
    private DataRepository dataRepository;
    private String mStrMissionId;
    private LiveData<Long> repeatStart = new LiveData<Long>(-1L){};
    private LiveData<Long> repeatEnd = new LiveData<Long>(-1L) {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>(-1L) {};

    public RangeCalenderViewModel(@NonNull Application application) {
        super(application);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        this.mStrMissionId = MissionManager.getInstance().getStrRangeCalenderId();
        LogUtil.logD(TAG,"missionId = "+ mStrMissionId);

        if (!mStrMissionId.equals("-1")) {
            fetchMission(mStrMissionId);
        }
    }

    private void fetchMission(String id){
        LogUtil.logD(TAG,"[fetchMission]");
        repeatStart = dataRepository.getMissionRepeatStart(id);
        repeatEnd = dataRepository.getMissionRepeatEnd(id);
        missionOperateDay = dataRepository.getMissionOperateDay(id);
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
