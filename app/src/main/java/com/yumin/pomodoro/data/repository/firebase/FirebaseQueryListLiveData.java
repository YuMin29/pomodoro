package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryListLiveData extends LiveData<List<UserMission>> {
    private static final String TAG = "[FirebaseQueryLiveData]";
    private OnQueryListener onQueryListener;
    private final Query query;
    private final MyValueEventListener myValueEventListener = new MyValueEventListener();

    public FirebaseQueryListLiveData(Query query) {
        LogUtil.logE(TAG,"[query] "+query.toString());
        this.query = query;
    }

    public FirebaseQueryListLiveData(DatabaseReference databaseReference) {
        this.query = databaseReference;
    }

    public void setOnQueryListener(OnQueryListener onQueryListener){
        this.onQueryListener = onQueryListener;
    }

    @Override
    protected void onActive() {
        LogUtil.logE(TAG,"[onActive]");
        query.addValueEventListener(myValueEventListener);
    }

    @Override
    protected void onInactive() {
        LogUtil.logE(TAG,"[onInactive]");
        query.removeEventListener(myValueEventListener);
    }

    interface OnQueryListener{
        public UserMission onSecondQuery(DataSnapshot dataSnapshot);
    }

    class MyValueEventListener implements ValueEventListener{

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            LogUtil.logE(TAG,"[MyValueEventListener]");
            List<UserMission> userMissionList = new ArrayList<>();
            if (snapshot.exists()) {
                LogUtil.logE(TAG,"[MyValueEventListener] snapshot EXIST");
                for (DataSnapshot mission : snapshot.getChildren()) {
                    if (onQueryListener != null) {
                        // operate second query
                        UserMission userMission = onQueryListener.onSecondQuery(mission);
                        if (userMission != null)
                            userMissionList.add(userMission);
                    } else {
                        userMissionList.add(mission.getValue(UserMission.class));
                    }
                }
            }
            setValue(userMissionList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            LogUtil.logE(TAG,"[MyValueEventListener] ERROR :"+error.getDetails());
        }
    }
}
