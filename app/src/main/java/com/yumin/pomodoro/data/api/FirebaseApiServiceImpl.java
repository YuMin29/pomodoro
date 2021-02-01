package com.yumin.pomodoro.data.api;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class FirebaseApiServiceImpl implements ApiService{
    private static final String TAG = "[FirebaseApiServiceImpl]";
    DatabaseReference databaseReference;

    public FirebaseApiServiceImpl(){
        LogUtil.logE(TAG,"constructor");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("missions");
    }

    @Override
    public LiveData<List<Mission>> getMissions() {
        return null;
    }

    @Override
    public void addMission(Mission mission) {

    }

    @Override
    public Mission getInitMission() {
        return null;
    }

    @Override
    public Mission getQuickMission(int time, int shortBreakTime, int color) {
        return null;
    }

    @Override
    public LiveData<List<Mission>> getTodayMissions(long start, long end) {
        return null;
    }

    @Override
    public LiveData<List<Mission>> getComingMissions(long today) {
        return null;
    }

    @Override
    public LiveData<Mission> getMissionById(int id) {
        return null;
    }

    @Override
    public void updateMission(Mission mission) {

    }

    @Override
    public void deleteMission(Mission mission) {

    }

    @Override
    public void updateNumberOfCompletionById(int id, int num) {

    }

    @Override
    public void updateIsFinishedById(int id, boolean finished) {

    }

    @Override
    public LiveData<Long> getMissionRepeatStart(int id) {
        return null;
    }

    @Override
    public LiveData<Long> getMissionRepeatEnd(int id) {
        return null;
    }

    @Override
    public LiveData<Long> getMissionOperateDay(int id) {
        return null;
    }

    @Override
    public LiveData<List<Mission>> getFinishedMissions() {
        return null;
    }

    @Override
    public LiveData<List<Mission>> getUnFinishedMissions() {
        return null;
    }
}
