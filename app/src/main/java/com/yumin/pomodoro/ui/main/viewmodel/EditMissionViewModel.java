package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

public class EditMissionViewModel extends AndroidViewModel {
    private static final String TAG = "[EditMissionViewModel]";
    private MainRepository mainRepository;
    private LiveData<Mission> editMission;
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();
    private int missionId;

    public EditMissionViewModel(@NonNull Application application, MainRepository mainRepository, int missionId) {
        super(application);
        this.mainRepository = mainRepository;
        this.missionId = missionId;
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
        LogUtil.logD(TAG,"editId = "+ missionId);
        fetchMission();
    }

    private void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] ");
        editMission = mainRepository.getMissionById(missionId);
    }

    public LiveData<Mission> getEditMission(){
        return this.editMission;
    }

    public LiveData<Boolean> getSaveButtonClick(){
        return this.saveButtonClick;
    }

    public LiveData<Boolean> getCancelButtonClick(){
        return this.cancelButtonClick;
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ editMission.getValue().toString());
        mainRepository.updateMission(editMission.getValue());
        saveButtonClick.postValue(true);
    }

    public void cancel(){
        cancelButtonClick.postValue(true);
    }

    public void updateRepeatStart(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+time);
        editMission.getValue().setRepeatStart(time);
    }
    public void updateRepeatEnd(long time){
        LogUtil.logD(TAG,"[updateRepeatEnd] time = "+time);
        editMission.getValue().setRepeatEnd(time);
    }

}
