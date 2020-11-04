package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import com.yumin.pomodoro.R;

public class MissionItemView extends LinearLayout {
    private static final String TAG = "[CountView]";

    public MissionItemView(Context context) {
        super(context);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mission_item_view,this,true);
    }

    public interface MissionItemListener {
        public void onAddButtonClick(View view,int position);
        public void onMinusButtonClock(View view,int position);
    }
}
