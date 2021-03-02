package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryLiveData extends LiveData<UserMission> {
    private static final String TAG = "[FirebaseQueryLiveData]";
    private final Query query;
    private final MyValueEventListener myValueEventListener = new MyValueEventListener();
    private final MyChildListener myChildListener = new MyChildListener();
    private boolean mIsOffline = false;

    public FirebaseQueryLiveData(Query query, boolean isOffline) {
        this.query = query;
        this.mIsOffline = isOffline;
    }

    public FirebaseQueryLiveData(DatabaseReference databaseReference, boolean isOffline){
        this.query  = databaseReference;
        mIsOffline = isOffline;
    }

    @Override
    protected void onActive() {
        LogUtil.logE(TAG,"[onActive]");
        if (mIsOffline)
            query.addChildEventListener(myChildListener);
        else
            query.addValueEventListener(myValueEventListener);
    }

    @Override
    protected void onInactive() {
        LogUtil.logE(TAG,"[onInactive]");
        if (mIsOffline)
            query.removeEventListener(myChildListener);
        else
            query.removeEventListener(myValueEventListener);
    }
    class MyChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            LogUtil.logE(TAG,"[MyChildListener] [onChildAdded]");
            setValue(snapshot.getValue(UserMission.class));
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

    class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    setValue(dataSnapshot.getValue(UserMission.class));
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            LogUtil.logE(TAG,"[MyValueEventListener][onCancelled] error = "+error);
        }
    }
}
