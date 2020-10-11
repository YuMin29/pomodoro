package com.yumin.pomodoro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yumin.pomodoro.R;

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

    public CountView(Context context) {
        super(context);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

<<<<<<< HEAD
    private void init(Context context,AttributeSet attributeSet) {
        View.inflate(context, R.layout.count_view, this);
        int[] set = {
                android.R.attr.text,                        // idx 0
                android.R.attr.contentDescription,          // idx 1
                R.styleable.CountView_add_num_visibility,   // idx 2
                R.styleable.CountView_minus_num_visibility  // idx 3
        };
        TypedArray a = context.obtainStyledAttributes(attributeSet, set);
        @SuppressLint("num") CharSequence num = a.getText(0);
=======
    public CountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attributeSet) {
        View.inflate(context, R.layout.count_view, this);
//        int[] set = {
//                R.styleable.CountView_add_num_visibility,   // idx 0
//                R.styleable.CountView_minus_num, // idx 1
//                android.R.attr.text,                        // idx 2
//                android.R.attr.contentDescription          // idx 3
//        };
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CountView);
        LogUtil.logD(TAG, "attr length: "  +typedArray.length());
        @SuppressLint("num") CharSequence num = typedArray.getText(R.styleable.CountView_android_text);
        LogUtil.logD(TAG, "attr num: "  +num);
>>>>>>> d7e87be... tmp
        mNumView = findViewById(R.id.num_textview);
        mNumView.setText(String.valueOf(num));
        setCount(Integer.valueOf(String.valueOf(num)));

<<<<<<< HEAD
        @SuppressLint("Description") CharSequence des = a.getText(1);
        LogUtil.logD(TAG, "attrs "  + des);
        mDescriptionView = findViewById(R.id.description_textview);
        mDescriptionView.setText(des);

        @SuppressLint("Add") Integer addVisibility = a.getInt(2, DEFAULT_BUTTON_GONE_VAL);
        LogUtil.logD(TAG, "attrs "  + addVisibility);
        mAddButton = findViewById(R.id.add_num);
        mAddButton.setVisibility(addVisibility);

        @SuppressLint("Minus") Integer minusVisibility = a.getInt(3, DEFAULT_BUTTON_GONE_VAL);
        mMinusButton = findViewById(R.id.minus_num);
        mMinusButton.setVisibility(minusVisibility);
        a.recycle();
=======
        @SuppressLint("Description") CharSequence des = typedArray.getText(R.styleable.CountView_android_description);
        LogUtil.logD(TAG, "attrs des:"  + des);
        mDescriptionView = findViewById(R.id.description_textview);
        mDescriptionView.setText(des);

        @SuppressLint("Add") int addVisibility = typedArray.getInt(R.styleable.CountView_add_num_visibility,8);
        LogUtil.logD(TAG, "addVisibility "  + addVisibility);
        mAddButton = findViewById(R.id.add_num);
        mAddButton.setVisibility(addVisibility);

        @SuppressLint("Minus") int minusVisibility = typedArray.getInt(R.styleable.CountView_minus_num,8);
        LogUtil.logD(TAG, "minusVisibility "  + minusVisibility);
        mMinusButton = findViewById(R.id.minus_num);
        mMinusButton.setVisibility(minusVisibility);
        typedArray.recycle();
>>>>>>> d7e87be... tmp
    }

    public void setCount(int count) {
        mCount = count;
        mNumView.setText(String.valueOf(count));
    }

    public int getCount() {
        return mCount;
    }
}
