package com.yumin.pomodoro.data.repository.firebase;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Query;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.api.FireBaseApiService;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.room.MissionDao;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseApiServiceImpl implements FireBaseApiService<UserMission> {
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

    private String getLocalUserUid(){
        // read from shared preference
        String userId = mApplication.getApplicationContext().getSharedPreferences("user_data",Context.MODE_PRIVATE)
                .getString("user_id","");

        if (userId.isEmpty()) {
            // create a unique uid and save in shared preference
            String id = databaseReference.child("usermissions").push().getKey();
            LogUtil.logE(TAG,"[getCurrentUserUid] CREATE ID:"+id);
            SharedPreferences preferences = mApplication.getApplicationContext()
                    .getSharedPreferences("user_data", Context.MODE_PRIVATE);
            preferences.edit().putString("user_id",id).commit();

            return id;
        }

        LogUtil.logE(TAG,"[getCurrentUserUid] GET ID:"+userId);
        return userId;
    }

    @Override
    public LiveData<List<UserMission>> getMissions() {
        FirebaseQueryListLiveData listLiveData =
                new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid()));
        return listLiveData;
    }

    @Override
    public void addMission(UserMission mission) {
        LogUtil.logD(TAG,"[addMission]");
        if (!getCurrentUserUid().isEmpty()) {
            String id = databaseReference.child("usermissions").child(getCurrentUserUid()).push().getKey();
            mission.setStrId(id);
            mission.setCreatedTime(new Date().getTime());
            // add mission to firebase
            databaseReference.child("usermissions").child(getCurrentUserUid()).child(id).setValue(mission);
        } else {
            // write to /local_usermissions
            String id = databaseReference.child("local_usermissions").child(getLocalUserUid()).push().getKey();
            mission.setStrId(id);
            mission.setCreatedTime(new Date().getTime());
            // add mission to firebase
            databaseReference.child("local_usermissions").child(getLocalUserUid()).child(id).setValue(mission);
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
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                            .orderByChild("operateDay").startAt(start).endAt(end));
        } else {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                            .orderByChild("operateDay").startAt(start).endAt(end));
        }
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                            .orderByChild("repeat").equalTo(1));
        } else {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                            .orderByChild("repeat").equalTo(1));
        }

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
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                            .orderByChild("repeatStart").startAt(start));
        } else {
            listLiveData =
                    new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                            .orderByChild("repeatStart").startAt(start));
        }

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
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                    .orderByChild("operateDay"));
        } else {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                    .orderByChild("operateDay"));
        }
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
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                    .orderByChild("repeat").equalTo(1));
        } else {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                    .orderByChild("repeat").equalTo(1));
        }
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                // 過濾 執行日< TODAY
                if (dataSnapshot.getValue(UserMission.class).getOperateDay() < today) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
        FirebaseQueryListLiveData listLiveData;
        if (!getCurrentUserUid().isEmpty()) {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                    .orderByChild("repeatEnd"));
        } else {
            listLiveData = new FirebaseQueryListLiveData(databaseReference.child("local_usermissions").child(getLocalUserUid())
                    .orderByChild("repeatEnd"));
        }

        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserMission.class).getRepeatEnd() >= today &&
                        dataSnapshot.getValue(UserMission.class).getOperateDay() < today) {
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }


    @Override
    public LiveData<UserMission> getMissionById(String strId) {
        FirebaseQueryLiveData liveData;
        if (!getCurrentUserUid().isEmpty()) {
            liveData = new FirebaseQueryLiveData(databaseReference.child("usermissions")
                    .child(getCurrentUserUid()).orderByKey().equalTo(strId));
        } else {
            liveData = new FirebaseQueryLiveData(databaseReference.child("local_usermissions")
                    .child(getLocalUserUid()).orderByKey().equalTo(strId));
        }
        return liveData;
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
        if (!getCurrentUserUid().isEmpty()) {
            databaseReference.child("usermissions").child(getCurrentUserUid()).child(id)
                 .child("numberOfCompletions").setValue(num);
        } else {
            databaseReference.child("local_usermissions").child(getLocalUserUid()).child(id)
                    .child("numberOfCompletions").setValue(num);
        }
    }

    @Override
    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber) {
        LogUtil.logE(TAG,"[updateIsFinishedById] ID = " + id + ", finished = "+finished);
        databaseReference.child("usermissions").child(getCurrentUserUid()).child(id)
                .child("finished").setValue(finished);

        databaseReference.child("usermissions").child(getCurrentUserUid()).child(id)
                .child("finishedDay").setValue(finished ? new Date().getTime() : -1);

        recordFinishDayByMission(id, completeOfNumber, finished);
    }

    private void recordFinishDayByMission(String id,int completeOfNumber, boolean isFinish){
        LogUtil.logE(TAG,"[recordFinishDayByMission] id = "+id
                +" ,completeOfNumber = "+completeOfNumber
                +" ,isFinish = "+isFinish);
        String todayMilli = String.valueOf(TimeMilli.getTodayInitTime());
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("record_calender").child(getCurrentUserUid()).child(todayMilli);
        databaseReference.push();
        databaseReference.child(id).setValue(new MissionState(completeOfNumber,isFinish));
    }

    @Override
    public LiveData<Long> getMissionRepeatStart(String id) {
        MutableLiveData<Long> repeatStart = new MutableLiveData<>();
        databaseReference.child("usermissions").child(getCurrentUserUid()).child(id).child("repeatStart")
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
        databaseReference.child("usermissions").child(getCurrentUserUid()).child(id).child("repeatEnd")
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
        databaseReference.child("usermissions").child(getCurrentUserUid()).child(id).child("operateDay")
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
        databaseReference.child("usermissions").child(getCurrentUserUid())
                .child(mission.getStrId()).setValue(mission);
    }

    @Override
    public void deleteMission(UserMission mission) {
        databaseReference.child("usermissions").child(getCurrentUserUid())
                .child(mission.getStrId()).removeValue();
    }

    @Override
    public LiveData<List<UserMission>> getFinishedMissions(long start, long end) {
        LogUtil.logE(TAG,"[getFinishedMissions] start = "+start+" ,end = "+end);
        start = TimeMilli.getTodayStartTime();
        end = TimeMilli.getTodayEndTime();
        FirebaseQueryListLiveData listLiveData =
                new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                        .orderByChild("finishedDay").startAt(start).endAt(end));
        return listLiveData;
    }

    @Override
    public LiveData<List<UserMission>> getUnFinishedMissions(long start, long end) {
        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(databaseReference.child("usermissions").child(getCurrentUserUid())
                .orderByChild("finishedDay"));
        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
            @Override
            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getValue(UserMission.class).getFinishedDay() > end &&
                        dataSnapshot.getValue(UserMission.class).getFinishedDay() < start) ||
                    dataSnapshot.getValue(UserMission.class).getFinishedDay() == -1){
                    return dataSnapshot.getValue(UserMission.class);
                }
                return null;
            }
        });
        return listLiveData;
    }
}
