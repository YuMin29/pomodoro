package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();
    private RoomRepository roomRepository;
    private LiveData<List<Mission>> missions;
    // get for today
    private LiveData<List<UserMission>> todayMissionsByOperateDay;
    private LiveData<List<UserMission>> todayMissionsByRepeatType;
    private LiveData<List<UserMission>> todayMissionsByRepeatRange;
    // get for coming
    private LiveData<List<UserMission>> comingMissionsByOperateDay;
    private LiveData<List<UserMission>> comingMissionsByRepeatType;
    private LiveData<List<UserMission>> comingMissionsByRepeatRange;

    private LiveData<List<Mission>> finishedMissions;
    private LiveData<List<Mission>> unfinishedMissions;

    public HomeViewModel(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
        fetchData();
    }

    private void fetchData() {
        mIsLoading.setValue(true);
        missions = this.roomRepository.getMissions();
//        todayMissions = this.roomRepository.getTodayMissions(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByOperateDay =  new FirebaseApiServiceImpl().getTodayMissionsByOperateDay(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByRepeatType = new FirebaseApiServiceImpl().getTodayMissionsByRepeatType(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByRepeatRange = new FirebaseApiServiceImpl().getTodayMissionsByRepeatRange(getCurrentStartTime(),getCurrentEndTime());

        comingMissionsByOperateDay = new FirebaseApiServiceImpl().getComingMissionsByOperateDay(getCurrentEndTime());
        comingMissionsByRepeatType = new FirebaseApiServiceImpl().getComingMissionsByRepeatType(getCurrentEndTime());
        comingMissionsByRepeatRange = new FirebaseApiServiceImpl().getComingMissionsByRepeatRange(getCurrentEndTime());

        finishedMissions = this.roomRepository.getFinishedMissions();
        unfinishedMissions = this.roomRepository.getUnfinishedMissions();
        mIsLoading.setValue(false);
    }

    public LiveData<List<Mission>> getMissions(){
        LogUtil.logD(TAG,"getMissionList");
        return missions;
    }

    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(){
        return todayMissionsByOperateDay;
    }

    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(){
        return todayMissionsByRepeatType;
    }

    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(){
        return todayMissionsByRepeatRange;
    }

    public LiveData<List<UserMission>> getComingMissionsByOperateDay(){
        return comingMissionsByOperateDay;
    }

    public LiveData<List<UserMission>> getComingMissionsByRepeatType(){
        return comingMissionsByRepeatType;
    }

    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(){
        return comingMissionsByRepeatRange;
    }
    
    public MutableLiveData<Boolean> getLoading(){
        LogUtil.logD(TAG,"getLoading");
		return mIsLoading;
	}

    public void updateIsFinishedById(int itemId,boolean finished){
        roomRepository.updateIsFinishedById(itemId,finished);
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
        this.roomRepository.deleteMission(mission);
    }
}
