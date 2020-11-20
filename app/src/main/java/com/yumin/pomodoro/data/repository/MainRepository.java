package com.yumin.pomodoro.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
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

    public LiveData<List<Mission>> getMissionsByOperate(long start, long end){
        return apiHelper.getMissionsByOperate(start,end);
    }

    public Mission getInitMission(){
        return apiHelper.getInitMission();
    }

    public void addMission(Context context, Mission mission){
        apiHelper.addMission(context,mission);
    }
}
