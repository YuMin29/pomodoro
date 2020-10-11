package com.yumin.pomodoro.ui.home;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.utils.Event;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private MutableLiveData<Event<Integer>> _clickEvent = new MutableLiveData<Event<Integer>>();
    LiveData<Event<Integer>> clickEvent = _clickEvent;

    public MutableLiveData<String> missionTitle = new MutableLiveData<>();
    public MutableLiveData<Integer> missionTime = new MutableLiveData<>();
    public MutableLiveData<Integer> missionBreak = new MutableLiveData<>();

    public MutableLiveData<Mission> mission = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application) {
        super(application);
        initMission();
    }

    private void initMission(){
        mission.postValue(new Mission("test"));
    }

    public LiveData<Event<Integer>> getClickEvent() {
        return clickEvent;
    }

    public View.OnClickListener onViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.logD(TAG,"[onClick] id = "+v.getId());
            _clickEvent.setValue(new Event<>(v.getId()));
        }
    };
}
