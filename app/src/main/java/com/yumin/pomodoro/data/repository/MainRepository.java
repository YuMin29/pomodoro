package com.yumin.pomodoro.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class MainRepository {
    private ApiHelper apiHelper;

    public MainRepository(ApiHelper apiHelper){
        this.apiHelper = apiHelper;
    }

    public LiveData<List<Mission>> getMissions(Context context){
        return apiHelper.getMissions(context);
    }

    public LiveData<List<Mission>> getTodayMissions(long start, long end){
        return apiHelper.getTodayMissions(start,end);
    }

    public LiveData<List<Mission>> getComingMissions(long today){
        return apiHelper.getComingMissions(today);
    }

    public Mission getInitMission(){
        return apiHelper.getInitMission();
    }

    public LiveData<Mission> getMissionById(int id){
        return apiHelper.getMissionById(id);
    }

    public void addMission(Context context, Mission mission){
        apiHelper.addMission(context,mission);
    }

    public void updateMission(Mission mission){
        apiHelper.updateMission(mission);
    }
}
