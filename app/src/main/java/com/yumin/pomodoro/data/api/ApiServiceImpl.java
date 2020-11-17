package com.yumin.pomodoro.data.api;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;

import java.util.ArrayList;
import java.util.List;

public class ApiServiceImpl implements ApiService {
    private List<Mission> missions;

    @Override
    public List<Mission> getMissions() {
        Log.d("[ApiServiceImpl]","[getMissions]");
        if (missions == null)
            missions = new ArrayList<>();
        return missions;
    }

    @Override
    public List<AdjustMissionItem> getAdjustMissionItems(Context context) {
        List<AdjustMissionItem> adjustMissionItems = new ArrayList<>();
        adjustMissionItems.add(new AdjustMissionItem(context, "25", R.string.mission_time, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.TIME,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context, "5", R.string.mission_break, View.VISIBLE, View.VISIBLE,AdjustMissionItem.AdjustItem.SHORT_BREAK,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context, "15", R.string.mission_long_break, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.LONG_BREAK,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context, "0", R.string.mission_goal, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.GOAL,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context, "0", R.string.mission_repeat, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.REPEAT,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context,"今天",R.string.mission_operate_day, View.GONE, View.GONE,AdjustMissionItem.AdjustItem.OPERATE_DAY,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context,"藍",R.string.mission_theme, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.COLOR,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context,"",R.string.mission_notification, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.NOTIFICATION,true,true));
        adjustMissionItems.add(new AdjustMissionItem(context,"",R.string.mission_sound, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.SOUND,true,true));
        adjustMissionItems.add(new AdjustMissionItem(context,"",R.string.mission_sound_level, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.VOLUME,false,false));
        adjustMissionItems.add(new AdjustMissionItem(context,"",R.string.mission_vibrate, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.VIBRATE,true,true));
        adjustMissionItems.add(new AdjustMissionItem(context,"",R.string.mission_keep_awake, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.SCREEN_ON,true,true));
        return adjustMissionItems;
    }

    @Override
    public void addMission(Mission mission) {
        if (missions == null)
            missions = new ArrayList<>();
        missions.add(mission);
    }

    @Override
    public Mission getInitMission() {
        return new Mission();
    }

}
