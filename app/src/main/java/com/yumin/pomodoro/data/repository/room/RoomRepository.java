package com.yumin.pomodoro.data.repository.room;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.api.DataRepository;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class RoomRepository implements DataRepository {
    private static final String TAG = "[RoomRepository]";
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

    public String addMission(UserMission userMission){
        return roomApiService.addMission(userMission);
    }

    public void updateMission(UserMission userMission){
        roomApiService.updateMission(userMission);
    }

    @Override
    public void updateMissionNumberOfCompletion(String id, int num) {
        roomApiService.updateNumberOfCompletionById(id,num);
    }

    @Override
    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber) {
        roomApiService.updateMissionFinishedState(id,finished,completeOfNumber);
    }


    public void deleteMission(UserMission userMission){
        roomApiService.deleteMission(userMission);
    }

    @Override
    public void deleteAllMission() {
        roomApiService.deleteAllMission();
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

    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end){
        return roomApiService.getFinishedMissionList(start, end);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return roomApiService.getNumberOfCompletionById(id,todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        LogUtil.logE(TAG,"[getMissionStateById] ID = "+id+" , TODAYSTART = "+todayStart);
        return roomApiService.getMissionStateById(id,todayStart);
    }

    @Override
    public void saveMissionState(String missionId,MissionState missionState) {
        roomApiService.saveMissionState(missionId,missionState);
    }

    @Override
    public LiveData<List<MissionState>> getMissionStateList() {
        return roomApiService.getMissionStateList();
    }

    @Override
    public LiveData<List<UserMission>> getPastFinishedMission(long today) {
        return roomApiService.getPastFinishedMission(today);
    }
}
