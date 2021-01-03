package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private MainRepository mainRepository;
    private MutableLiveData<List<AdjustMissionItem>> adjustMissionItems = new MutableLiveData<>();
    private MutableLiveData<Mission> mission = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();

    // TODO: 12/29/20 operate day have relationship with repeat day 選的時候應該要監聽值 從init mission賦值
    public AddMissionViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application);
        this.mainRepository = mainRepository;
        fetchMission();
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
    }

    private void fetchMission(){
        // init adjust item
        mission.setValue(mainRepository.getInitMission());
    }
    public void setMissionOperateDay(long operateDay){
        LogUtil.logD(TAG,"[setTmpMissionOperateDay] operateDay ="+getTransferDate(operateDay));
        this.mission.getValue().setOperateDay(operateDay);
    }

    public long getMissionOperateDay(){
        return this.mission.getValue().getOperateDay();
    }

    public LiveData<Mission> getMission(){
        return this.mission;
    }

    public LiveData<Boolean> getSaveButtonClick(){
        return this.saveButtonClick;
    }

    public LiveData<Boolean> getCancelButtonClick(){
        return this.cancelButtonClick;
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+mission.getValue().toString());
        mainRepository.addMission(mission.getValue());
        saveButtonClick.postValue(true);
    }

    public void cancel(){
        cancelButtonClick.postValue(true);
    }

    public void updateRepeatStart(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+getTransferDate(time));
        mission.getValue().setRepeatStart(time);
    }

    public void updateRepeatEnd(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+getTransferDate(time));
        mission.getValue().setRepeatEnd(time);
    }

    private String getTransferDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(time));
    }
}
