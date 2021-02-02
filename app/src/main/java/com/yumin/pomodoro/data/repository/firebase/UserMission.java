package com.yumin.pomodoro.data.repository.firebase;

import com.yumin.pomodoro.data.model.Mission;

public class UserMission extends Mission {
    private static final String TAG = "[UserMission]";
    private String uid;

    public UserMission() {
        super();
    }

    public UserMission(int time, int shortBreakTime, int color) {
        super(time, shortBreakTime, color);
    }

    public UserMission(String name, int time, int shortBreakTime,
                       int longBreakTime, int color, long operateDay,
                       int goal, int repeat, int repeatStart, int repeatEnd,
                       boolean enableNotification, boolean enableSound,
                       boolean enableVibrate, boolean keepScreenOn,
                       boolean finished,String uid) {
        super(name, time, shortBreakTime, longBreakTime, color, operateDay, goal, repeat, repeatStart, repeatEnd, enableNotification, enableSound, enableVibrate, keepScreenOn, finished);
        this.uid = uid;
    }

    @Override
    public String toString() {
        return super.toString()+", uis = "+uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
