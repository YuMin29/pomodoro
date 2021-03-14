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

    public String addMission(UserMission mission){
        return firebaseApiService.addMission(mission);
    }

    public void updateMission(UserMission mission){
        firebaseApiService.updateMission(mission);
    }

    public void updateMissionNumberOfCompletion(String id, int num){
        firebaseApiService.updateNumberOfCompletionById(id,num);
    }

    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber){
        firebaseApiService.updateMissionFinishedState(id,finished,completeOfNumber);
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
        firebaseApiService.initMissionState(id);
    }

    @Override
    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end) {
        return firebaseApiService.getFinishedMissionList(start, end);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return firebaseApiService.getNumberOfCompletionById(id,todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        return firebaseApiService.getMissionStateById(id, todayStart);
    }

    @Override
    public void saveMissionState(String missionId,MissionState missionState) {
        firebaseApiService.saveMissionState(missionId,missionState);
    }

    @Override
    public LiveData<List<MissionState>> getMissionStates() {
        return null;
    }
}
