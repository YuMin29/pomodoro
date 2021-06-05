package com.yumin.pomodoro.ui.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class ProtectedUnPeekLiveData<T> extends LiveData<T> {
    protected boolean mIsAllowNullValue;
    private final HashMap<Integer, Boolean> mObservers = new HashMap<>();

    public void observeInActivity(@NonNull AppCompatActivity activity, @NonNull Observer<? super T> observer) {
        LifecycleOwner owner = activity;
        Integer storeId = System.identityHashCode(activity.getViewModelStore());
        observe(storeId, owner, observer);
    }

    public void observeInFragment(@NonNull Fragment fragment, @NonNull Observer<? super T> observer) {
        LifecycleOwner owner = fragment.getViewLifecycleOwner();
        Integer storeId = System.identityHashCode(fragment.getViewModelStore());
        observe(storeId, owner, observer);
    }

    private void observe(@NonNull Integer storeId, @NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {

        if (mObservers.get(storeId) == null) {
            mObservers.put(storeId, true);
        }

        super.observe(owner, t -> {
            if (!mObservers.get(storeId)) {
                mObservers.put(storeId, true);
                if (t != null || mIsAllowNullValue) {
                    observer.onChanged(t);
                }
            }
        });
    }

    @Override
    protected void setValue(T value) {
        if (value != null || mIsAllowNullValue) {
            for (Map.Entry<Integer, Boolean> entry : mObservers.entrySet()) {
                LogUtil.logD(ProtectedUnPeekLiveData.class.getName(),"[setValue] value = "+value);
                entry.setValue(false);
            }
            super.setValue(value);
        }
    }

    protected void clear() {
        super.setValue(null);
    }
}