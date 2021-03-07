package com.yumin.pomodoro.data.repository.firebase;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.api.DataRepository;

import java.util.List;

public class FirebaseRepository implements DataRepository {

    private FirebaseApiServiceImpl firebaseApiService;

    public FirebaseRepository(ApiService apiService){
        this.firebaseApiService = (FirebaseApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return firebaseApiService.getMissions();
    }

    public UserMission getInitMission(){
        return firebaseApiService.getInitMission();
    }

    public UserMission getQuickMission(){
        return firebaseApiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return firebaseApiService.getMissionById(id);
    }

    public void addMission(UserMission mission){
        firebaseApiService.addMission(mission);
    }

    public void updateMission(UserMission mission){
        firebaseApiService.updateMission(mission);
    }

    public void updateNumberOfCompletionById(String id, int num){
        firebaseApiService.updateNumberOfCompletionById(id,num);
    }

    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber){
        firebaseApiService.updateIsFinishedById(id,finished,completeOfNumber);
    }

    public void deleteMission(UserMission mission){
        firebaseApiService.deleteMission(mission);
    }

    public LiveData<Long> getMissionRepeatStart(String id){
        return firebaseApiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return firebaseApiService.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(String id){
        return firebaseApiService.getMissionOperateDay(id);
    }

    @Override
    public void initMissionState(String id) {

    }

    @Override
    public LiveData<List<UserMission>> getFinishedMissions(long start, long end) {
        return null;
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return null;
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        return null;
    }

//    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end){
//        return firebaseRepository.getTodayMissionsByOperateDay(start,end);
//    }
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end){
//        return firebaseRepository.getTodayMissionsByRepeatType(start,end);
//    }
//
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end){
//        return firebaseRepository.getTodayMissionsByRepeatRange(start,end);
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today){
//        return firebaseRepository.getComingMissionsByOperateDay(today);
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today){
//        return firebaseRepository.getComingMissionsByRepeatType(today);
//    }
//
//    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today){
//        return firebaseRepository.getComingMissionsByRepeatRange(today);
//    }
//
//    public LiveData<List<UserMission>> getFinishedMissions(long start, long end){
//        return firebaseRepository.getFinishedMissions(start, end);
//    }
//
//    public LiveData<List<UserMission>> getUnfinishedMissions(long start, long end){
//        return firebaseRepository.getUnFinishedMissions(start, end);
//    }
}
