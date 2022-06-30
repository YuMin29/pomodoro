package com.yumin.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;

    private static final String PREFERENCE_NAME = "pomodoro-welcome";
    private static final String FIRST_TIME_LAUNCH = "firstTimeLaunch";

    public PreferenceManager(Context context) {
        this.mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        mEditor.putBoolean(FIRST_TIME_LAUNCH, isFirstTime);
        mEditor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return mSharedPreferences.getBoolean(FIRST_TIME_LAUNCH, true);
    }
}
