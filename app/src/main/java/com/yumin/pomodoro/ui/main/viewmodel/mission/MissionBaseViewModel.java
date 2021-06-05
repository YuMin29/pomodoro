package com.yumin.pomodoro.ui.main.viewmodel.mission;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class MissionBaseViewModel extends AndroidViewModel {
    public static final String TAG = MissionBaseViewModel.class.getSimpleName();
    protected DataRepository mDataRepository;
    protected MutableLiveData<UserMission> mMission = new MutableLiveData<>();
    protected LiveData<UserMission> mEditMission;
    protected MutableLiveData<Boolean> mIsSaveButtonClicked = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsCancelButtonClicked = new MutableLiveData<>();

    public abstract void fetchMission();
    public abstract void saveMission();

    public MissionBaseViewModel(@NonNull Application application) {
        super(application);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            this.mDataRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
//        else
            this.mDataRepository = new RoomRepository(new RoomApiServiceImpl(application));

        fetchMission();
        mIsSaveButtonClicked.postValue(false);
        mIsCancelButtonClicked.postValue(false);
    }

    public LiveData<UserMission> getMission(){
        return this.mMission;
    }

    public LiveData<UserMission> getEditMission(){
        return this.mEditMission;
    }

    public LiveData<Boolean> getIsSaveButtonClicked(){
        return this.mIsSaveButtonClicked;
    }

    public LiveData<Boolean> getIsCancelButtonClicked(){
        return this.mIsCancelButtonClicked;
    }

    public void cancel(){
        mIsCancelButtonClicked.postValue(true);
    }

    public void updateEditMissionRepeatStart(long time){
        LogUtil.logD(TAG,"[updateEditMissionRepeatStart] time = "+getTransferDate(time));
        mEditMission.getValue().setRepeatStart(time);
    }

    public void updateEditMissionRepeatEnd(long time){
        LogUtil.logD(TAG,"[updateEditMissionRepeatStart] time = "+getTransferDate(time));
        mEditMission.getValue().setRepeatEnd(time);
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
