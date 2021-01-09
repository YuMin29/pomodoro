package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();;
    private MainRepository mainRepository;
    private LiveData<List<Mission>> missions;
    private LiveData<List<Mission>> todayMissions;
    private LiveData<List<Mission>> comingMissions;
    private LiveData<List<Mission>> finishedMissions;
    private LiveData<List<Mission>> unfinishedMissions;

    public HomeViewModel(MainRepository mainRepository){
        this.mainRepository = mainRepository;
        fetchData();
    }

    private void fetchData() {
        mIsLoading.setValue(true);
        missions = this.mainRepository.getMissions();
        todayMissions = this.mainRepository.getTodayMissions(getCurrentStartTime(),getCurrentEndTime());
        comingMissions = this.mainRepository.getComingMissions(getCurrentEndTime());
        finishedMissions = this.mainRepository.getFinishedMissions();
        unfinishedMissions = this.mainRepository.getUnfinishedMissions();
        mIsLoading.setValue(false);
    }

    public LiveData<List<Mission>> getMissions(){
        LogUtil.logD(TAG,"getMissionList");
        return missions;
    }

    public LiveData<List<Mission>> getTodayMissions(){
        return todayMissions;
    }

    public LiveData<List<Mission>> getComingMissions(){
        return comingMissions;
    }
    
    public MutableLiveData<Boolean> getLoading(){
        LogUtil.logD(TAG,"getLoading");
		return mIsLoading;
	}

    public void updateIsFinishedById(int itemId,boolean finished){
        mainRepository.updateIsFinishedById(itemId,finished);
    }

    public LiveData<List<Mission>> getFinishedMissions(){
        return finishedMissions;
    }

    public LiveData<List<Mission>> getUnfinishedMissions(){
        return unfinishedMissions;
    }

    private long getCurrentStartTime(){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTimeInMillis();
    }

    private long getCurrentEndTime(){
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTimeInMillis();
    }

    public void deleteMission(Mission mission){
        this.mainRepository.deleteMission(mission);
    }
}
