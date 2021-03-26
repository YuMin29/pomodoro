package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMissionViewModel extends AndroidViewModel {
    private static final String TAG = "[EditMissionViewModel]";
    private DataRepository dataRepository;
    private LiveData<UserMission> editMission;
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();
    private int missionId;
    private String missionStrId;

    public EditMissionViewModel(@NonNull Application application) {
        super(application);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        } else {
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));
        }

        this.missionStrId = MissionManager.getInstance().getStrEditId();

        LogUtil.logD(TAG,"missionStrId = "+missionStrId);
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
        fetchMission();
    }

    private void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] ");
//        editMission = roomRepository.getMissionById(missionId);
        editMission = dataRepository.getMissionById(missionStrId);
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
        dataRepository.updateMission(editMission.getValue());
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
