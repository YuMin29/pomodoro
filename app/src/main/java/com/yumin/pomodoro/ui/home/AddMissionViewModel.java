package com.yumin.pomodoro.ui.home;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.utils.CountView;
import com.yumin.pomodoro.utils.Event;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionViewModel extends AndroidViewModel{
    public static final String TAG = "[AddMissionViewModel]";

    public MutableLiveData<String> missionTitle = new MutableLiveData<>();
    public MutableLiveData<String> missionTime = new MutableLiveData<>();
    public MutableLiveData<String> missionBreak = new MutableLiveData<>();
    public MutableLiveData<Mission> mission = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMissionTitle(String title){
        missionTitle.postValue(title);
    }

    // if don't have exist mission, just create a new one
    // init view
    public void init(){
        LogUtil.logD(TAG,"[initView]");
        missionTitle.postValue("");
        missionTime.postValue("100");
        missionBreak.postValue("200");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtil.logD(TAG,"[onCleared]");
    }
}
