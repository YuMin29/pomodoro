package com.yumin.pomodoro.data;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;


public class Mission {
    public String mName;
    public Type mType;
    public int mTime;
    public Drawable mIcon;
    public int mColor;
    public int mDay;
    public int mFrequency;
    public int mRepeat;
    private List<SubMission> mSubMissions;
    private List<SubMission> mSavedSubMissions;

	public enum Type{DEFAULT,NONE,COUNT;}
	public enum Repeat{NONE,EVERYDAY,MON,TUE,WED,THU,FRI,SAT,SUN;}

	private static final String TAG = "[Mission]";

	public Mission(){
        this(null, null, 0, null, -1, 0, -1, -1);
    }

	public Mission(String name) {
        this(name, Type.COUNT, 0, null, -1, 0, -1, -1);
    }
	
    public Mission(String name, Type type, int time) {
        this(name, type, time, null, -1, 0, -1, -1);
    }

    public Mission(String name, Type type, int time, Drawable icon, int color, int day, int frequency, int repeat) {
        mName = name;
        mType = type;
        mTime = time;
        mIcon = icon;
        mColor = color;
        mDay = day;
        mFrequency = frequency;
        mRepeat = repeat;
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
        mTime = time;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public int getRepeat() {
        return mRepeat;
    }

    public void setRepeat(int repeat) {
        mRepeat = repeat;
    }

    public int getDay(){
	    return mDay;
    }

    public void setDay(int day){
	    mDay = day;
    }
}
