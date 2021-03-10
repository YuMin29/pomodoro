package com.yumin.pomodoro.data.repository.firebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseApiServiceImpl implements ApiService<UserMission,MissionState> {
    private static final String TAG = "[FirebaseApiServiceImpl]";
    DatabaseReference databaseReference;
    Application mApplication;

    public FirebaseApiServiceImpl(Application application){
        LogUtil.logE(TAG,"constructor");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.mApplication = application;
    }

    private String getCurrentUserUid(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            return user.getUid();
        return "";
    }

    private DatabaseReference getUserMissionPath(){
        return databaseReference.child("usermissions").child(getCurrentUserUid());
    }

    private DatabaseReference getCalendarPath(){
        return databaseReference.child("record_calender").child(getCurrentUserUid());
    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        return new FirebaseQueryListLiveData(getUserMissionPath());
    }

    @Override
    public void addMission(UserMission mission) {
        LogUtil.logD(TAG,"[addMission]");
        String id = getUserMissionPath().push().getKey();
        mission.setFirebaseMissionId(id);
        mission.setCreatedTime(new Date().getTime());
        // add mission to firebase
        getUserMissionPath().child(id).setValue(mission);
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
    public LiveData<UserMission> getMissionById(String strId) {
        return new FirebaseQueryLiveData(getUserMissionPath().orderByKey().equalTo(strId));
    }

    @Override
    public LiveData<Long> getMissionRepeatStart(String id) {
        MutableLiveData<Long> repeatStart = new MutableLiveData<>();
        getUserMissionPath().child(id).child("repeatStart")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    repeatStart.setValue(snapshot.getValue(Long.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LogUtil.logE(TAG,"[getMissionRepeatStart][onCancelled] ERROR = "+error.getDetails());
            }
        });
        return repeatStart;
    }

    @Override
    public LiveData<Long> getMissionRepeatEnd(String id) {
        MutableLiveData<Long> repeatEnd = new MutableLiveData<>();
        getUserMissionPath().child(id).child("repeatEnd")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            repeatEnd.setValue(snapshot.getValue(Long.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LogUtil.logE(TAG,"[getMissionRepeatEnd][onCancelled] ERROR = "+error.getDetails());
                    }
                });
        return repeatEnd;
    }

    @Override
    public LiveData<Long> getMissionOperateDay(String id) {
        MutableLiveData<Long> operateDay = new MutableLiveData<>();
        getUserMissionPath().child(id).child("operateDay")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            operateDay.setValue(snapshot.getValue(Long.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LogUtil.logE(TAG,"[getMissionOperateDay][onCancelled] ERROR = "+error.getDetails());
                    }
                });
        return operateDay;
    }

    @Override
    public void updateMission(UserMission mission) {
        getUserMissionPath().child(mission.getFirebaseMissionId()).setValue(mission);
    }

    @Override
    public void deleteMission(UserMission mission) {
        getUserMissionPath().child(mission.getFirebaseMissionId()).removeValue();
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
//        getUserMissionPath().child(id).child("numberOfCompletions").setValue(num);
        getCalendarPath().child(String.valueOf(TimeMilli.getTodayInitTime())).child(id)
                .child("numberOfCompletion").setValue(num);

    }

    @Override
    public void updateMissionFinishedState(String id, boolean isFinished, int completeOfNumber) {
        LogUtil.logE(TAG,"[updateIsFinishedById] ID = " + id + ", finished = "+isFinished);

        getCalendarPath().child(String.valueOf(TimeMilli.getTodayInitTime())).child(id)
                .child("isFinished").setValue(isFinished);

        getCalendarPath().child(String.valueOf(TimeMilli.getTodayInitTime())).child(id)
                .child("finishedDay").setValue(isFinished ? new Date().getTime() : -1);
    }

    @Override
    public LiveData<List<UserMission>> getFinishedMissionList(long start, long end) {
        LogUtil.logE(TAG,"[getFinishedMissions] start = "+start+" ,end = "+end);

        List<String> missions = new ArrayList<>();
        getCalendarPath().child(String.valueOf(start)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            LogUtil.logE(TAG,"[getFinishedMissions] exist");
                            for (DataSnapshot missionState : snapshot.getChildren()){
                                if (missionState.getValue(MissionState.class).isFinished()) {
                                    String missionId = missionState.getValue(MissionState.class).getMissionId();
                                    missions.add(missionId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LogUtil.logE(TAG,"[getNumberOfCompletionById][onCancelled] ERROR = "+error.getDetails());
                    }
                });

        FirebaseQueryListLiveData firebaseQueryListLiveData = new FirebaseQueryListLiveData(getUserMissionPath());
        FirebaseQueryListLiveData.OnQueryListener onQueryListener = new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                for (String id : missions) {
                    UserMission userMission = dataSnapshot.getValue(UserMission.class);
                    if (userMission.getFirebaseMissionId().equals(id)) {
                        LogUtil.logE(TAG,"[getFinishedMissions] 1111 exist");
                        return userMission;
                    }

                }
                return null;
            }
        };
        firebaseQueryListLiveData.setOnQueryListener(onQueryListener);
        return firebaseQueryListLiveData;
    }

    @Override
    public LiveData<Integer> getNumberOfCompletionById(String id, long todayStart) {
        // query number of completion from calendar
        MutableLiveData<Integer> numberOfCompletion = new MutableLiveData<>();
        getCalendarPath().child(String.valueOf(todayStart)).child(id).child("numberOfCompletion")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            numberOfCompletion.setValue(snapshot.getValue(Integer.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LogUtil.logE(TAG,"[getNumberOfCompletionById][onCancelled] ERROR = "+error.getDetails());
                    }
                });
        return numberOfCompletion;
    }

    @Override
    public LiveData<MissionState> getMissionStateById(String id, long todayStart) {
        MutableLiveData<MissionState> missionState = new MutableLiveData<>();
        getCalendarPath().child(String.valueOf(todayStart)).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            missionState.setValue(snapshot.getValue(MissionState.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LogUtil.logE(TAG,"[getNumberOfCompletionById][onCancelled] ERROR = "+error.getDetails());
                    }
                });
        return missionState;
    }

    @Override
    public void initMissionState(String missionId) {
        // insert value to calendar
        String todayMilli = String.valueOf(TimeMilli.getTodayInitTime());
        DatabaseReference databaseReference = getCalendarPath().child(todayMilli);
        databaseReference.child(missionId).setValue(
                new MissionState(0,false,TimeMilli.getTodayInitTime(),-1,missionId));
    }

    private void saveMissionState(String missionId, int completeOfNumber, boolean isFinish){
        LogUtil.logE(TAG,"[recordFinishDayByMission] id = "+missionId
                +" ,completeOfNumber = "+completeOfNumber
                +" ,isFinish = "+isFinish);
        String todayMilli = String.valueOf(TimeMilli.getTodayInitTime());
        DatabaseReference databaseReference = getCalendarPath().child(todayMilli);
        databaseReference.push();
        databaseReference.child(missionId).setValue(new MissionState(completeOfNumber,isFinish));
    }

}
