package com.yumin.pomodoro.data.repository.firebase;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.ApiService;

import java.util.List;

public class FirebaseRepository {

    private FirebaseApiServiceImpl mFirebaseApiService;

    public FirebaseRepository(ApiService apiService){
        mFirebaseApiService = (FirebaseApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return mFirebaseApiService.getMissions();
    }

    public UserMission getInitMission(){
        return mFirebaseApiService.getInitMission();
    }

    public UserMission getQuickMission(){
        return mFirebaseApiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return mFirebaseApiService.getMissionById(id);
    }

    public String addMission(UserMission mission){
        return mFirebaseApiService.addMission(mission);
    }

    public void updateMission(UserMission mission){
        mFirebaseApiService.updateMission(mission);
    }

    public void updateMissionNumberOfCompletion(String id, int num){
        mFirebaseApiService.updateNumberOfCompletionById(id,num);
    }

    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber){
        mFirebaseApiService.updateMissionState(id,finished,completeOfNumber);
    }

    public void deleteMission(UserMission mission){
        mFirebaseApiService.deleteMission(mission);
    }


    public void deleteAllMission() {
        mFirebaseApiService.deleteAllMission();
    }

    public LiveData<Long> getMissionRepeatStart(String id){
        return mFirebaseApiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return mFirebaseApiService.getMissionRepeatEnd(id);
    }


    public void initMissionState(String id) {
        mFirebaseApiService.initMissionState(id);
    }


    public LiveData<List<UserMission>> getCompletedMissionList(long start, long end) {
        return mFirebaseApiService.getCompletedMissionList(start, end);
    }


    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return mFirebaseApiService.getNumberOfCompletionById(id,todayStart);
    }


    public LiveData<MissionState> getMissionStateByToday(String id, long todayStart) {
        return mFirebaseApiService.getMissionStateByToday(id, todayStart);
    }


    public void saveMissionState(String missionId,MissionState missionState) {
        mFirebaseApiService.saveMissionState(missionId,missionState);
    }


    public LiveData<List<MissionState>> getMissionStateList() {
        return mFirebaseApiService.getMissionStateList();
    }


    public LiveData<List<UserMission>> getPastCompletedMission(long today) {
        return null;
    }
}
