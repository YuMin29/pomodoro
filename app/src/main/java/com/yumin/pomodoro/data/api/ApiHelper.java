package com.yumin.pomodoro.data.api;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.model.Mission;

import java.util.List;

public class ApiHelper {
    private ApiService apiService;

    public ApiHelper(ApiService apiService){
        this.apiService = apiService;
    }

    public LiveData<List<Mission>> getMissions(){
        return apiService.getMissions();
    }

    public LiveData<List<Mission>> getTodayMissions(long start, long end){
        return apiService.getTodayMissions(start,end);
    }

    public LiveData<List<Mission>> getComingMissions(long today){
        return apiService.getComingMissions(today);
    }


    public void addMission(Mission mission){
        apiService.addMission(mission);
    }

    public Mission getInitMission(){
        return apiService.getInitMission();
    }

    public Mission getQuickMission(int time,int shortBreakTime,int color){
        return apiService.getQuickMission(time,shortBreakTime,color);
    }

    public LiveData<Mission> getMissionById(int id){
        return apiService.getMissionById(id);
    }

    public void updateMission(Mission mission){
        apiService.updateMission(mission);
    }

    public void updateNumberOfCompletionById(int id, int num){
        apiService.updateNumberOfCompletionById(id,num);
    }

    public void updateIsFinishedById(int id, boolean finished){
        apiService.updateIsFinishedById(id,finished);
    }

    public void deleteMission(Mission mission){
        apiService.deleteMission(mission);
    }

    public LiveData<Long> getMissionRepeatStart(int id){
        return apiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(int id){
        return apiService.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(int id){
        return apiService.getMissionOperateDay(id);
    }

    public LiveData<List<Mission>> getFinishedMissions(){
        return apiService.getFinishedMissions();
    }


    public LiveData<List<Mission>> getUnfinishedMissions(){
        return apiService.getUnFinishedMissions();
    }
}
