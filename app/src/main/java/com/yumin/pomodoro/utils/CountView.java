package com.yumin.pomodoro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.CountViewBindingImpl;

public class CountView extends LinearLayout {
    private static final String TAG = "[CountView]";
    private final int DEFAULT_BUTTON_GONE_VAL = View.GONE;
    private final int DEFAULT_BUTTON_VISIBLE_VAL = View.VISIBLE;
    private int mCount;
    private String mDescription;
    private TextView mDescriptionView;
    private TextView mNumView;
    public Button mAddButton;
    public Button mMinusButton;
    CountViewBindingImpl mCountViewBinding;

    public CountView(Context context) {
        super(context);
        inflateCountView(context);
    }

    private void inflateCountView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCountViewBinding = DataBindingUtil.inflate(inflater, R.layout.count_view,this,true);
        mAddButton = mCountViewBinding.addNum;
        mAddButton = mCountViewBinding.minusNum;
    }

    public void setViewModel(){

    }

    public interface CountViewListener{
        public void onAddButtonClick(View view,int position);
        public void onMinusButtonClock(View view,int position);
    }

    public interface CountViewModel{

    }
}
