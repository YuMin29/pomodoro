package com.yumin.pomodoro.base;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class BaseApplication extends Application implements ViewModelStoreOwner {
    private static Application mApplication;
    private ViewModelStore mAppViewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mAppViewModelStore = new ViewModelStore();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }
}

