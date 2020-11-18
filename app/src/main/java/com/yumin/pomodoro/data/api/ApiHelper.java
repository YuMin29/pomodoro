package com.yumin.pomodoro.data.api;

import android.content.Context;

import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class ApiHelper {
    private ApiService apiService;
    private Context context;

    public ApiHelper(ApiService apiService,Context context){
        this.apiService = apiService;
        this.context = context;
    }

    public List<Mission> getMissions(){
        return apiService.getMissions();
    }

    public void addMission(Mission mission){
        apiService.addMission(mission);
    }

    public Mission getInitMission(){
        return apiService.getInitMission();
    }
}
