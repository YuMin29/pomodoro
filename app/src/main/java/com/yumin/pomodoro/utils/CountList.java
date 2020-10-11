package com.yumin.pomodoro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yumin.pomodoro.R;

public class CountList extends LinearLayout {
    private static final String TAG = "[CountList]";
    public CountList(Context context) {
        super(context);
    }

    public CountList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        View.inflate(context, R.layout.count_list_view, this);
    }
}
