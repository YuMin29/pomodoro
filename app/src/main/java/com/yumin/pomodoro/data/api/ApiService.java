package com.yumin.pomodoro.data.api;

import android.content.Context;

import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public interface ApiService {
    public List<Mission> getMissions();
    public List<AdjustMissionItem> getAdjustMissionItems(Context context);
    public void addMission(Mission mission);
    public Mission getInitMission();
}
