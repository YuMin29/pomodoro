package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

// shared view model between AddMissionFragment/EditMissionFragment/RangeCalenderViewFragment
public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = "[RangeCalenderViewModel]";
    private MainRepository mainRepository;
    private int mMissionId;
    private LiveData<Long> repeatStart = new LiveData<Long>(-1L){};
    private LiveData<Long> repeatEnd = new LiveData<Long>(-1L) {};
    private LiveData<Long> missionOperateDay = new LiveData<Long>(-1L) {};

    public RangeCalenderViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mainRepository = mainRepository;
        this.mMissionId = MissionManager.getInstance().getRangeCalenderId();
        LogUtil.logD(TAG,"missionId = "+ mMissionId);

        if (mMissionId != -1) {
            fetchMission(mMissionId);
        }
    }

    private void fetchMission(int id){
        LogUtil.logD(TAG,"[fetchMission]");
        // TODO: 2020/12/30 需要考慮 重複進入點選區間 必須取得最後一次選的值
        repeatStart = mainRepository.getMissionRepeatStart(id);
        repeatEnd = mainRepository.getMissionRepeatEnd(id);
        missionOperateDay = mainRepository.getMissionOperateDay(id);
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
