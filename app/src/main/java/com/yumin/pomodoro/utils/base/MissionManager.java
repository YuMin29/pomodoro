package com.yumin.pomodoro.utils.base;

public class MissionManager {
    private static final MissionManager mMissionManager =  new MissionManager();
    private int editId = -1;
    private int operateId = -1;
    private int rangeCalenderId = -1;

    private MissionManager(){}

    public static MissionManager getInstance(){
        return mMissionManager;
    }

    public void setEditId(int editId){
        this.editId = editId;
    }

    public int getEditId(){
        return this.editId;
    }

    public int getRangeCalenderId() {
        return rangeCalenderId;
    }

    public void setRangeCalenderId(int rangeCalenderId) {
        this.rangeCalenderId = rangeCalenderId;
    }

    public int getOperateId() {
        return operateId;
    }

    public void setOperateId(int operateId) {
        this.operateId = operateId;
    }
}