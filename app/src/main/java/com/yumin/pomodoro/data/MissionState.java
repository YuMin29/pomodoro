package com.yumin.pomodoro.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "MissionState",
        foreignKeys = {@ForeignKey(entity = UserMission.class ,parentColumns = "id",
        childColumns = "missionId",
        onDelete = ForeignKey.CASCADE)})

public class MissionState {

    @PrimaryKey(autoGenerate = true)
    private int id;
    public int numberOfCompletion = 0;
    public boolean isFinished = false;
    public long recordDay = -1;
    public long finishedDay = -1;
    public String missionId;


    public MissionState() {
    }

    @Ignore
    public MissionState(int numberOfCompletion, boolean isFinished) {
        this.numberOfCompletion = numberOfCompletion;
        this.isFinished = isFinished;
    }

    public long getFinishedDay() {
        return finishedDay;
    }

    public void setFinishedDay(long finishedDay) {
        this.finishedDay = finishedDay;
    }

    @Ignore
    public MissionState(int numberOfCompletion, boolean isFinished, long recordDay, long finishedDay, String missionId) {
        this.numberOfCompletion = numberOfCompletion;
        this.isFinished = isFinished;
        this.recordDay = recordDay;
        this.finishedDay = finishedDay;
        this.missionId = missionId;
    }

    @Override
    public String toString() {
        return "MissionState{" +
                "id=" + id +
                ", completeOfNumber=" + numberOfCompletion +
                ", isFinish=" + isFinished +
                ", recordDay=" + recordDay +
                ", finishedDay=" + finishedDay +
                ", missionId=" + missionId +
                '}';
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public int getNumberOfCompletion() {
        return numberOfCompletion;
    }

    public void setNumberOfCompletion(int numberOfCompletion) {
        this.numberOfCompletion = numberOfCompletion;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(long recordDay) {
        this.recordDay = recordDay;
    }
}
