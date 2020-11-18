package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.model.AdjustMissionItem;

import java.util.ArrayList;
import java.util.List;

public class MissionItemViewModel extends AndroidViewModel {
    public MutableLiveData<AdjustMissionItem> countViewItem = new MutableLiveData<>();
    public MutableLiveData<List<AdjustMissionItem>> countViewItemList = new MutableLiveData<>();
    List<AdjustMissionItem> adjustMissionItems = new ArrayList<>();

    public MissionItemViewModel(@NonNull Application application) {
        super(application);
        init(application);
    }

    private void init(Application application){
//        countViewItems = new ArrayList<>();
//        countViewItems.add(new CountViewItem(application.getApplicationContext(), "0", R.string.mission_time, 0, 0));
//        countViewItems.add(new CountViewItem(application.getApplicationContext(), "0", R.string.mission_break, 0, 0));
//        countViewItems.add(new CountViewItem(application.getApplicationContext(), "0", R.string.mission_goal, 0, 0));
//        countViewItems.add(new CountViewItem(application.getApplicationContext(), "0", R.string.mission_repeat, 0, 0));
//        countViewItemList.setValue(countViewItems);
    }

    public void updateCountView(){

    }

    public List<AdjustMissionItem> getCountViewList(){
        return this.countViewItemList.getValue();
    }
}