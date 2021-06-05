package com.yumin.pomodoro.ui.view.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.utils.LogUtil;

public class CustomImageButton extends LinearLayout {
    private static final String TAG = CustomImageButton.class.getSimpleName();
    private TextView mTextView;
    private ImageButton mImageButton;
    private boolean mTintEnabled = false;

    public CustomImageButton(Context context) {
        super(context);
    }

    public CustomImageButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attributeSet){
        View.inflate(context, R.layout.custom_imagebutton, this);
        int[] set = {
                android.R.attr.src, // idx 0
                android.R.attr.text // idx 1
        };
        TypedArray a = context.obtainStyledAttributes(attributeSet, set);
        int resid = a.getResourceId(0,R.drawable.ic_baseline_mood_24);
        Drawable d = a.getDrawable(0);
        @SuppressLint("ResourceType") CharSequence t = a.getText(1);
        LogUtil.logD(TAG, "attrs " + d + " " + t);
        a.recycle();
        mImageButton = findViewById(R.id.custom_image_button);
        mImageButton.setBackgroundResource(resid);
        mTextView = findViewById(R.id.custom_text_view);
        mTextView.setText(t);
    }

    public boolean isTintEnabled(){
        return mTintEnabled;
    }

    public void setTint(boolean enabled){
        if (enabled) {
            mTintEnabled = true;
            mImageButton.setColorFilter(Color.parseColor("#F77062"));
            mTextView.setTextColor(Color.parseColor("#F77062"));
        } else {
            mTintEnabled = false;
            mImageButton.setColorFilter(null);
            mTextView.setTextColor(Color.TRANSPARENT);
        }
    }
}
