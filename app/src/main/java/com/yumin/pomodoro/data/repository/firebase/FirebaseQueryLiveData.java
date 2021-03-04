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
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

public class FirebaseQueryLiveData extends LiveData<UserMission> {
    private static final String TAG = "[FirebaseQueryLiveData]";
    private final Query query;
    private final MyValueEventListener myValueEventListener = new MyValueEventListener();
    private final MyChildListener myChildListener = new MyChildListener();

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveData(DatabaseReference databaseReference){
        this.query  = databaseReference;
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
            query.addChildEventListener(myChildListener);
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
