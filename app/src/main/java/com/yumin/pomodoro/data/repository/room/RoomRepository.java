package com.yumin.pomodoro.data.repository.room;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.firebase.UserMission;

import java.util.List;

public class RoomRepository {
    private RoomApiServiceImpl apiService;

    public RoomRepository(ApiService apiService){
        this.apiService = (RoomApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return apiService.getMissions();
    }

    public LiveData<List<UserMission>> getTodayMissions(long start, long end){
        return apiService.getTodayMissionsByOperateDay(start,end);
    }

    public LiveData<List<UserMission>> getComingMissions(long today){
        return apiService.getComingMissionsByOperateDay(today);
    }

    public UserMission getInitMission(){
        return apiService.getInitMission();
    }

    public UserMission getQuickMission(){
        return apiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return apiService.getMissionById(id);
    }

    public void addMission(UserMission userMission){
        apiService.addMission(userMission);
//        new FirebaseApiServiceImpl().addMission(new UserMission(userMission.getTime(),userMission.getShortBreakTime(),userMission.getColor()));
    }

    public void updateMission(UserMission userMission){
        apiService.updateMission(userMission);
    }

    public void updateNumberOfCompletionById(int id, int num){
        apiService.updateNumberOfCompletionById(id,num);
    }

    public void updateIsFinishedById(int id, boolean finished){
        apiService.updateIsFinishedById(id,finished);
    }

    public void deleteMission(UserMission userMission){
        apiService.deleteMission(userMission);
    }

    public LiveData<Long> getMissionRepeatStart(String id){
        return apiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return apiService.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(String id){
        return apiService.getMissionOperateDay(id);
    }

    public LiveData<List<UserMission>> getFinishedMissions(){
        return apiService.getFinishedMissions();
    }

    public LiveData<List<UserMission>> getUnfinishedMissions(){
        return apiService.getUnFinishedMissions();
    }
}
