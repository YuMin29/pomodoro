package com.yumin.pomodoro.data.repository.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class MissionDBModel {
    int id;
    String name;
    int time;
    int shortBreak;
    int longBreak;
    int color;
    String operate;
    int goal;
    String repeat;
    boolean enableNotification;
    boolean enableSound;
    boolean enableVibrate;
    boolean keepScreenOn;

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

    public int getShortBreak() {
        return shortBreak;
    }

    public void setShortBreak(int shortBreak) {
        this.shortBreak = shortBreak;
    }

    public int getLongBreak() {
        return longBreak;
    }

    public void setLongBreak(int longBreak) {
        this.longBreak = longBreak;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
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
}
