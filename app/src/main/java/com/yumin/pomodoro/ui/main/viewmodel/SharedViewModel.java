package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.utils.base.ProtectedUnPeekLiveData;
import com.yumin.pomodoro.utils.base.UnPeekLiveData;

public class SharedViewModel extends ViewModel {
    private UnPeekLiveData<Long> mRepeatStart = new UnPeekLiveData<>();
    private UnPeekLiveData<Long> mRepeatEnd = new UnPeekLiveData<>();
    private UnPeekLiveData<Boolean> mIsChangedRepeatRange = new UnPeekLiveData<>();

    public ProtectedUnPeekLiveData<Long> getRepeatStart() {
        return mRepeatStart;
    }

    public void setRepeatStart(long time) {
        this.mRepeatStart.setValue(time);
    }

    public ProtectedUnPeekLiveData<Long> getRepeatEnd() {
        return mRepeatEnd;
    }

    public void setRepeatEnd(long time) {
        this.mRepeatEnd.setValue(time);
    }

    public void setChangedRepeatRange(boolean changed){
        this.mIsChangedRepeatRange.setValue(changed);
    }

    public ProtectedUnPeekLiveData<Boolean> getChangedRepeatRange(){
        return mIsChangedRepeatRange;
    }
}
