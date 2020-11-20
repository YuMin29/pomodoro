package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

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

    public LiveData<List<Mission>> getMissions(Context context){
        return apiService.getMissions(context);
    }

    public LiveData<List<Mission>> getMissionsByOperate(long start, long end){
        return apiService.getMissionsByOperateDay(start,end);
    }


    public void addMission(Context context, Mission mission){
        apiService.addMission(context,mission);
    }

    public Mission getInitMission(){
        return apiService.getInitMission();
    }
}
