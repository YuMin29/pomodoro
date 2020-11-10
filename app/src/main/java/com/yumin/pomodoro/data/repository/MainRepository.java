package com.yumin.pomodoro.data.repository;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class MainRepository {
    private ApiHelper apiHelper;

    public MainRepository(ApiHelper apiHelper){
        this.apiHelper = apiHelper;
    }

    public List<Mission> getMissions(){
        return apiHelper.getMissions();
    }

    public List<AdjustMissionItem> getAdjustMissionItems(){
        return apiHelper.getAdjustMissionItems();
    }

    public Mission getInitMission(){
        return apiHelper.getInitMission();
    }

    public void addMission(Mission mission){
        apiHelper.addMission(mission);
    }
}
