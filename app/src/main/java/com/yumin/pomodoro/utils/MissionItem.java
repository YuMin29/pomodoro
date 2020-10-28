package com.yumin.pomodoro.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.yumin.pomodoro.R;

public class MissionItem {
    private static String TAG = "[CountViewItem]";
    private String content;
    private String desc;
    private int addButtonVisibility;
    private int minusButtonVisibility;

    public MissionItem(Context context, String content, int descId, int addButtonVisibility, int minusButtonVisibility){
        this.content = content;
        this.desc = context.getString(descId);
        this.addButtonVisibility = addButtonVisibility;
        this.minusButtonVisibility = minusButtonVisibility;
    }

    @BindingAdapter("loadIcon")
    public static void loadIcon(TextView textView, boolean isEmpty){
        LogUtil.logD(TAG,"[loadIcon] isEmpty = "+isEmpty);
        if (isEmpty) {
            Drawable img = ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_check_circle_black_24dp);
            img.setBounds(0, 0, 125, 125);
            textView.setCompoundDrawables(null, img, null, null);
        } else {
            textView.setCompoundDrawables(null, null, null, null);
        }
    }

    public void setContent(String content){
        LogUtil.logD(TAG,"[setCount]");
        this.content = content;
    }

    public boolean isContentEmpty(){
        return this.content.isEmpty();
    }

    public String getContent(){
        return this.content;
    }

    public String getDesc(){
        return desc;
    }

    public int getAddButtonVisibility(){
        return addButtonVisibility;
    }

    public int getMinusButtonVisibility(){
        return minusButtonVisibility;
    }
}
