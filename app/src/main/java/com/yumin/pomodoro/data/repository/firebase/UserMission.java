package com.yumin.pomodoro.data.repository.firebase;

import com.yumin.pomodoro.data.model.Mission;

public class UserMission extends Mission {
    private static final String TAG = "[UserMission]";
    private String stringId;

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
                       boolean finished, String stringId) {
        super(name, time, shortBreakTime, longBreakTime, color, operateDay, goal, repeat, repeatStart, repeatEnd, enableNotification, enableSound, enableVibrate, keepScreenOn, finished);
        this.stringId = stringId;
    }

    @Override
    public String toString() {
        return super.toString()+", stringId = "+stringId;
    }

    public String getStrId() {
        return stringId;
    }

    public void setStrId(String id) {
        this.stringId = id;
    }


}
