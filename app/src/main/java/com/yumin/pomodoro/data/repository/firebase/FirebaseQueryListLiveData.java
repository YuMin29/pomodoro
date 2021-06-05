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

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryListLiveData extends LiveData<List<UserMission>> {
    private static final String TAG = FirebaseQueryListLiveData.class.getSimpleName();
    private OnQueryListener mOnQueryListener;
    private final Query mQuery;
    private final MyValueEventListener mMyValueEventListener = new MyValueEventListener();

    public FirebaseQueryListLiveData(Query query) {
        LogUtil.logE(TAG,"[query] "+query.toString());
        mQuery = query;
    }

    public FirebaseQueryListLiveData(DatabaseReference databaseReference) {
        mQuery = databaseReference;
    }

    public void setOnQueryListener(OnQueryListener onQueryListener){
        mOnQueryListener = onQueryListener;
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

    interface OnQueryListener{
        public UserMission onSecondQuery(DataSnapshot dataSnapshot);
    }

    class MyValueEventListener implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            LogUtil.logE(TAG,"[MyValueEventListener]");
            List<UserMission> userMissionList = new ArrayList<>();
            if (snapshot.exists()) {
                LogUtil.logE(TAG,"[MyValueEventListener] snapshot exist");
                for (DataSnapshot mission : snapshot.getChildren()) {
                    if (mOnQueryListener != null) {
                        // operate second query
                        UserMission userMission = mOnQueryListener.onSecondQuery(mission);
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
            LogUtil.logE(TAG,"[MyValueEventListener] error :"+error.getDetails());
        }
    }
}
