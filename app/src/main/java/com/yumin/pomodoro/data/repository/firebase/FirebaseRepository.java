package com.yumin.pomodoro.data.repository.firebase;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.api.ApiService;

import java.util.List;

public class FirebaseRepository {
    private FirebaseApiServiceImpl firebaseRepository;

    public FirebaseRepository(ApiService apiService){
        this.firebaseRepository = (FirebaseApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return firebaseRepository.getMissions();
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


    public UserMission getInitMission(){
        return firebaseRepository.getInitMission();
    }

    public UserMission getQuickMission(){
        return firebaseRepository.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return firebaseRepository.getMissionById(id);
    }

    public void addMission(UserMission mission){
        firebaseRepository.addMission(mission);
    }

    public void updateMission(UserMission mission){
        firebaseRepository.updateMission(mission);
    }

    public void updateNumberOfCompletionById(String id, int num){
        firebaseRepository.updateNumberOfCompletionById(id,num);
    }

    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber){
        firebaseRepository.updateIsFinishedById(id,finished,completeOfNumber);
    }

    public void deleteMission(UserMission mission){
        firebaseRepository.deleteMission(mission);
    }

    public LiveData<Long> getMissionRepeatStart(String id){
        return firebaseRepository.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return firebaseRepository.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(String id){
        return firebaseRepository.getMissionOperateDay(id);
    }

//    public LiveData<List<UserMission>> getFinishedMissions(long start, long end){
//        return firebaseRepository.getFinishedMissions(start, end);
//    }
//
//    public LiveData<List<UserMission>> getUnfinishedMissions(long start, long end){
//        return firebaseRepository.getUnFinishedMissions(start, end);
//    }
}
