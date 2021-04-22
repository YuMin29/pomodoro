package com.yumin.pomodoro.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

@Entity(tableName = "MissionState",
        foreignKeys = {@ForeignKey(entity = UserMission.class ,parentColumns = "id",
        childColumns = "missionId",
        onDelete = ForeignKey.CASCADE)})
public class MissionState {
    @PrimaryKey(autoGenerate = true)
    int id;
    private int numberOfCompletion = 0;
    private boolean finished = false;
    private long recordDay = TimeToMillisecondUtil.getTodayInitTime();
    private long finishedDay = -1;
    private String missionId = "";


    public MissionState() {
        LogUtil.logE("[MissionState]","Constructor");
    }

    @Ignore
    public MissionState(int numberOfCompletion, boolean finished) {
        LogUtil.logE("[MissionState]","Constructor 111");
        this.numberOfCompletion = numberOfCompletion;
        this.finished = finished;
    }

    @Ignore
    public MissionState(int numberOfCompletion, boolean finished, long recordDay, long finishedDay, String missionId) {
        LogUtil.logE("[MissionState]","Constructor 222");
        this.numberOfCompletion = numberOfCompletion;
        this.finished = finished;
        this.recordDay = recordDay;
        this.finishedDay = finishedDay;
        this.missionId = missionId;
    }

    @Override
    public String toString() {
        return "MissionState{" +
                "id=" + id +
                ", completeOfNumber=" + numberOfCompletion +
                ", finished=" + finished +
                ", recordDay=" + recordDay +
                ", finishedDay=" + finishedDay +
                ", missionId=" + missionId +
                "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfCompletion() {
        return numberOfCompletion;
    }

    public void setNumberOfCompletion(int numberOfCompletion) {
        this.numberOfCompletion = numberOfCompletion;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public long getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(long recordDay) {
        this.recordDay = recordDay;
    }

    public long getFinishedDay() {
        return finishedDay;
    }

    public void setFinishedDay(long finishedDay) {
        this.finishedDay = finishedDay;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}
