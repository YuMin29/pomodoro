package com.yumin.pomodoro.data.model;

import android.graphics.Color;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.yumin.pomodoro.R;

@Entity(tableName = "MyMission")
public class Mission {
    @PrimaryKey(autoGenerate = true)
    int id;
    private String name;
//    private Type mType;
    private int time = 25;
    private int shortBreakTime = 5;
    private int longBreakTime = 15;
    private int color = Color.parseColor("#595775");
    private long operateDay = System.currentTimeMillis();
    private int goal = 0;
    private String repeat = "NONE";
    private boolean enableNotification = true;
    private boolean enableSound = true;
//    private Volume mVolume = Volume.MEDIUM;
    private boolean enableVibrate = true;
    private boolean keepScreenOn = true;

    public enum Type{DEFAULT,NONE,COUNT;}

	public enum Repeat {
        NONE(1),
        TODAY(2),
        EVERYDAY(3);

        private Integer index;
        public Integer getIndex() {
            return index;
        }
        private static Repeat[] vals = values();

        Repeat(Integer index) {
            this.index = index;
        }
        /**
         * 根據索引獲取對應的列舉物件
         * @param index
         * @return
         */
        public static Repeat getEnumTypeByIndex(Integer index) {
            Repeat[] values = Repeat.values();
            for (Repeat value : values) {
                if (value.getIndex() == index) {
                    return value;
                }
            }
            return null;
        }
    }
	public enum Volume{SMALL,MEDIUM,LARGE;}

	private static final String TAG = "[Mission]";

	public Mission(){
	    // init
    }

    public Mission(String test, Type aDefault, int i) {
    }

    public Mission(String name, int time, int shortBreakTime, int longBreakTime, int color, long operateDay, int goal,
                   String repeat, boolean enableNotification, boolean enableSound, boolean enableVibrate, boolean keepScreenOn) {
        this.name = name;
        this.time = time;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.color = color;
        this.operateDay = operateDay;
        this.goal = goal;
        this.repeat = repeat;
        this.enableNotification = enableNotification;
        this.enableSound = enableSound;
        this.enableVibrate = enableVibrate;
        this.keepScreenOn = keepScreenOn;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", shortBreakTime=" + shortBreakTime +
                ", longBreakTime=" + longBreakTime +
                ", color=" + color +
                ", operateDay='" + operateDay + '\'' +
                ", goal=" + goal +
                ", repeat='" + repeat + '\'' +
                ", enableNotification=" + enableNotification +
                ", enableSound=" + enableSound +
                ", enableVibrate=" + enableVibrate +
                ", keepScreenOn=" + keepScreenOn +
                '}';
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
