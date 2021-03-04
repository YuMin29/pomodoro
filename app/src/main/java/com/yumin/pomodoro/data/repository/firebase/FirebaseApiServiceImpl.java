package com.yumin.pomodoro.data.repository.firebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.data.RecordMissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;

import java.util.Date;
import java.util.List;

public class FirebaseApiServiceImpl implements ApiService<UserMission> {
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
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(getUserMissionPath().orderByKey().equalTo(strId));
        return liveData;
    }

    @Override
    public void updateNumberOfCompletionById(String id, int num) {
        getUserMissionPath().child(id).child("numberOfCompletions").setValue(num);
    }

    @Override
    public void updateIsFinishedById(String id, boolean finished, int completeOfNumber) {
        LogUtil.logE(TAG,"[updateIsFinishedById] ID = " + id + ", finished = "+finished);

        getUserMissionPath().child(id)
                .child("finished").setValue(finished);

        getUserMissionPath().child(id)
                .child("finishedDay").setValue(finished ? new Date().getTime() : -1);

        recordFinishDayByMission(id, completeOfNumber, finished);
    }

    private void recordFinishDayByMission(String id,int completeOfNumber, boolean isFinish){
        LogUtil.logE(TAG,"[recordFinishDayByMission] id = "+id
                +" ,completeOfNumber = "+completeOfNumber
                +" ,isFinish = "+isFinish);
        String todayMilli = String.valueOf(TimeMilli.getTodayInitTime());
        DatabaseReference databaseReference = getCalendarPath().child(todayMilli);
        databaseReference.push();
        databaseReference.child(id).setValue(new RecordMissionState(completeOfNumber,isFinish));
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
    public LiveData<List<UserMission>> getFinishedMissions(long start, long end) {
        LogUtil.logE(TAG,"[getFinishedMissions] start = "+start+" ,end = "+end);
        start = TimeMilli.getTodayStartTime();
        end = TimeMilli.getTodayEndTime();
        return new FirebaseQueryListLiveData(getUserMissionPath()
                .orderByChild("finishedDay").startAt(start).endAt(end));
    }

    @Override
    public LiveData<List<UserMission>> getUnFinishedMissions(long start, long end) {
        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(getUserMissionPath()
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

//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByOperateDay(long start, long end) {
//        LogUtil.logE("[Stella]","[getTodayMissionsByOperateDay]");
//        return new FirebaseQueryListLiveData(getUserMissionPath()
//                .orderByChild("operateDay").startAt(start).endAt(end));
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatType(long start, long end) {
//        FirebaseQueryListLiveData listLiveData =
//                new FirebaseQueryListLiveData(getUserMissionPath()
//                        .orderByChild("repeat").equalTo(1));
//
//        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
//            @Override
//            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(UserMission.class).getOperateDay() < start) {
//                    return dataSnapshot.getValue(UserMission.class);
//                }
//                return null;
//            }
//        });
//        return listLiveData;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getTodayMissionsByRepeatRange(long start, long end) {
//        FirebaseQueryListLiveData listLiveData =
//                new FirebaseQueryListLiveData(getUserMissionPath()
//                        .orderByChild("repeatStart").startAt(start));
//
//        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
//            @Override
//            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(UserMission.class).getRepeatEnd() <= end &&
//                        dataSnapshot.getValue(UserMission.class).getOperateDay() <= start) {
//                    return dataSnapshot.getValue(UserMission.class);
//                }
//                return null;
//            }
//        });
//        return listLiveData;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByOperateDay(long today) {
//        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(getUserMissionPath()
//                .orderByChild("operateDay"));
//
//        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
//            @Override
//            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(UserMission.class).getOperateDay() > today) {
//                    return dataSnapshot.getValue(UserMission.class);
//                }
//                return null;
//            }
//        });
//        return listLiveData;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByRepeatType(long today) {
//        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(getUserMissionPath()
//                .orderByChild("repeat").equalTo(1));
//
//        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
//            @Override
//            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
//                // 過濾 執行日< TODAY
//                if (dataSnapshot.getValue(UserMission.class).getOperateDay() < today) {
//                    return dataSnapshot.getValue(UserMission.class);
//                }
//                return null;
//            }
//        });
//        return listLiveData;
//    }
//
//    @Override
//    public LiveData<List<UserMission>> getComingMissionsByRepeatRange(long today) {
//        FirebaseQueryListLiveData listLiveData = new FirebaseQueryListLiveData(getUserMissionPath()
//                .orderByChild("repeatEnd"));
//
//        listLiveData.setOnQueryListener(new FirebaseQueryListLiveData.OnQueryListener() {
//            @Override
//            public UserMission onSecondQuery(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(UserMission.class).getRepeatEnd() >= today &&
//                        dataSnapshot.getValue(UserMission.class).getOperateDay() < today) {
//                    return dataSnapshot.getValue(UserMission.class);
//                }
//                return null;
//            }
//        });
//        return listLiveData;
//    }
}
