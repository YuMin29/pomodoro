package com.yumin.pomodoro.utils.base;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

public class MissionManager {
    private static final String TAG = "[MissionManager]";
    private static final MissionManager mMissionManager =  new MissionManager();
    private int rangeCalenderId = -1;
    private String strOperateId = "";
    private String strEditId = "";
    private String strRangeCalenderId = "";

    private MissionManager(){}

    public static MissionManager getInstance(){
        return mMissionManager;
    }

    public void setStrEditId(UserMission userMission){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LogUtil.logE(TAG,"setStrEditId = "+userMission.getFirebaseMissionId());
            this.strEditId = userMission.getFirebaseMissionId();
        } else {
            LogUtil.logE(TAG,"setStrEditId = "+userMission.getId());
            this.strEditId = String.valueOf(userMission.getId());
        }
    }

    public String getStrEditId(){
        return this.strEditId;
    }

    public int getRangeCalenderId() {
        return rangeCalenderId;
    }

    public void setRangeCalenderId(int rangeCalenderId) {
        this.rangeCalenderId = rangeCalenderId;
    }

    public void setRangeCalenderId(String rangeCalenderId){
        this.strRangeCalenderId = rangeCalenderId;
    }

    public String getStrRangeCalenderId(){
        return this.strRangeCalenderId;
    }

    public void setOperateId(UserMission userMission){
        if (userMission == null) {
            this.strOperateId = "quick_mission";
            return;
        }


        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            this.strOperateId = userMission.getFirebaseMissionId();
        else
            this.strOperateId = String.valueOf(userMission.getId());
    }

    public String getStrOperateId(){
        return this.strOperateId;
    }
}
