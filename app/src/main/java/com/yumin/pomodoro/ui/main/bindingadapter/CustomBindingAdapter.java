package com.yumin.pomodoro.ui.main.bindingadapter;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.yumin.pomodoro.ui.view.MissionItemView;

public class CustomBindingAdapter {
    @BindingAdapter("itemContent1")
    public static void setTime(MissionItemView view, int newValue) {
        // Important to break potential infinite loops.
        if (view.getItemContent() != newValue) {
            view.setItemContent(newValue);
        }
    }

    @InverseBindingAdapter(attribute = "itemContent1")
    public static int getTime(MissionItemView view) {
        return view.getItemContent();
    }
}
