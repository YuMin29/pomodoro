package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.mission.MissionBaseViewModel;
import com.yumin.pomodoro.utils.LogUtil;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends MissionBaseViewModel {
    public static final String TAG = AndroidViewModel.class.getSimpleName();

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchMission(){
        mMission.setValue(mDataRepository.getInitMission());
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ mMission.getValue().toString());
        mDataRepository.addMission(mMission.getValue());
        mIsSaveButtonClicked.postValue(true);
    }
}
