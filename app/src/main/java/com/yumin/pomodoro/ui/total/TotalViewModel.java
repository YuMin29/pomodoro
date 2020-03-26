package com.yumin.pomodoro.ui.total;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TotalViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TotalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is total fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}