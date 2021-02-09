package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FirebaseApiServiceImpl implements ApiService<UserMission> {
    private static final String TAG = "[FirebaseApiServiceImpl]";
    DatabaseReference databaseReference;

    public FirebaseApiServiceImpl(){
        LogUtil.logE(TAG,"constructor");
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getCurrentUserUid(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            return user.getUid();
        return "";
    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        return null;
    }

    @Override
    public void addMission(UserMission mission) {
        LogUtil.logD(TAG,"[addMission]");
        if (getCurrentUserUid() != null) {
            String id = databaseReference.child("usermissions").child(getCurrentUserUid()).push().getKey();
            mission.setStrId(id);
            // add mission to firebase
            databaseReference.child("usermissions").child(getCurrentUserUid()).child(id).setValue(mission);
        }
    }

    @Override
    public UserMission getInitMission() {
        return new UserMission();
    }

    @Override
    public UserMission getQuickMission(int time, int shortBreakTime, int color) {
        return new UserMission(time,shortBreakTime,color);
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end) {
        FirebaseQueryListLiveData listLiveData =
                new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                        .orderByChild("operateDay").startAt(start).endAt(end));
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
        FirebaseQueryListLiveData listLiveData =
                new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                        .orderByChild("repeat").equalTo(1));
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserMission.class).getOperateDay() < start) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end) {
        FirebaseQueryListLiveData listLiveData =
                new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                        .orderByChild("repeatStart").startAt(start));
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserMission.class).getRepeatEnd() <= end &&
                        dataSnapshot.getValue(UserMission.class).getOperateDay() <= start) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today) {
        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                .orderByChild("operateDay"));
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserMission.class).getOperateDay() > today) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today) {
        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                .orderByChild("repeat").equalTo(1));
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                .orderByChild("repeatEnd"));
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserMission.class).getRepeatEnd() >= today) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }

    @Override
    public LiveData<UserMission> getMissionById(int id) {
        return null;
    }

    @Override
    public LiveData<UserMission> getMissionById(String strId) {
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(databaseReference.child("usermissions")
                .child(getCurrentUserUid()).orderByKey().equalTo(strId));
        return liveData;
    }

    @Override
    public void updateMission(UserMission mission) {
        databaseReference.child("usermissions").child(getCurrentUserUid())
                .child(mission.getStrId()).setValue(mission);
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
