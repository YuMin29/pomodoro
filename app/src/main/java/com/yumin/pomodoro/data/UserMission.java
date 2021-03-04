package com.yumin.pomodoro.data;

import android.graphics.Color;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyMission")
public class UserMission {
    private static final String TAG = "[UserMission]";
    public static final int TYPE_NONE = 0;
    public static final int TYPE_EVERYDAY = 1;
    public static final int TYPE_DEFINE = 2;

    @PrimaryKey(autoGenerate = true)
    int id;
    private String name;
    private int time = 25;
    private int shortBreakTime = 5;
    private int longBreakTime = 15;
    private int color = Color.parseColor("#595775");
    private long operateDay = System.currentTimeMillis();
    private int goal = 1; // at least 1
    private int repeat = TYPE_NONE;
    private long repeatStart = -1L;
    private long repeatEnd = -1L;
    private boolean enableNotification = true;
    private boolean enableSound = true;
    private boolean enableVibrate = true;
    private boolean keepScreenOn = true;
    // TODO: 2021/2/17 考慮重複的情況 只留判斷今日完成?
    private boolean isFinished = false;
    private int numberOfCompletions = 0;
    private String firebaseMissionId = "";
    private long finishedDay = -1;
    private long createdTime;


    public UserMission(){}

    @Ignore
    public UserMission(int time, int shortBreakTime, int color) {
        this.name = "快速番茄任務";
        this.time = time;
        this.shortBreakTime = shortBreakTime;
        this.color = color;
        this.goal = -1;
        this.numberOfCompletions = -1;
    }

    @Ignore
    public UserMission(String name, int time, int shortBreakTime,
                       int longBreakTime, int color, long operateDay,
                       int goal, int repeat, int repeatStart, int repeatEnd,
                       boolean enableNotification, boolean enableSound,
                       boolean enableVibrate, boolean keepScreenOn,
                       boolean finished, String firebaseMissionId, long finishedDay, long createdTime) {
        this.name = name;
        this.time = time;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.color = color;
        this.operateDay = operateDay;
        this.goal = goal;
        this.repeat = repeat;
        this.repeatStart = repeatStart;
        this.repeatEnd = repeatEnd;
        this.enableNotification = enableNotification;
        this.enableSound = enableSound;
        this.enableVibrate = enableVibrate;
        this.keepScreenOn = keepScreenOn;
        this.isFinished = finished;
        this.firebaseMissionId = firebaseMissionId;
        this.finishedDay = finishedDay;
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "UserMission { " +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time + '\'' +
                ", shortBreakTime=" + shortBreakTime + '\'' +
                ", longBreakTime=" + longBreakTime + '\'' +
                ", color=" + color + '\'' +
                ", operateDay='" + operateDay + '\'' +
                ", goal=" + goal + '\'' +
                ", repeat='" + repeat + '\'' +
                ", repeatStart ='"+repeatStart + '\'' +
                ", repeatEnd ='"+repeatEnd + '\'' +
                ", enableNotification=" + enableNotification + '\'' +
                ", enableSound=" + enableSound + '\'' +
                ", enableVibrate=" + enableVibrate + '\'' +
                ", keepScreenOn=" + keepScreenOn + '\'' +
                ", isFinished =" + isFinished + '\'' +
                ", firebaseId = " + firebaseMissionId +
                ", finishedDay = " + finishedDay +
                ", createdTime = "+createdTime
                + " } ";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getShortBreakTime() {
        return shortBreakTime;
    }

    public void setShortBreakTime(int shortBreakTime) {
        this.shortBreakTime = shortBreakTime;
    }

    public int getLongBreakTime() {
        return longBreakTime;
    }

    public void setLongBreakTime(int longBreakTime) {
        this.longBreakTime = longBreakTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getOperateDay() {
        return operateDay;
    }

    public void setOperateDay(long operateDay) {
        this.operateDay = operateDay;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getRepeat() {
        return this.repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public long getRepeatStart() {
        return repeatStart;
    }

    public void setRepeatStart(long repeatStart) {
        this.repeatStart = repeatStart;
    }

    public long getRepeatEnd() {
        return repeatEnd;
    }

    public void setRepeatEnd(long repeatEnd) {
        this.repeatEnd = repeatEnd;
    }

    public boolean isEnableNotification() {
        return enableNotification;
    }

    public void setEnableNotification(boolean enableNotification) {
        this.enableNotification = enableNotification;
    }

    public boolean isEnableSound() {
        return enableSound;
    }

    public void setEnableSound(boolean enableSound) {
        this.enableSound = enableSound;
    }

    public boolean isEnableVibrate() {
        return enableVibrate;
    }

    public void setEnableVibrate(boolean enableVibrate) {
        this.enableVibrate = enableVibrate;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getNumberOfCompletions() {
        return numberOfCompletions;
    }

    public void setNumberOfCompletions(int numberOfCompletions) {
        this.numberOfCompletions = numberOfCompletions;
    }

    public String getFirebaseMissionId() {
        return firebaseMissionId;
    }

    public void setFirebaseMissionId(String firebaseMissionId) {
        this.firebaseMissionId = firebaseMissionId;
    }

    public long getFinishedDay(){
        return this.finishedDay;
    }

    public void setFinishedDay(long finishedDay){
        this.finishedDay = finishedDay;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
