package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private boolean DEBUG = true;
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<List<Mission>> mMissions = null;
    private MutableLiveData<List<Category>> mCategory =  new MutableLiveData<List<Category>>();;
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();;

    private MainRepository mainRepository;
    private LiveData<List<Mission>> missions;
    private LiveData<List<Mission>> missionsToday;
    private LiveData<List<Mission>> missionsComing;

    public HomeViewModel(){
        mCategory = new MutableLiveData<List<Category>>();
        mMissions = new MutableLiveData<List<Mission>>();
        mIsLoading = new MutableLiveData<Boolean>();
        loadData();
    }

    public HomeViewModel(Application application,MainRepository mainRepository){
        this.mainRepository = mainRepository;
        missions = this.mainRepository.getMissions(application.getApplicationContext());
        missionsToday = this.mainRepository.getMissionsByOperate(System.currentTimeMillis(),System.currentTimeMillis());
        mIsLoading.setValue(true);
        loadData();
    }

    public LiveData<List<Mission>> getMissions(){
        if (DEBUG)
            LogUtil.logD(TAG,"getMissionList");
        return missions;
    }

    public LiveData<List<Mission>> getMissionsToday(){
        return missionsToday;
    }

    public LiveData<List<Category>> getCategoryList(){
        if (DEBUG)
            LogUtil.logD(TAG,"getCategoryList");
        return mCategory;
    }
    
    public MutableLiveData<Boolean> getLoading(){
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

        Category today = new Category("Today"); // 今天
//        if (missionsToday != null && missionsToday.getValue().size() > 1) {
//            for (Mission mission : missionsToday.getValue()) {
//                today.addMission(mission);
//            }
//        }

        today.addMission(new Mission("Test",Mission.Type.DEFAULT,20));
        today.addMission(new Mission("Test222",Mission.Type.DEFAULT,20));

        Category coming = new Category("Coming"); // 即將到來
        coming.addMission(new Mission("Test2",Mission.Type.DEFAULT,20));

        List<Category> defaultCategory = new ArrayList<Category>();
        // add a edit item
        defaultCategory.add(today);
        defaultCategory.add(coming);
        mCategory.setValue(defaultCategory);
        mIsLoading.setValue(false);
    }
}
