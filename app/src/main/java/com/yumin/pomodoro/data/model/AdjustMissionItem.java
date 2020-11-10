package com.yumin.pomodoro.data.model;

import android.content.Context;

import com.yumin.pomodoro.utils.LogUtil;

public class AdjustMissionItem {
    private static String TAG = "[CountViewItem]";
    private String mContent;
    private String mDescription;
    private int mAddButtonVisibility;
    private int mMinusButtonVisibility;
    private boolean enabled;
    private AdjustItem adjustItem;
    public enum AdjustItem{
        TIME,LONG_BREAK,SHORT_BREAK,GOAL,REPEAT,OPERATE_DAY,COLOR,NOTIFICATION,SOUND,VOLUME, VIBRATE,SCREEN_ON
    }

    public AdjustMissionItem(Context context, String mContent, int descId, int mAddButtonVisibility
            , int mMinusButtonVisibility, AdjustItem adjustItem){
        this.mContent = mContent;
        this.mDescription = context.getString(descId);
        this.mAddButtonVisibility = mAddButtonVisibility;
        this.mMinusButtonVisibility = mMinusButtonVisibility;
        this.adjustItem = adjustItem;
    }

    public void setContent(String mContent){
        LogUtil.logD(TAG,"[setCount]");
        this.mContent = mContent;
    }

    public String getContent(){
        return this.mContent;
    }

    public String getDesc(){
        return mDescription;
    }

    public int getAddButtonVisibility(){
        return mAddButtonVisibility;
    }

    public int getMinusButtonVisibility(){
        return mMinusButtonVisibility;
    }

    public AdjustItem getAdjustItem(){
        return this.adjustItem;
    }
}
