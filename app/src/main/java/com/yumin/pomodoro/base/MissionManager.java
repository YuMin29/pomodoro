package com.yumin.pomodoro.base;

import com.yumin.pomodoro.data.UserMission;

public class MissionManager {
    private static final MissionManager mMissionManager =  new MissionManager();
    private int mRangeCalenderId = -1;
    private String mStrOperateId = "";
    private String mStrEditId = "";
    private String mStrRangeCalenderId = "";

    private MissionManager(){}

    public static MissionManager getInstance(){
        return mMissionManager;
    }

    public void setStrEditId(UserMission userMission){
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            LogUtil.logE(TAG,"setStrEditId = "+userMission.getFirebaseMissionId());
//            this.strEditId = userMission.getFirebaseMissionId();
//        } else {
//            LogUtil.logE(TAG,"setStrEditId = "+userMission.getId());
            mStrEditId = String.valueOf(userMission.getId());
//        }
    }

    public String getStrEditId(){
        return mStrEditId;
    }

    public int getRangeCalenderId() {
        return mRangeCalenderId;
    }

    public void setRangeCalenderId(int rangeCalenderId) {
        this.mRangeCalenderId = rangeCalenderId;
    }

    public void setRangeCalenderId(String rangeCalenderId){
        mStrRangeCalenderId = rangeCalenderId;
    }

    public String getStrRangeCalenderId(){
        return mStrRangeCalenderId;
    }

    public void setOperateId(UserMission userMission){
        if (userMission == null) {
            mStrOperateId = "quick_mission";
            return;
        }

//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            mStrOperateId = userMission.getFirebaseMissionId();
//        else
            mStrOperateId = String.valueOf(userMission.getId());
    }

    public String getStrOperateId(){
        return this.mStrOperateId;
    }
}
