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
import com.yumin.pomodoro.ui.main.viewmodel.mission.MissionBaseViewModel;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends MissionBaseViewModel {
    public static final String TAG = "[AddMissionViewModel]";

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchMission(){
        // init adjust item
        mMission.setValue(mDataRepository.getInitMission());
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ mMission.getValue().toString());
        mDataRepository.addMission(mMission.getValue());
        mIsSaveButtonClicked.postValue(true);
    }
}
