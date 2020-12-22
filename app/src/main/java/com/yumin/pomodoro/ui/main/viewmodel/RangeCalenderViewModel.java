package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

// shared view model between AddMissionFragment/EditMissionFragment/RangeCalenderViewFragment
public class RangeCalenderViewModel extends AndroidViewModel {
    private static final String TAG = "[RangeCalenderViewModel]";
    private MainRepository mainRepository;
    private int mMissionId;
    private MutableLiveData<Long> repeatStart = new MutableLiveData<Long>();
    private MutableLiveData<Long> repeatEnd = new MutableLiveData<Long>();
    private MutableLiveData<Boolean> clickCommit = new MutableLiveData<Boolean>();

    public RangeCalenderViewModel(@NonNull Application application, MainRepository mainRepository, int id) {
        super(application);
        this.mainRepository = mainRepository;
        mMissionId = id;
        LogUtil.logD(TAG,"missionId = "+ mMissionId);

        repeatStart.setValue(-1L);
        repeatEnd.setValue(-1L);

        if (mMissionId != -1)
            fetchMission(mMissionId);
    }

    private void fetchMission(int id){
        repeatStart.setValue(mainRepository.getMissionRepeatStart(id).getValue());
        repeatEnd.setValue(mainRepository.getMissionRepeatEnd(id).getValue());
    }

    public LiveData<Long> getRepeatStart(){
        return this.repeatStart;
    }

    public LiveData<Long> getRepeatEnd(){
        return this.repeatEnd;
    }

    public void setRepeatStart(long start) {
        if (start != -1)
            this.repeatStart.postValue(start);
    }

    public void setRepeatEnd(long end) {
        if (end != -1)
            this.repeatEnd.postValue(end);
    }

    public void setClickCommit(boolean clicked){
        LogUtil.logD(TAG,"[setClickCommit] clicked = "+clicked);
        this.clickCommit.postValue(clicked);
    }

    public LiveData<Boolean> getClickCommit(){
        return this.clickCommit;
    }
}
