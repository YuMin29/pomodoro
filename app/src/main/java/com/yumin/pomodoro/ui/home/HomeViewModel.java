package com.yumin.pomodoro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.Mission;

import java.util.List;

public class HomeViewModel extends ViewModel {
    MutableLiveData<List<Mission>> mMissionList;

    public HomeViewModel(){
        mMissionList = new MutableLiveData<>();
    }

    public LiveData<List<Mission>> getList(){
        return mMissionList;
    }
}
