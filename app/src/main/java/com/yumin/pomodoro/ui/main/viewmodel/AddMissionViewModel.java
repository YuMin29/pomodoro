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

import java.text.SimpleDateFormat;
import java.util.Date;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private DataRepository dataRepository;
    MutableLiveData<UserMission> mMission = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsSaveButtonClicked = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsCancelButtonClicked = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.dataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        else
            this.dataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchMission();
        mIsSaveButtonClicked.postValue(false);
        mIsCancelButtonClicked.postValue(false);
    }

    private void fetchMission(){
        // init adjust item
        mMission.setValue(dataRepository.getInitMission());
    }

    public LiveData<UserMission> getMission(){
        return this.mMission;
    }

    public LiveData<Boolean> getIsSaveButtonClicked(){
        return this.mIsSaveButtonClicked;
    }

    public LiveData<Boolean> getIsCancelButtonClicked(){
        return this.mIsCancelButtonClicked;
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ mMission.getValue().toString());
        dataRepository.addMission(mMission.getValue());
        mIsSaveButtonClicked.postValue(true);
    }

    public void cancel(){
        mIsCancelButtonClicked.postValue(true);
    }

    public void updateRepeatStart(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+getTransferDate(time));
        mMission.getValue().setRepeatStart(time);
    }

    public void updateRepeatEnd(long time){
        LogUtil.logD(TAG,"[updateRepeatStart] time = "+getTransferDate(time));
        mMission.getValue().setRepeatEnd(time);
    }

    private String getTransferDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(time));
    }
}
