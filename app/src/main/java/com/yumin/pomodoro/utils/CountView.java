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
    private Button mAddButton;
    private Button mMinusButton;
    CountViewBindingImpl mCountViewBinding;

    public CountView(Context context,int addNumVisibility,int minusNumVisibility,String numText,int descTextResId){
        this(context);
        setAddButtonVisibility(addNumVisibility);
        setMinusButtonVisibility(minusNumVisibility);
        setCountText(numText);
        setDescriptionText(descTextResId);
    }

    public CountView(Context context) {
        super(context);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        inflateCountView(context);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CountView);
        LogUtil.logD(TAG, "attr length: " + typedArray.length());
        @SuppressLint("num") CharSequence num = typedArray.getText(R.styleable.CountView_android_text);
        LogUtil.logD(TAG, "attr num: " + num);
        setCountText(num.toString());
        mCount = Integer.valueOf(num.toString());

        @SuppressLint("Description") CharSequence des = typedArray.getText(R.styleable.CountView_android_description);
        LogUtil.logD(TAG, "attrs des:" + des);
        setDescriptionText(des.toString());

        @SuppressLint("Add") int addVisibility = typedArray.getInt(R.styleable.CountView_add_button_visibility, DEFAULT_BUTTON_GONE_VAL);
        LogUtil.logD(TAG, "addVisibility " + addVisibility);
        setAddButtonVisibility(addVisibility);

        @SuppressLint("Minus") int minusVisibility = typedArray.getInt(R.styleable.CountView_minus_button_visibility, DEFAULT_BUTTON_GONE_VAL);
        LogUtil.logD(TAG, "minusVisibility " + minusVisibility);
        setMinusButtonVisibility(minusVisibility);
        typedArray.recycle();
    }

    private void inflateCountView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCountViewBinding = DataBindingUtil.inflate(inflater, R.layout.count_view,this,true);
    }

    private void setCountText(String num) {
        mNumView = findViewById(R.id.num_textview);
        mNumView.setText(String.valueOf(num));
    }

    private void setDescriptionText(String des) {
        mDescriptionView = findViewById(R.id.description_textview);
        mDescriptionView.setText(des);
    }

    private void setDescriptionText(int des) {
        mDescriptionView = findViewById(R.id.description_textview);
        mDescriptionView.setText(des);
    }


    private void setAddButtonVisibility(int addVisibility) {
        mAddButton = findViewById(R.id.add_num);
        mAddButton.setVisibility(addVisibility);
    }

    private void setMinusButtonVisibility(int minusVisibility) {
        mMinusButton = findViewById(R.id.minus_num);
        mMinusButton.setVisibility(minusVisibility);
    }

    public int getCount(){
        return mCount;
    }

    public void setShowNumText(String num){
        mNumView.setText(num);
    }

    public void setListener(CountViewListener countViewListener) {
        mAddButton.setOnClickListener(countViewListener.onAddButtonClick());
        mMinusButton.setOnClickListener(countViewListener.onMinusButtonClock());
    }

    public interface CountViewListener{
        public View.OnClickListener onAddButtonClick();
        public View.OnClickListener onMinusButtonClock();
    }

    public interface CountViewModel{

    }
}
