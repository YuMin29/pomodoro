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
import com.yumin.pomodoro.ui.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMissionViewModel extends MissionBaseViewModel {
    private static final String TAG = "[EditMissionViewModel]";
    private String missionStrId;

    public EditMissionViewModel(@NonNull Application application) {
        super(application);
        LogUtil.logD(TAG,"[EditMissionViewModel] ");
    }

    public void fetchMission(){
        LogUtil.logD(TAG,"[fetchMission] missionStrId = "+missionStrId);
        this.missionStrId = MissionManager.getInstance().getStrEditId();
        mEditMission = mDataRepository.getMissionById(missionStrId);
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ mEditMission.getValue().toString());
        if (mEditMission.getValue().getRepeat() != UserMission.TYPE_DEFINE &&
                (mEditMission.getValue().getRepeatStart() != -1 || mEditMission.getValue().getRepeatEnd() != -1)) {
            LogUtil.logE(TAG,"[saveMission] clear repeat start and end");
            mEditMission.getValue().setRepeatStart(-1);
            mEditMission.getValue().setRepeatEnd(-1);
        }

        mDataRepository.updateMission(mEditMission.getValue());
        mIsSaveButtonClicked.postValue(true);
    }
}
