package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;
import com.yumin.pomodoro.utils.base.BaseApplication;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "[HomeViewModel]";
    private MutableLiveData<Boolean> mIsLoading  = new MutableLiveData<Boolean>();
//    private RoomRepository roomRepository;
    private DataRepository dataRepository;
    private LiveData<List<UserMission>> allMissions;
    MutableLiveData<List<UserMission>> todayNoneRepeatMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> todayRepeatEverydayMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> todayRepeatDefineMissions = new MutableLiveData<>();

    MutableLiveData<List<UserMission>> comingNoneRepeatMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> comingRepeatEverydayMissions = new MutableLiveData<>();
    MutableLiveData<List<UserMission>> comingRepeatDefineMissions = new MutableLiveData<>();


    MutableLiveData<List<UserMission>> unfinishedMissions = new MutableLiveData<>();

    LiveData<List<UserMission>> finishedMissions;

    public HomeViewModel(Application application){
        LogUtil.logE(TAG,"[HomeViewModel] Constructor");
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchData();
    }

    public void refreshDataWhenLogout(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LogUtil.logE(TAG,"[refreshDataWhenLogout] get firebase user");
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(BaseApplication.getApplication()));
        } else {
            LogUtil.logE(TAG,"[refreshDataWhenLogout] no firebase user");
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(BaseApplication.getApplication()));
        }
        fetchData();
    }

    private void fetchData() {
        LogUtil.logE(TAG,"[fetchData]");
        mIsLoading.setValue(true);

        allMissions = this.dataRepository.getMissions();

        finishedMissions = this.dataRepository.getFinishedMissionList(TimeMilli.getTodayStartTime(),
                TimeMilli.getTodayEndTime());

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
            if (userMission.getRepeat() == UserMission.TYPE_NONE &&
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
            if (userMission.getRepeat() == UserMission.TYPE_EVERYDAY &&
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
        LogUtil.logE(TAG,"TimeMilli.getTodayStartTime() = "+TimeMilli.getTodayStartTime()+
                " , TimeMilli.getTodayEndTime() = "+ TimeMilli.getTodayEndTime());
        List<UserMission> missionList = new ArrayList<>();
        for (UserMission userMission : allMissions.getValue()) {
            if (userMission.getRepeat() == UserMission.TYPE_DEFINE &&
                TimeMilli.getTodayEndTime() >= userMission.getRepeatStart() &&
                TimeMilli.getTodayEndTime() <= userMission.getRepeatEnd()) {
                missionList.add(userMission);
            }
            LogUtil.logE(TAG,"userMission.getRepeatStart() = "+userMission.getRepeatStart()+
                    " ,  userMission.getRepeatEnd() = "+  userMission.getRepeatEnd());
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
            if (userMission.getRepeat() == UserMission.TYPE_EVERYDAY)
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
            if (userMission.getRepeat() == UserMission.TYPE_DEFINE &&
                    (userMission.getRepeatStart() > TimeMilli.getTodayEndTime() ||
                            userMission.getRepeatEnd() >= TimeMilli.getTodayEndTime()))
                missionList.add(userMission);
        }
        comingRepeatDefineMissions.setValue(missionList);
        return comingRepeatDefineMissions;
    }
    
    public MutableLiveData<Boolean> getLoading(){
        LogUtil.logD(TAG,"[getLoading]");
		return mIsLoading;
	}

    public LiveData<List<UserMission>> getFinishedMissions(){
        LogUtil.logE(TAG,"[getFinishedMissions]");
        return finishedMissions;
    }


    public void deleteMission(UserMission mission){
        this.dataRepository.deleteMission(mission);
    }

}
