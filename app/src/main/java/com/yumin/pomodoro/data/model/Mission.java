package com.yumin.pomodoro.data.model;

import com.yumin.pomodoro.utils.LogUtil;


public class Mission {
    private String mName;
    private Type mType;
    private int mTime = 25;
    private int mShortBreakTime = 5;
    private int mLongBreakTime = 15;
    private Color mColor = Color.WHITE;
    private Operate mOperateDay = Operate.EVERYDAY;
    private int mGoal = 0;
    private int mRepeat = 1;
    private boolean mEnableNotification = true;
    private boolean mEnableSound = true;
    private Volume mVolume = Volume.MEDIUM;
    private boolean mEnableVibrate = true;
    private boolean mKeepScreenOn = true;

    public enum Type{DEFAULT,NONE,COUNT;}

	public enum Operate {
        NONE(1),
        EVERYDAY(2),
        CHOOSE(3);

        private Integer index;
        public Integer getIndex() {
            return index;
        }
        Operate(Integer index) {
            this.index = index;
        }
        /**
         * 根據索引獲取對應的列舉物件
         * @param index
         * @return
         */
        public static Operate getEnumTypeByIndex(Integer index) {
            Operate[] values = Operate.values();
            for (Operate value : values) {
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

    public Color getColor() {
        return mColor;
    }

    public void setColor(Color color) {
        mColor = color;
    }

    public Operate getOperateDay() {
        return mOperateDay;
    }

    public void setOperateDay(Operate repeat) {
        this.mOperateDay = repeat;
    }

    public void setRepeat(int repeat){
	    this.mRepeat = repeat;
    }

    public int getRepeat(){
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
