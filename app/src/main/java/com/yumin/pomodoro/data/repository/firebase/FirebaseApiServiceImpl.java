package com.yumin.pomodoro.data.repository.firebase;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class FirebaseApiServiceImpl implements ApiService<UserMission> {
    private static final String TAG = "[FirebaseApiServiceImpl]";
    FirebaseDatabase database;

    public FirebaseApiServiceImpl(){
        LogUtil.logE(TAG,"constructor");
        database = FirebaseDatabase.getInstance();
    }

    private String getCurrentUserUid(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            return user.getUid();
        return null;
    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        return null;
    }

    @Override
    public void addMission(UserMission mission) {
        LogUtil.logD(TAG,"[addMission]");
        if (getCurrentUserUid() != null)
            mission.setUid(getCurrentUserUid());
        // add mission to firebase
        database.getReference().child("usermissions").setValue(mission);
        database.getReference().child("usermissions").getKey();
    }

    @Override
    public UserMission getInitMission() {
        return new UserMission();
    }

    @Override
    public UserMission getQuickMission(int time, int shortBreakTime, int color) {
        return null;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissions(long start, long end) {
        return null;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissions(long today) {
        return null;
    }

    @Override
    public LiveData<UserMission> getMissionById(int id) {
        return null;
    }

    @Override
    public void updateMission(UserMission mission) {

    }

    @Override
    public void deleteMission(UserMission mission) {

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
    public LiveData<List<UserMission>> getFinishedMissions() {
        return null;
    }

    @Override
    public LiveData<List<UserMission>> getUnFinishedMissions() {
        return null;
    }
}
