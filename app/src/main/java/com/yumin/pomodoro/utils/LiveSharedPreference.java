package com.yumin.pomodoro.utils;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

public class LiveSharedPreference<T> extends LiveData<T> {
    private static final String TAG = LiveSharedPreference.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private String mKey;
    private T mDefaultValue;

    public LiveSharedPreference(SharedPreferences sharedPreferences, String key, T defaultValue) {
        mSharedPreferences = sharedPreferences;
        mKey = key;
        mDefaultValue = defaultValue;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (LiveSharedPreference.this.mKey.equals(key)) {
                LogUtil.logE(TAG,"[onSharedPreferenceChanged] KEY = "+mKey +" ,VALUE = "+ (T) mSharedPreferences.getAll().get(mKey));
                T value = (T) mSharedPreferences.getAll().get(mKey);
                setValue(value == null ? mDefaultValue : value);
            }
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        LogUtil.logE(TAG,"[onActive] KEY = "+mKey +" ,VALUE = "+ (T) mSharedPreferences.getAll().get(mKey));
        T value = (T) mSharedPreferences.getAll().get(mKey);
        setValue(value == null ? mDefaultValue : value);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
}
