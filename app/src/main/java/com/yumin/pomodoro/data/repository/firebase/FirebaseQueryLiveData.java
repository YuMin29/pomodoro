package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

public class FirebaseQueryLiveData extends LiveData<UserMission> {
    private static final String TAG = FirebaseQueryLiveData.class.getSimpleName();
    private final Query mQuery;
    private final MyValueEventListener mMyValueEventListener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        mQuery = query;
    }

    public FirebaseQueryLiveData(DatabaseReference databaseReference){
        mQuery = databaseReference;
    }


    private boolean isLoginAsUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LogUtil.logE(TAG,"[isLoginAsUser] return :" + String.valueOf(user != null));
        return user != null;
    }

    @Override
    protected void onActive() {
        LogUtil.logE(TAG,"[onActive]");
        mQuery.addValueEventListener(mMyValueEventListener);
    }

    @Override
    protected void onInactive() {
        LogUtil.logE(TAG,"[onInactive]");
        mQuery.removeEventListener(mMyValueEventListener);
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
