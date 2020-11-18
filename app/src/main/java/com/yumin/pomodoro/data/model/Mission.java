package com.yumin.pomodoro.data.model;

import android.graphics.Color;

import com.yumin.pomodoro.utils.LogUtil;


public class Mission {
    private String mName;
    private Type mType;
    private int mTime = 25;
    private int mShortBreakTime = 5;
    private int mLongBreakTime = 15;
    private int mColor;
    private String mOperateDay = "TODAY";
    private int mGoal = 0;
    private String mRepeat = "NONE";
    private boolean mEnableNotification = true;
    private boolean mEnableSound = true;
    private Volume mVolume = Volume.MEDIUM;
    private boolean mEnableVibrate = true;
    private boolean mKeepScreenOn = true;

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
	public enum Color{BLACK,WHITE,RED,YELLOW,GREEN;}
	public enum Volume{SMALL,MEDIUM,LARGE;}

	private static final String TAG = "[Mission]";

	public Mission(){
	    // init
    }

    public Mission(String test, Type aDefault, int i) {
    }

    public String dump(){
	    return "=== dump mission ===\n"+
                "name = "+getName()+", time = "+String.valueOf(getTime())+
                ", short break time = "+String.valueOf(getShortBreakTime())+
                ", long break time  = "+String.valueOf(getLongBreakTime())+
                ", color = "+String.valueOf(getColor())+
                ", operate day = "+getOperateDay()+
                ", goal = "+String.valueOf(getGoal())+
                ", repeat = "+getRepeat().toString()+
                ", notification = "+getEnableNotification()+
                ", sound = "+getEnableSound()+
                ", vibrate = "+getEnableVibrate()+
                ", keep screen on"+getKeepScreenOn();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        LogUtil.logD(TAG,"[setName] name = "+name);
        mName = name;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
	    LogUtil.logD(TAG,"[setTime] time = "+time);
        mTime = time;
    }

    public void setShortBreakTime(int time){
	    this.mShortBreakTime = time;
    }

    public int getShortBreakTime(){
	    return this.mShortBreakTime;
    }

    public void setLongBreakTime(int time){
	    this.mLongBreakTime = time;
    }

    public int getLongBreakTime(){
	    return this.mLongBreakTime;
    }

    public int getColor() {
        return android.graphics.Color.YELLOW;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public String getOperateDay() {
        return mOperateDay;
    }

    public void setOperateDay(String repeat) {
        this.mOperateDay = repeat;
    }

    public void setRepeat(String repeat){
	    this.mRepeat = repeat;
    }

    public String getRepeat(){
	    return this.mRepeat;
    }

    public void setGoal(int goal) {
	    mGoal = goal;
	}

    public int getGoal() {
	    return this.mGoal;
	}

    public void setEnableNotification(boolean enabled) {
        mEnableNotification = enabled;
    }

    public boolean getEnableNotification() {
        return this.mEnableNotification;
    }

    public void setEnableSound(boolean enabled) {
        mEnableSound = enabled;
    }

    public boolean getEnableSound() {
        return this.mEnableSound;
    }

    public void setVolume(Volume volume) {
        mVolume = volume;
    }

    public Volume getVolume() {
        return this.mVolume;
    }

    public void setEnableVibrate(boolean enabled) {
        mEnableVibrate = enabled;
    }

    public boolean getEnableVibrate() {
        return this.mEnableVibrate;
    }

    public void setKeepScreenOn(boolean enabled) {
        mKeepScreenOn = enabled;
    }

    public boolean getKeepScreenOn() {
        return this.mKeepScreenOn;
    }
}
