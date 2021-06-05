package com.yumin.pomodoro.ui.base;

import android.util.SparseArray;

import androidx.lifecycle.ViewModel;

public class DataBindingConfig {
    private final int mLayout;
    private final int mVmVariableId;
    private final ViewModel mStateViewModel;
    private SparseArray mBindingParams = new SparseArray();

    public DataBindingConfig(int layout, int vmVariableId, ViewModel stateViewModel) {
        mLayout = layout;
        mVmVariableId = vmVariableId;
        mStateViewModel = stateViewModel;
    }

    public int getLayout() {
        return mLayout;
    }

    public int getVmVariableId() {
        return mVmVariableId;
    }

    public ViewModel getStateViewModel() {
        return mStateViewModel;
    }

    public SparseArray getBindingParams() {
        return mBindingParams;
    }

    public DataBindingConfig addBindingParam(int variableId, Object object) {
        if (mBindingParams.get(variableId) == null) {
            mBindingParams.put(variableId, object);
        }
        return this;
    }
}
