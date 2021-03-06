package com.yumin.pomodoro.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.MissionManager;

public class EditMissionViewModel extends MissionBaseViewModel {
    private static final String TAG = EditMissionViewModel.class.getSimpleName();
    private String mMissionStrId;

    public EditMissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchMission(){
        this.mMissionStrId = MissionManager.getInstance().getStrEditId();
        LogUtil.logD(TAG,"[fetchMission] missionStrId = "+ mMissionStrId);
        mEditMission = mRoomRepository.getMissionById(mMissionStrId);
    }

    public void saveMission(){
        LogUtil.logD(TAG,"[saveMission] mission val = "+ mEditMission.getValue().toString());
        if (mEditMission.getValue().getRepeat() != UserMission.TYPE_DEFINE &&
                (mEditMission.getValue().getRepeatStart() != -1 || mEditMission.getValue().getRepeatEnd() != -1)) {
            mEditMission.getValue().setRepeatStart(-1);
            mEditMission.getValue().setRepeatEnd(-1);
        }

        mRoomRepository.updateMission(mEditMission.getValue());
        mIsSaveButtonClicked.postValue(true);
    }
}
