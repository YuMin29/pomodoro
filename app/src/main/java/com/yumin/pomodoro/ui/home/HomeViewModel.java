package com.yumin.pomodoro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.Category;
import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private boolean DEBUG = true;
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<List<Mission>> mMissions = null;
    private MutableLiveData<List<Category>> mCategory = null;
    private MutableLiveData<Boolean> mIsLoading;

    public HomeViewModel(){
        mCategory = new MutableLiveData<List<Category>>();
        mMissions = new MutableLiveData<List<Mission>>();
        mIsLoading = new MutableLiveData<Boolean>();
        loadData();
    }

    public LiveData<List<Mission>> getMissionList(){
        if (DEBUG)
            LogUtil.logD(TAG,"getMissionList");
        return mMissions;
    }

    public LiveData<List<Category>> getCategoryList(){
        if (DEBUG)
            LogUtil.logD(TAG,"getCategoryList");
        return mCategory;
    }
    
    public LiveData<Boolean> getLoading(){
        if (DEBUG)
            LogUtil.logD(TAG,"getLoading");
		return mIsLoading;
	}

    private void loadData(){
        if (DEBUG)
            LogUtil.logD(TAG,"loadMissions");

        mIsLoading.setValue(true);
        // Do an asynchronous operation to fetch missions.
        // Define default mission items in here.
//        List<Mission> defaultMission = new ArrayList<>();
//        defaultMission.add(new Mission("Test",Mission.Type.DEFAULT,20));
//        mMissions.setValue(defaultMission);
        Category testCategory = new Category("Test");
        testCategory.addMission(new Mission("Test",Mission.Type.DEFAULT,20));
        testCategory.addMission(new Mission("Test222",Mission.Type.DEFAULT,20));
        Category testCategory2 = new Category("Test2");
        testCategory2.addMission(new Mission("Test2",Mission.Type.DEFAULT,20));
        List<Category> defaultCategory = new ArrayList<Category>();
        // add a edit item
        defaultCategory.add(testCategory);
        defaultCategory.add(testCategory2);
        mCategory.setValue(defaultCategory);
        mIsLoading.setValue(false);
    }
}
