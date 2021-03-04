package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private DataRepository dataRepository;
    private MutableLiveData<List<AdjustMissionItem>> adjustMissionItems = new MutableLiveData<>();
    private MutableLiveData<UserMission> mission = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchMission();
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
    }

    private void fetchMission(){
        // init adjust item
        mission.setValue(dataRepository.getInitMission());
    }
    public void setMissionOperateDay(long operateDay){
        LogUtil.logD(TAG,"[setTmpMissionOperateDay] operateDay ="+getTransferDate(operateDay));
        this.mission.getValue().setOperateDay(operateDay);
    }

    public long getMissionOperateDay(){
        return this.mission.getValue().getOperateDay();
    }

    public LiveData<UserMission> getMission(){
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
        dataRepository.addMission(mission.getValue());
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
