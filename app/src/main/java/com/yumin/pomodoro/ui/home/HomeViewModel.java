package com.yumin.pomodoro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.Mission;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    MutableLiveData<List<Mission>> mMissions;

    public HomeViewModel(){
        mMissions = new MutableLiveData<>();
    }

    public LiveData<List<Mission>> getList(){
        if (mMissions == null) {
            mMissions = new MutableLiveData<List<Mission>>();
            loadMissions();
        }
        return mMissions;
    }

    public LiveData<List<String>> getStringList(){
        MutableLiveData<List<String>> list = new MutableLiveData<>();
        List<String> data = new ArrayList<>();
        data.add("000");
        data.add("111");

        list.setValue(data);

        return list;
    }

    private void loadMissions(){
        // Do an asynchronous operation to fetch missions.
        // Define default mission items in here.
        List<Mission> defaultMission = new ArrayList<>();
        defaultMission.add(new Mission("test1"));
        defaultMission.add(new Mission("test2"));
        mMissions.setValue(defaultMission);
    }
}
