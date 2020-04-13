package com.yumin.pomodoro.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.Mission;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Mission>> mMissions = null;
    private MutableLiveData<Boolean> mIsLoading;

    public HomeViewModel(){
        mMissions = new MutableLiveData<List<Mission>>();
        mIsLoading = new MutableLiveData<Boolean>();
    }

    public LiveData<List<Mission>> getMissionList(){
        Log.d("[Stella]","getMissionList");

        mMissions = new MutableLiveData<List<Mission>>();
        loadMissions();

        return mMissions;
    }
    
    public LiveData<Boolean> getLoading(){
        if (mIsLoading == null) {
            mIsLoading = new MutableLiveData<Boolean>();
            mIsLoading.setValue(false);
        }
		return mIsLoading;
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
        Log.d("[Stella]","loadMissions");
        mIsLoading.setValue(true);
        // Do an asynchronous operation to fetch missions.
        // Define default mission items in here.
        List<Mission> defaultMission = new ArrayList<>();
        defaultMission.add(new Mission("test1"));
        defaultMission.add(new Mission("test2"));
        defaultMission.add(new Mission("test3"));
        defaultMission.add(new Mission("test4"));
        defaultMission.add(new Mission("test5"));
        mMissions.setValue(defaultMission);
        mIsLoading.setValue(false);
    }
}
