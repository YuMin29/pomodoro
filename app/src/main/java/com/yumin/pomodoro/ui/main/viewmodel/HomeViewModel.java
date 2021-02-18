package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();
//    private RoomRepository roomRepository;
    private FirebaseRepository firebaseRepository;
    private LiveData<List<UserMission>> missions;
    // get for today
    private LiveData<List<UserMission>> todayMissionsByOperateDay;
    private LiveData<List<UserMission>> todayMissionsByRepeatType;
    private LiveData<List<UserMission>> todayMissionsByRepeatRange;
    // get for coming
    private LiveData<List<UserMission>> comingMissionsByOperateDay;
    private LiveData<List<UserMission>> comingMissionsByRepeatType;
    private LiveData<List<UserMission>> comingMissionsByRepeatRange;

    private LiveData<List<UserMission>> finishedMissions;
    private LiveData<List<UserMission>> unfinishedMissions;

    public HomeViewModel(FirebaseRepository firebaseRepository){
        this.firebaseRepository = firebaseRepository;
        fetchData();
    }

    private void fetchData() {
        mIsLoading.setValue(true);
        missions = this.firebaseRepository.getMissions();
//        todayMissions = this.roomRepository.getTodayMissions(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByOperateDay =  firebaseRepository.getTodayMissionsByOperateDay(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByRepeatType = firebaseRepository.getTodayMissionsByRepeatType(getCurrentStartTime(),getCurrentEndTime());
        todayMissionsByRepeatRange = firebaseRepository.getTodayMissionsByRepeatRange(getCurrentStartTime(),getCurrentEndTime());

        comingMissionsByOperateDay = firebaseRepository.getComingMissionsByOperateDay(getCurrentEndTime());
        comingMissionsByRepeatType = firebaseRepository.getComingMissionsByRepeatType(getCurrentEndTime());
        comingMissionsByRepeatRange = firebaseRepository.getComingMissionsByRepeatRange(getCurrentEndTime());

        finishedMissions = this.firebaseRepository.getFinishedMissions(getCurrentStartTime(),getCurrentEndTime());
        unfinishedMissions = this.firebaseRepository.getUnfinishedMissions(getCurrentStartTime(),getCurrentEndTime());
        mIsLoading.setValue(false);
    }

    public LiveData<List<UserMission>> getMissions(){
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

    public void updateIsFinishedById(String itemId,boolean finished){
//        roomRepository.updateIsFinishedById(itemId,finished);
        firebaseRepository.updateIsFinishedById(itemId,finished);
    }

    public LiveData<List<UserMission>> getFinishedMissions(){
        return finishedMissions;
    }

    public LiveData<List<UserMission>> getUnfinishedMissions(){
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
//        this.roomRepository.deleteMission(mission);
        firebaseRepository.deleteMission((UserMission) mission);
    }

    public void initNumberOfCompletions(String uid){
        LogUtil.logD(TAG,"[initNumberOfCompletions] uid = "+uid);
        firebaseRepository.updateNumberOfCompletionById(uid,0);
    }
}
