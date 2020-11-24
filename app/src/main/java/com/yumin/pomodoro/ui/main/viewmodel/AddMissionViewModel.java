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

import java.util.List;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private MainRepository mainRepository;
    private MutableLiveData<List<AdjustMissionItem>> adjustMissionItems = new MutableLiveData<>();
    private MutableLiveData<Mission> mission = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelButtonClick = new MutableLiveData<>();

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
        mainRepository.addMission(getApplication(),mission.getValue());
        saveButtonClick.postValue(true);
    }

    public void cancel(){
        cancelButtonClick.postValue(true);
    }
}
