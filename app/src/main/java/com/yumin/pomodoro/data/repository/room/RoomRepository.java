package com.yumin.pomodoro.data.repository.room;

import android.graphics.Color;

import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class RoomRepository {
    private static final String TAG = RoomRepository.class.getSimpleName();
    private RoomApiServiceImpl mRoomApiService;

    public RoomRepository(ApiService apiService){
        mRoomApiService = (RoomApiServiceImpl) apiService;
    }

    public LiveData<List<UserMission>> getMissions(){
        return mRoomApiService.getMissions();
    }

    public UserMission getInitMission(){
        return mRoomApiService.getInitMission();
    }

    public UserMission getQuickMission(){
        return mRoomApiService.getQuickMission(25,5, Color.parseColor("#e57373"));
    }

    public LiveData<UserMission> getMissionById(String id){
        return mRoomApiService.getMissionById(id);
    }

    public void addMission(UserMission userMission){
        mRoomApiService.addMission(userMission);
    }

    public Single<String> addMissionAndGetId(UserMission userMission){
        return mRoomApiService.addMissionAndGetId(userMission);
    }

    public void updateMission(UserMission userMission){
        mRoomApiService.updateMission(userMission);
    }

    public void updateMissionNumberOfCompletion(String id, int num) {
        mRoomApiService.updateNumberOfCompletionById(id,num);
    }

    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber) {
        mRoomApiService.updateMissionState(id,finished,completeOfNumber);
    }


    public void deleteMission(UserMission userMission){
        mRoomApiService.deleteMission(userMission);
    }

    public void deleteAllMission() {
        mRoomApiService.deleteAllMission();
    }


    public LiveData<Long> getMissionRepeatStart(String id){
        return mRoomApiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return mRoomApiService.getMissionRepeatEnd(id);
    }

    public void initMissionState(String id) {
        mRoomApiService.initMissionState(id);
    }

    public LiveData<List<UserMission>> getCompletedMissionList(long start, long end){
        return mRoomApiService.getCompletedMissionList(start, end);
    }

    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return mRoomApiService.getNumberOfCompletionById(id,todayStart);
    }

    public LiveData<MissionState> getMissionStateByToday(String id, long todayStart) {
        LogUtil.logE(TAG,"[getMissionStateByToday] ID = "+id+" , TODAYSTART = "+todayStart);
        return mRoomApiService.getMissionStateByToday(id,todayStart);
    }

    public void saveMissionState(String missionId,MissionState missionState) {
        mRoomApiService.saveMissionState(missionId,missionState);
    }

    public LiveData<List<MissionState>> getMissionStateList() {
        return mRoomApiService.getMissionStateList();
    }

    public LiveData<List<UserMission>> getPastCompletedMission(long today) {
        return mRoomApiService.getPastCompletedMission(today);
    }

    public LiveData<List<UserMission>> getTodayNoneRepeatMissions(){
        return mRoomApiService.getTodayNoneRepeatMissions();
    }

    public LiveData<List<UserMission>> getTodayRepeatEverydayMissions(){
        return mRoomApiService.getTodayRepeatEverydayMissions();
    }

    public LiveData<List<UserMission>> getTodayRepeatCustomizeMissions(){
        return mRoomApiService.getTodayRepeatCustomizeMissions();
    }

    public LiveData<List<UserMission>> getComingNoneRepeatMissions(){
        return mRoomApiService.getComingNoneRepeatMissions();
    }

    public LiveData<List<UserMission>> getComingRepeatEverydayMissions(){
        return mRoomApiService.getComingRepeatEverydayMissions();
    }

    public LiveData<List<UserMission>> getComingRepeatCustomizeMissions(){
        return mRoomApiService.getComingRepeatCustomizeMissions();
    }

    public LiveData<List<UserMission>> getPastNoneRepeatMissions(){
        return mRoomApiService.getPastNoneRepeatMissions();
    }

    public LiveData<List<UserMission>> getPastRepeatEverydayMissions(){
        return mRoomApiService.getPastRepeatEverydayMissions();
    }

    public LiveData<List<UserMission>> getPastRepeatDefineMissions(){
        return mRoomApiService.getPastRepeatCustomizeMissions();
    }
}
