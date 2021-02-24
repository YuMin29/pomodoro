package com.yumin.pomodoro.data.repository.firebase;

public class MissionState {
    private int completeOfNumber = 0;
    private boolean isFinish = false;

    public MissionState(int completeOfNumber, boolean isFinish) {
        this.completeOfNumber = completeOfNumber;
        this.isFinish = isFinish;
    }

    @Override
    public String toString() {
        return "MissionState{" +
                "completeOfNumber=" + completeOfNumber +
                ", isFinish=" + isFinish +
                '}';
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
}
