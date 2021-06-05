package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.ui.base.ProtectedUnPeekLiveData;
import com.yumin.pomodoro.ui.base.UnPeekLiveData;

public class SharedViewModel extends ViewModel {
    private UnPeekLiveData<Long> mRepeatStart = new UnPeekLiveData<>();
    private UnPeekLiveData<Long> mRepeatEnd = new UnPeekLiveData<>();
    private UnPeekLiveData<String> mBackupTime = new UnPeekLiveData.Builder<String>().setAllowNullValue(false).create();
    private UnPeekLiveData<String> mRestoreTime = new UnPeekLiveData.Builder<String>().setAllowNullValue(false).create();

    public ProtectedUnPeekLiveData<Long> getRepeatStart() {
        return mRepeatStart;
    }

    public void setRepeatStart(long time) {
        mRepeatStart.setValue(time);
    }

    public ProtectedUnPeekLiveData<Long> getRepeatEnd() {
        return mRepeatEnd;
    }

    public void setRepeatEnd(long time) {
        mRepeatEnd.setValue(time);
    }

    public void setBackupTime(String time){
        mBackupTime.setValue(time);
    }

    public ProtectedUnPeekLiveData<String> getBackupTime(){
        return mBackupTime;
    }

    public void setRestoreTime(String time){
        mRestoreTime.setValue(time);
    }

    public ProtectedUnPeekLiveData<String> getRestoreTime(){
        return mRestoreTime;
    }
}
