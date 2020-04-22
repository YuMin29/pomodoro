package com.yumin.pomodoro.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.Mission;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private boolean DEBUG = true;
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<List<Mission>> mMissions = null;
    private MutableLiveData<Boolean> mIsLoading;

    public HomeViewModel(){
        mMissions = new MutableLiveData<List<Mission>>();
        mIsLoading = new MutableLiveData<Boolean>();
        loadMissions();
    }

    public LiveData<List<Mission>> getMissionList(){
        if (DEBUG)
            Log.d(TAG,"getMissionList");
        return mMissions;
    }
    
    public LiveData<Boolean> getLoading(){
        if (DEBUG)
            Log.d(TAG,"getLoading");
		return mIsLoading;
	}

    private void loadMissions(){
        if (DEBUG)
            Log.d(TAG,"loadMissions");

        mIsLoading.setValue(true);
        // Do an asynchronous operation to fetch missions.
        // Define default mission items in here.
        List<Mission> defaultMission = new ArrayList<>();
        defaultMission.add(new Mission("test1", Mission.Type.DEFAULT,10));
        defaultMission.add(new Mission("test2", Mission.Type.DEFAULT,10));
        defaultMission.add(new Mission("test3", Mission.Type.DEFAULT,10));
        defaultMission.add(new Mission("test4", Mission.Type.DEFAULT,10));
        defaultMission.add(new Mission("test5", Mission.Type.DEFAULT,10));
        mMissions.setValue(defaultMission);
        mIsLoading.setValue(false);
    }
}
