package com.yumin.pomodoro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    MutableLiveData<List<String>> mList;

    public HomeViewModel(){
        mList = new MutableLiveData<>();
        List<String> data = new ArrayList<>();
        data.add("aaaa");
        data.add("bbbb");
        mList.setValue(data);
    }

    public LiveData<List<String>> getList(){
        return mList;
    }
}
