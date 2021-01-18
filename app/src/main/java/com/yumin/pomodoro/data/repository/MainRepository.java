package com.yumin.pomodoro.data.repository;

import android.content.Context;
import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class MainRepository {
    private ApiHelper apiHelper;

    public MainRepository(ApiHelper apiHelper){
        this.apiHelper = apiHelper;
    }

    public LiveData<List<Mission>> getMissions(){
        return apiHelper.getMissions();
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

    public Mission getQuickMission(){
        return apiHelper.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<Mission> getMissionById(int id){
        return apiHelper.getMissionById(id);
    }

    public void addMission(Mission mission){
        apiHelper.addMission(mission);
    }

    public void updateMission(Mission mission){
        apiHelper.updateMission(mission);
    }

    public void updateNumberOfCompletionById(int id, int num){
        apiHelper.updateNumberOfCompletionById(id,num);
    }

    public void updateIsFinishedById(int id, boolean finished){
        apiHelper.updateIsFinishedById(id,finished);
    }

    public void deleteMission(Mission mission){
        apiHelper.deleteMission(mission);
    }

    public LiveData<Long> getMissionRepeatStart(int id){
        return apiHelper.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(int id){
        return apiHelper.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(int id){
        return apiHelper.getMissionOperateDay(id);
    }

    public LiveData<List<Mission>> getFinishedMissions(){
        return apiHelper.getFinishedMissions();
    }

    public LiveData<List<Mission>> getUnfinishedMissions(){
        return apiHelper.getUnfinishedMissions();
    }
}
