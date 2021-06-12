package com.yumin.pomodoro.data;

import androidx.annotation.Nullable;
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
    private boolean isCompleted = false;
    private long recordDay = TimeToMillisecondUtil.getTodayInitTime();
    private long completedDay = -1;
    private String missionId = "";


    public MissionState() {}

    @Ignore
    public MissionState(int numberOfCompletion, boolean finished) {
        this.numberOfCompletion = numberOfCompletion;
        this.isCompleted = finished;
    }

    @Ignore
    public MissionState(int numberOfCompletion, boolean finished, long recordDay, long finishedDay, String missionId) {
        this.numberOfCompletion = numberOfCompletion;
        this.isCompleted = finished;
        this.recordDay = recordDay;
        this.completedDay = finishedDay;
        this.missionId = missionId;
    }

    @Override
    public String toString() {
        return "MissionState{" +
                "id=" + id +
                ", completeOfNumber=" + numberOfCompletion +
                ", finished=" + isCompleted +
                ", recordDay=" + recordDay +
                ", finishedDay=" + completedDay +
                ", missionId=" + missionId +
                "}";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof MissionState))
            return false;

        if (obj == this)
            return true;

        MissionState missionState = (MissionState) obj;
        LogUtil.logD("MissionState","missionState.getMissionId() = "+missionState.getMissionId()+" ,this.missionId = "+this.missionId);
        return this.missionId.equals(missionState.missionId);
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

    public boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public long getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(long recordDay) {
        this.recordDay = recordDay;
    }

    public long getCompletedDay() {
        return completedDay;
    }

    public void setCompletedDay(long completedDay) {
        this.completedDay = completedDay;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}
