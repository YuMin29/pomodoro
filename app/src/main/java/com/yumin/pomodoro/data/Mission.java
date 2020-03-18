package com.yumin.pomodoro.data;

import android.graphics.drawable.Drawable;

import java.util.List;


public class Mission {
    protected String mName;
    protected int mType;
    private Drawable mIcon;
    protected int mRepeatType;
    protected int mTime;
    private int mColor;
    private List<SubMission> mSubMissions;
    private List<SubMission> mSavedSubMissions;

    public Mission(String name, int type, int time, int color, Drawable icon, int repeatType){
        mName = name;
        mType = type;
        mTime = time;
        mColor = color;
        mIcon = icon;
        repeatType = repeatType;
    }


    public Mission(String name) {
        mName = name;
    }
}
