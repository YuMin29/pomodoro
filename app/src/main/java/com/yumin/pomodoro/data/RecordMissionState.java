package com.yumin.pomodoro.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "RecordMissionState",
        foreignKeys = {@ForeignKey(entity = UserMission.class ,parentColumns = "id",
        childColumns = "missionId",
        onDelete = ForeignKey.CASCADE)})
public class RecordMissionState {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int completeOfNumber = 0;
    private boolean isFinish = false;
    private long recordDay = 0;
    private int missionId;


    public RecordMissionState() {
    }

    @Ignore
    public RecordMissionState(int completeOfNumber, boolean isFinish) {
        this.completeOfNumber = completeOfNumber;
        this.isFinish = isFinish;
    }

    @Ignore
    public RecordMissionState(int completeOfNumber, boolean isFinish, long recordDay, int missionId) {
        this.completeOfNumber = completeOfNumber;
        this.isFinish = isFinish;
        this.recordDay = recordDay;
        this.missionId = missionId;
    }

    @Override
    public String toString() {
        return "RecordMissionState{" +
                "id=" + id +
                ", completeOfNumber=" + completeOfNumber +
                ", isFinish=" + isFinish +
                ", recordDay=" + recordDay +
                ", missionId=" + missionId +
                '}';
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public int getCompleteOfNumber() {
        return completeOfNumber;
    }

    public void setCompleteOfNumber(int completeOfNumber) {
        this.completeOfNumber = completeOfNumber;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
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
