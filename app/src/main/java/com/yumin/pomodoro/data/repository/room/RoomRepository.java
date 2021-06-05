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

    public String addMission(UserMission userMission){
        return mRoomApiService.addMission(userMission);
    }

    public void updateMission(UserMission userMission){
        mRoomApiService.updateMission(userMission);
    }

    @Override
    public void updateMissionNumberOfCompletion(String id, int num) {
        mRoomApiService.updateNumberOfCompletionById(id,num);
    }

    @Override
    public void updateMissionFinishedState(String id, boolean finished, int completeOfNumber) {
        mRoomApiService.updateMissionState(id,finished,completeOfNumber);
    }


    public void deleteMission(UserMission userMission){
        mRoomApiService.deleteMission(userMission);
    }

    @Override
    public void deleteAllMission() {
        mRoomApiService.deleteAllMission();
    }


    public LiveData<Long> getMissionRepeatStart(String id){
        return mRoomApiService.getMissionRepeatStart(id);
    }

    public LiveData<Long> getMissionRepeatEnd(String id){
        return mRoomApiService.getMissionRepeatEnd(id);
    }

    public LiveData<Long> getMissionOperateDay(String id){
        return mRoomApiService.getMissionOperateDay(id);
    }

    @Override
    public void initMissionState(String id) {
        mRoomApiService.initMissionState(id);
    }

    public LiveData<List<UserMission>> getCompletedMissionList(long start, long end){
        return mRoomApiService.getCompletedMissionList(start, end);
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        return mRoomApiService.getNumberOfCompletionById(id,todayStart);
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        LogUtil.logE(TAG,"[getMissionStateById] ID = "+id+" , TODAYSTART = "+todayStart);
        return mRoomApiService.getMissionStateById(id,todayStart);
    }

    @Override
    public void saveMissionState(String missionId,MissionState missionState) {
        mRoomApiService.saveMissionState(missionId,missionState);
    }

    @Override
    public LiveData<List<MissionState>> getMissionStateList() {
        return mRoomApiService.getMissionStateList();
    }

    @Override
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
        return mRoomApiService.getTodayRepeatDefineMissions();
    }

    public LiveData<List<UserMission>> getComingNoneRepeatMissions(){
        return mRoomApiService.getComingNoneRepeatMissions();
    }

    public LiveData<List<UserMission>> getComingRepeatEverydayMissions(){
        return mRoomApiService.getComingRepeatEverydayMissions();
    }

    public LiveData<List<UserMission>> getComingRepeatCustomizeMissions(){
        return mRoomApiService.getComingRepeatDefineMissions();
    }
}
