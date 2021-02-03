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
        return null;
    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        return null;
    }

    @Override
    public void addMission(UserMission mission) {
        LogUtil.logD(TAG,"[addMission]");
        if (getCurrentUserUid() != null) {
            mission.setUid(getCurrentUserUid());
            // add mission to firebase
            databaseReference.child("usermissions").child(getCurrentUserUid()).push().setValue(mission);
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
    public LiveData<List<UserMission>> getTodayMissions(long start, long end) {
        MutableLiveData<List<UserMission>> mutableLiveData = new MutableLiveData<>();
        List<UserMission> missionList = new ArrayList<>();

        Query queryOperateDay = databaseReference.child("usermissions").child(getCurrentUserUid()).orderByChild("operateDay").startAt(start).endAt(end);
        queryOperateDay.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LogUtil.logE(TAG,"[getTodayMissions] queryOperateDay EXIST");
                    for (DataSnapshot mission : dataSnapshot.getChildren()) {
                        missionList.add(mission.getValue(UserMission.class));
                        mutableLiveData.postValue(missionList);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logE(TAG,"[getTodayMissions] queryOperateDay ERROR"+databaseError.getDetails());
            }
        });

        Query queryRepeatType = databaseReference.child("usermissions").child(getCurrentUserUid()).orderByChild("repeat").equalTo(1);
        queryRepeatType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LogUtil.logE(TAG,"[getTodayMissions] queryRepeatType EXIST");
                    for (DataSnapshot mission : dataSnapshot.getChildren()) {
                        if (mission.getValue(UserMission.class).getOperateDay() <= end) {
                            missionList.add(mission.getValue(UserMission.class));
                            mutableLiveData.postValue(missionList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logE(TAG,"[getTodayMissions] queryRepeatType ERROR"+databaseError.getDetails());
            }
        });

        Query queryRepeatDay = databaseReference.child("usermissions").child(getCurrentUserUid()).orderByChild("repeatStart").startAt(start);
        queryRepeatDay.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LogUtil.logE(TAG,"[getTodayMissions] queryRepeatDay EXIST");
                    for (DataSnapshot mission : dataSnapshot.getChildren()) {
                        if (mission.getValue(UserMission.class).getRepeatEnd() <= end) {
                            missionList.add(mission.getValue(UserMission.class));
                            mutableLiveData.postValue(missionList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logE(TAG,"[getTodayMissions] queryRepeatDay ERROR"+databaseError.getDetails());
            }
        });

        LogUtil.logE(TAG,"[getTodayMissions] missionList SIZE = "+missionList.size());
        return mutableLiveData;
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
