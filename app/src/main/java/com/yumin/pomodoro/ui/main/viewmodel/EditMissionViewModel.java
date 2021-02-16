package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMissionViewModel extends AndroidViewModel {
    private static final String TAG = "[EditMissionViewModel]";
//    private RoomRepository roomRepository;
    private FirebaseRepository firebaseRepository;
    private LiveData<UserMission> editMission;
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();
    private int missionId;
    private String missionStrId;

    public EditMissionViewModel(@NonNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        this.firebaseRepository = firebaseRepository;
        this.missionId = MissionManager.getInstance().getEditId();
        this.missionStrId = MissionManager.getInstance().getStrEditId();
        LogUtil.logD(TAG,"missionStrId = "+missionStrId);
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
        fetchMission();
    }

    private void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] ");
//        editMission = roomRepository.getMissionById(missionId);
        editMission = firebaseRepository.getMissionById(missionStrId);
    }

    public LiveData<UserMission> getEditMission(){
        return this.editMission;
    }

    private String getTransferDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(time));
    }

    public LiveData<Boolean> getSaveButtonClick(){
        return this.saveButtonClick;
    }

    public LiveData<Boolean> getCancelButtonClick(){
        return this.cancelButtonClick;
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ editMission.getValue().toString());
//        roomRepository.updateMission(editMission.getValue());
        firebaseRepository.updateMission(editMission.getValue());
        saveButtonClick.postValue(true);
    }

    public void cancel(){
        cancelButtonClick.postValue(true);
    }

    public void updateRepeatStart(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+getTransferDate(time));
        editMission.getValue().setRepeatStart(time);
    }
    public void updateRepeatEnd(long time){
        LogUtil.logD(TAG,"[updateRepeatEnd] time = "+getTransferDate(time));
        editMission.getValue().setRepeatEnd(time);
    }
}
