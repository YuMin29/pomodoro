package com.yumin.pomodoro.utils.base;

public class MissionManager {
    private static final MissionManager mMissionManager =  new MissionManager();
    private int editId = -1;
    private int operateId = -1;
    private int rangeCalenderId = -1;
    private String strOperateId = "";
    private String strEditId = "";
    private String strRangeCalenderId = "";

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

    public void setStrEditId(String strEditId){
        this.strEditId = strEditId;
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

    public int getOperateId() {
        return operateId;
    }

    public void setOperateId(int operateId) {
        this.operateId = operateId;
    }

    public void setOperateId(String operateId){
        this.strOperateId = operateId;
    }

    public String getStrOperateId(){
        return this.strOperateId;
    }
}
