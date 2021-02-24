package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseQueryListLiveData;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();
//    private RoomRepository roomRepository;
    private FirebaseRepository firebaseRepository;
    private LiveData<List<UserMission>> allMissions;
    MutableLiveData<List<UserMission>> todayNoneRepeatMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> todayRepeatEverydayMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> todayRepeatDefineMissions = new MutableLiveData<>();

    MutableLiveData<List<UserMission>> comingNoneRepeatMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> comingRepeatEverydayMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> comingRepeatDefineMissions = new MutableLiveData<>();

    MutableLiveData<List<UserMission>> finishedMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> unfinishedMissions = new MutableLiveData<>();

    public HomeViewModel(FirebaseRepository firebaseRepository){
        this.firebaseRepository = firebaseRepository;
        fetchData();
    }

    private void fetchData() {
        LogUtil.logE(TAG,"[fetchData]");
        mIsLoading.setValue(true);
        allMissions = this.firebaseRepository.getMissions();
        mIsLoading.setValue(false);
    }

    public LiveData<List<UserMission>> getAllMissions(){
        LogUtil.logD(TAG,"getMissionList");
        return allMissions;
    }

    /**
     * 不重複 且 執行日為今天 [today start]<----operate day---->[today end]
     * @return
     */
    public LiveData<List<UserMission>> getTodayNoneRepeatMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == Mission.TYPE_NONE &&
                    TimeMilli.getTodayStartTime() <= userMission.getOperateDay() &&
                    userMission.getOperateDay() <= TimeMilli.getTodayEndTime()) {
                missionList.add(userMission);
            }
        }
        todayNoneRepeatMissions.setValue(missionList);
        return todayNoneRepeatMissions;
    }

    /**
     * 每日重複： everyday && 執行日為今天或今天以前
     * @return
     */
    public LiveData<List<UserMission>> getTodayRepeatEverydayMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == Mission.TYPE_EVERYDAY &&
                    userMission.getOperateDay() <= TimeMilli.getTodayEndTime())
                missionList.add(userMission);
        }
        todayRepeatEverydayMissions.setValue(missionList);
        return todayRepeatEverydayMissions;
    }

    /**
     * 特定範圍重複： 判斷今天有無在範圍區間內  [start]<--- today --->[end]
     * @return
     */
    public LiveData<List<UserMission>> getTodayRepeatDefineMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == Mission.TYPE_DEFINE &&
                TimeMilli.getTodayStartTime() >= userMission.getRepeatStart() &&
                TimeMilli.getTodayEndTime() <= userMission.getRepeatEnd())
                missionList.add(userMission);
        }
        todayRepeatDefineMissions.setValue(missionList);
        return todayRepeatDefineMissions;
    }

    /**
     * 不重複： 執行日大於今天結束
     * @return
     */
    public LiveData<List<UserMission>> getComingNoneRepeatMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getOperateDay() > TimeMilli.getTodayEndTime())
                missionList.add(userMission);
        }
        comingNoneRepeatMissions.setValue(missionList);
        return comingNoneRepeatMissions;
    }

    /**
     * 每日重複 ： everyday
     * @return
     */
    public LiveData<List<UserMission>> getComingRepeatEverydayMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == Mission.TYPE_EVERYDAY)
                missionList.add(userMission);
        }
        comingRepeatEverydayMissions.setValue(missionList);
        return comingRepeatEverydayMissions;
    }

    /**
     * 特定範圍重複 ： 範例
     * 	區間 4/1-4/10
     *
     * 	3/20 X 未來 以今天判斷未來有 4/1 開始 today end < [range start]
     * 	4/1  O 未來 以今天判斷未來還有~4/10 today end <= [range end]
     * 	4/10 O 未來 以今天判斷未來沒有 today end > [range end]
     * @return
     */
    public LiveData<List<UserMission>> getComingRepeatDefineMissions(){
        if (allMissions.getValue() == null)
            return null;

        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == Mission.TYPE_DEFINE &&
                    (userMission.getRepeatStart() > TimeMilli.getTodayEndTime() ||
                            userMission.getRepeatEnd() >= TimeMilli.getTodayEndTime()))
                missionList.add(userMission);
        }
        comingRepeatDefineMissions.setValue(missionList);
        return comingRepeatDefineMissions;
    }
    
    public MutableLiveData<Boolean> getLoading(){
        LogUtil.logD(TAG,"getLoading");
		return mIsLoading;
	}

    public void updateIsFinishedById(String itemId,boolean finished,int completeOfNumber){
//        roomRepository.updateIsFinishedById(itemId,finished);
        firebaseRepository.updateIsFinishedById(itemId,finished,completeOfNumber);
    }

    public LiveData<List<UserMission>> getFinishedMissions(){
        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (TimeMilli.getTodayStartTime() <= userMission.getFinishedDay() &&
                    userMission.getFinishedDay() <= TimeMilli.getTodayEndTime())
                missionList.add(userMission);
        }
        finishedMissions.setValue(missionList);
        return finishedMissions;
    }

    public LiveData<List<UserMission>> getUnfinishedMissions(){
        return unfinishedMissions;
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
