package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.ItemImageviewBinding;

@InverseBindingMethods({@InverseBindingMethod(type = ItemImageView.class,
        attribute = "itemEnable", event = "itemEnableAttrChanged")})
public class ItemImageView extends LinearLayout {
    private ItemImageviewBinding mViewBinding;
    private boolean mIsUseImage = false;
    private boolean mIsEnabled;
    private InverseBindingListener mInverseBindingListener;

    public ItemImageView(Context context) {
        super(context);
        inflateView(context);
    }

    public ItemImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ItemImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.item_imageview,this,true);
        mViewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsUseImage) {
                    if (mIsEnabled) {
                        mIsEnabled = false;
                    } else {
                        mIsEnabled = true;
                    }
                    setItemEnable(mIsEnabled);
                }
        }});
    }

    public void setItemEnable(boolean enabled){
        mIsUseImage = true;
        mIsEnabled = enabled;
        int imgRsc;

        if (enabled) {
            imgRsc = R.drawable.ic_check_circle_black_24dp;
        } else {
            imgRsc = R.drawable.ic_cancel_black_24dp;
        }

        mViewBinding.imageView.setImageResource(imgRsc);
        if (mInverseBindingListener != null)
            mInverseBindingListener.onChange();
    }

    public boolean getItemEnable(){
        return mIsEnabled;
    }

    public void setItemDescription(String string) {
        mViewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemEnableAttrChanged(InverseBindingListener inverseBindingListener){
        mInverseBindingListener = inverseBindingListener;
    }
}