package com.yumin.pomodoro.data.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.utils.LogUtil;

public class AdjustMissionItem {
    private static String TAG = "[CountViewItem]";
    private String mContent;
    private String mDescription;
    private int mAddButtonVisibility;
    private int mMinusButtonVisibility;
    private boolean showIcon;
    private boolean enabledIcon;
    private AdjustItem adjustItem;
    public enum AdjustItem{
        TIME,LONG_BREAK,SHORT_BREAK,GOAL,REPEAT,OPERATE_DAY,COLOR,NOTIFICATION,SOUND,VOLUME, VIBRATE,SCREEN_ON
    }

    public AdjustMissionItem(Context context, String mContent, int descId, int mAddButtonVisibility
            , int mMinusButtonVisibility, AdjustItem adjustItem, boolean showIcon, boolean enabledIcon){
        this.mContent = mContent;
        this.mDescription = context.getString(descId);
        this.mAddButtonVisibility = mAddButtonVisibility;
        this.mMinusButtonVisibility = mMinusButtonVisibility;
        this.adjustItem = adjustItem;
        this.showIcon = showIcon;
        this.enabledIcon = enabledIcon;
    }

    public void setContent(String mContent){
        LogUtil.logD(TAG,"[setCount]");
        if (!showIcon)
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

    public boolean getShowIcon(){
        return this.showIcon;
    }

    public boolean getEnabledIcon(){
        return this.enabledIcon;
    }

    public void setEnabledIcon(boolean enabled) {
        this.enabledIcon = enabled;
    }

    @BindingAdapter(value = {"showIcon","enabledIcon"}, requireAll = false)
    public static void loadIcon(TextView textView,boolean showIcon,boolean enabledIcon){
        LogUtil.logD(TAG,"[loadIcon] showIcon = "+showIcon +" ,enabledIcon = "+enabledIcon);
        if (showIcon) {
            if (enabledIcon) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_check_circle_black_24dp,0,0);
            } else if (!enabledIcon) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_cancel_black_24dp,0,0);
            }
        }
    }
}
