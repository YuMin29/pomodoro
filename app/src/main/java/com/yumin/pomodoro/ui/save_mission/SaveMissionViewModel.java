package com.yumin.pomodoro.ui.save_mission;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SaveMissionViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SaveMissionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is save fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}