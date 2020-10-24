package com.yumin.pomodoro.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.CountViewBinding;
import com.yumin.pomodoro.databinding.CountViewBindingImpl;

public class CountViewItem {
    private String count;
    private String desc;
    private int addButtonVisibility;
    private int minusButtonVisibility;
    CountViewBindingImpl countViewBinding;

    public CountViewItem(Context context, String count, int descId, int addButtonVisibility, int minusButtonVisibility){
        this.count = count;
        this.desc = context.getString(descId);
        this.addButtonVisibility = addButtonVisibility;
        this.minusButtonVisibility = minusButtonVisibility;
    }

    public String getCount(){
        return count;
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
