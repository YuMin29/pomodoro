package com.yumin.pomodoro.data.repository.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.utils.LogUtil;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = "[FirebaseQueryLiveData]";
    private final Query query;
    private final MyValueEventListener myValueEventListener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveData(DatabaseReference databaseReference){
        this.query  = databaseReference;
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


    class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            setValue(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            LogUtil.logE(TAG,"Can't listen to query :" + query + error.toException());
        }
    }
}
