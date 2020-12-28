package com.yumin.pomodoro.ui.main.viewmodel;

import androidx.lifecycle.ViewModel;

import com.yumin.pomodoro.utils.base.ProtectedUnPeekLiveData;
import com.yumin.pomodoro.utils.base.UnPeekLiveData;

public class SharedViewModel extends ViewModel {
//    private UnPeekLiveData<Integer> mEditMissionId = new UnPeekLiveData<>();
//    private UnPeekLiveData<Integer> mOperateMissionId = new UnPeekLiveData<>();
    private UnPeekLiveData<Long> mRepeatStart = new UnPeekLiveData<>();
    private UnPeekLiveData<Long> mRepeatEnd = new UnPeekLiveData<>();

//    public ProtectedUnPeekLiveData<Integer> getOperateMissionId() {
//        return mOperateMissionId;
//    }
//
//    public void setOperateMissionId(int mOperateMissionId) {
//        this.mOperateMissionId.setValue(mOperateMissionId);
//    }
//
//    public ProtectedUnPeekLiveData<Integer> getEditMissionId(){
//        return mEditMissionId;
//    }
//
//    public void setEditMissionId(int id){
//        this.mEditMissionId.setValue(id);
//    }

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
}
