package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class EditMissionViewModel extends AndroidViewModel {
    private static final String TAG = "[EditMissionViewModel]";
    private MainRepository mainRepository;
    private LiveData<Mission> editMission;
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();
    private int editId;

    public EditMissionViewModel(@NonNull Application application, MainRepository mainRepository, int editId) {
        super(application);
        this.mainRepository = mainRepository;
        this.editId = editId;
        saveButtonClick.postValue(false);
        cancelButtonClick.postValue(false);
        LogUtil.logD(TAG,"editId = "+editId);
        fetchMission();
    }

    private void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] ");
        editMission = mainRepository.getMissionById(editId);
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
}
