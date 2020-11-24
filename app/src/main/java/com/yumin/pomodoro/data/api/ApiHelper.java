package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class ApiHelper {
    private ApiService apiService;
    private Context context;

    public ApiHelper(ApiService apiService,Context context){
        this.apiService = apiService;
        this.context = context;
    }

    public LiveData<List<Mission>> getMissions(Context context){
        return apiService.getMissions(context);
    }

    public LiveData<List<Mission>> getTodayMissions(long start, long end){
        return apiService.getTodayMissions(start,end);
    }

    public LiveData<List<Mission>> getComingMissions(long today){
        return apiService.getComingMissions(today);
    }


    public void addMission(Context context, Mission mission){
        apiService.addMission(context,mission);
    }

    public Mission getInitMission(){
        return apiService.getInitMission();
    }

    public LiveData<Mission> getMissionById(int id){
        return apiService.getMissionById(id);
    }

    public void updateMission(Mission mission){
        apiService.updateMission(mission);
    }
}
