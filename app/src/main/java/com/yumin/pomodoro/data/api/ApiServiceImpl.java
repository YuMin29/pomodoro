package com.yumin.pomodoro.data.api;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ApiServiceImpl implements ApiService {
    private static final String TAG = "[ApiServiceImpl]";
    private List<Mission> missions;

    @Override
    public List<Mission> getMissions() {
        Log.d("[ApiServiceImpl]","[getMissions]");
        if (missions == null)
            missions = new ArrayList<>();
        return missions;
    }

    @Override
    public void addMission(Mission mission) {
        if (missions == null)
            missions = new ArrayList<>();
        missions.add(mission);
        LogUtil.logD(TAG,"[addMission] mission size = "+missions.size());
    }

    @Override
    public Mission getInitMission() {
        return new Mission();
    }

}
