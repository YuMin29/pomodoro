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
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryMissionListLiveData extends LiveData<List<MissionState>> {
    private static final String TAG = FirebaseQueryMissionListLiveData.class.getSimpleName();
    private QueryListener mOnQueryListener;
    private final Query mQuery;
    private final FirebaseQueryMissionListLiveData.MyValueEventListener mMyValueEventListener = new FirebaseQueryMissionListLiveData.MyValueEventListener();

    public FirebaseQueryMissionListLiveData(Query query) {
        LogUtil.logE(TAG,"[query] "+query.toString());
        mQuery = query;
    }

    public FirebaseQueryMissionListLiveData(DatabaseReference databaseReference) {
        mQuery = databaseReference;
    }

    public void setOnQueryListener(QueryListener onQueryListener){
        mOnQueryListener = onQueryListener;
    }

    private boolean isLoginAsUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LogUtil.logE(TAG,"[isLoginAsUser] RETURN :" + String.valueOf(user != null));
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

    interface QueryListener {
        MissionState onSecondQuery(DataSnapshot dataSnapshot);
    }

    class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            LogUtil.logE(TAG,"[MyValueEventListener]");
            List<MissionState> missionStateList = new ArrayList<>();
            if (snapshot.exists()) {
                LogUtil.logE(TAG,"[MyValueEventListener] snapshot EXIST");
                for (DataSnapshot state : snapshot.getChildren()) {
                    if (mOnQueryListener != null) {
                        // operate second query
                        MissionState missionState = mOnQueryListener.onSecondQuery(state);
                        if (missionState != null)
                            missionStateList.add(missionState);
                    } else {
                        LogUtil.logE(TAG,"[MyValueEventListener] state.getValue(MissionState.class) = "+state.getValue(MissionState.class).toString());
                        missionStateList.add(state.getValue(MissionState.class));
                    }
                }
            }
            setValue(missionStateList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            LogUtil.logE(TAG,"[MyValueEventListener] ERROR :"+error.getDetails());
        }
    }
}
