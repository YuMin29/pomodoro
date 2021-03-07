package com.yumin.pomodoro.data.repository.room;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.UserMission;

import java.util.List;

public class RoomRepository implements DataRepository {
    private RoomApiServiceImpl roomApiService;

    public RoomRepository(ApiService apiService){
        this.roomApiService = (RoomApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return roomApiService.getMissions();
    }

    public UserMission getInitMission(){
        return roomApiService.getInitMission();
    }

    public UserMission getQuickMission(){
        return roomApiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return roomApiService.getMissionById(id);
    }

    public void addMission(UserMission userMission){
        roomApiService.addMission(userMission);
    }

    public void updateMission(UserMission userMission){
        roomApiService.updateMission(userMission);
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
        roomApiService.updateNumberOfCompletionById(id,num);
    }

    @Override
    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber) {
        roomApiService.updateIsFinishedById(id,finished,completeOfNumber);
    }


    public void deleteMission(UserMission userMission){
        roomApiService.deleteMission(userMission);
    }

    public LiveData<Long> getMissionRepeatStart(String id){
        return roomApiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return roomApiService.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(String id){
        return roomApiService.getMissionOperateDay(id);
    }

    @Override
    public void initMissionState(String id) {
        roomApiService.initMissionState(id);
    }

    //    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end) {
//        return null;
//    }
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
//        return null;
//    }
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end) {
//        return null;
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today) {
//        return null;
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today) {
//        return null;
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
//        return null;
//    }
//
    public LiveData<List<UserMission>> getFinishedMissions(long start, long end){
        return roomApiService.getFinishedMissions(start, end);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return roomApiService.getNumberOfCompletionById(id,todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        return roomApiService.getMissionStateById(id,todayStart);
    }

//    public LiveData<List<UserMission>> getUnfinishedMissions(long start, long end){
//        return roomApiService.getUnFinishedMissions(start, end);
//    }
}
