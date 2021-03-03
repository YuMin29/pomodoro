package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private final MyChildListener myChildListener = new MyChildListener();

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

    private boolean isLoginAsUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LogUtil.logE(TAG,"[isLoginAsUser] RETURN :" + String.valueOf(user != null));
        return user != null;
    }

    @Override
    protected void onActive() {
        LogUtil.logE(TAG,"[onActive]");
        if (isLoginAsUser())
            query.limitToFirst(1000).addChildEventListener(myChildListener);
        else
            query.addValueEventListener(myValueEventListener);
    }

    @Override
    protected void onInactive() {
        LogUtil.logE(TAG,"[onInactive]");
        if (isLoginAsUser())
            query.removeEventListener(myChildListener);
        else
            query.removeEventListener(myValueEventListener);
    }

    interface OnQueryListener{
        public UserMission onSecondQuery(DataSnapshot dataSnapshot);
    }

    class MyChildListener implements ChildEventListener {
        List<UserMission> userMissionList = new ArrayList<>();
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            LogUtil.logE(TAG,"[MyChildListener] [onChildAdded]");
            if (snapshot.exists()) {
                LogUtil.logE(TAG,"[MyChildListener] [onChildAdded] snapshot EXIST");
                LogUtil.logE(TAG,"[MyChildListener] [onChildAdded] snapshot count = "
                        +snapshot.getChildrenCount());

                    if (onQueryListener != null) {
                        // operate second query
                        UserMission userMission = onQueryListener.onSecondQuery(snapshot);
                        if (userMission != null)
                            userMissionList.add(userMission);
                    } else {
                        userMissionList.add(snapshot.getValue(UserMission.class));
                    }
            }
            setValue(userMissionList);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
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
