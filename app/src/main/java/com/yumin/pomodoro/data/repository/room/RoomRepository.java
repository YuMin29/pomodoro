package com.yumin.pomodoro.data.repository.room;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.UserMission;

import java.util.List;

public class RoomRepository {
    private RoomApiServiceImpl apiService;

    public RoomRepository(ApiService apiService){
        this.apiService = (RoomApiServiceImpl) apiService;
    }

    public LiveData<List<Mission>> getMissions(){
        return apiService.getMissions();
    }

    public LiveData<List<Mission>> getTodayMissions(long start, long end){
        return apiService.getTodayMissionsByOperateDay(start,end);
    }

    public LiveData<List<Mission>> getComingMissions(long today){
        return apiService.getComingMissionsByOperateDay(today);
    }

    public Mission getInitMission(){
        return apiService.getInitMission();
    }

    public Mission getQuickMission(){
        return apiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<Mission> getMissionById(int id){
        return apiService.getMissionById(id);
    }

    public void addMission(Mission mission){
        apiService.addMission(mission);
        new FirebaseApiServiceImpl().addMission(new UserMission(mission.getTime(),mission.getShortBreakTime(),mission.getColor()));
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
