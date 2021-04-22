package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
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
    private static final String TAG = "[ItemImageView]";
    private ItemImageviewBinding viewBinding;
    private boolean isUseImage = false;
    private boolean isEnabled;
    private InverseBindingListener inverseBindingListener;

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
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.item_imageview,this,true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUseImage) {
                    // switch icon when click button
                    if (isEnabled) {
                        isEnabled = false;
                    } else {
                        isEnabled = true;
                    }
                    setItemEnable(isEnabled);
                }
        }});
    }

    public void setItemEnable(boolean enabled){
        isUseImage = true;
        isEnabled = enabled;
        int imgRsc;

        if (enabled) {
            imgRsc = R.drawable.ic_check_circle_black_24dp;
        } else {
            imgRsc = R.drawable.ic_cancel_black_24dp;
        }

        viewBinding.imageView.setImageResource(imgRsc);
        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
    }

    public boolean getItemEnable(){
        return isEnabled;
    }

    public void setItemDescription(String string) {
        viewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemEnableAttrChanged(InverseBindingListener inverseBindingListener){
        this.inverseBindingListener = inverseBindingListener;
    }
}