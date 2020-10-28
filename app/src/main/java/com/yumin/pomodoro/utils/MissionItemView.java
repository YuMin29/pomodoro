package com.yumin.pomodoro.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.MissionItemViewBindingImpl;

public class MissionItemView extends LinearLayout {
    private static final String TAG = "[CountView]";
    MissionItemViewBindingImpl mMissionItemViewBindingImpl;

    public MissionItemView(Context context) {
        super(context);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMissionItemViewBindingImpl = DataBindingUtil.inflate(inflater, R.layout.mission_item_view,this,true);
    }

    public interface MissionItemListener {
        public void onAddButtonClick(View view,int position);
        public void onMinusButtonClock(View view,int position);
    }
}
