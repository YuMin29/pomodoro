package com.yumin.pomodoro.ui.home;

import android.app.Application;
<<<<<<< HEAD
import android.text.Editable;
import android.text.TextWatcher;
=======
>>>>>>> d7e87be... tmp
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

<<<<<<< HEAD
=======
import com.yumin.pomodoro.data.Mission;
>>>>>>> d7e87be... tmp
import com.yumin.pomodoro.utils.Event;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionViewModel extends AndroidViewModel {
    public static final String TAG = "[AddMissionViewModel]";
    private MutableLiveData<Event<Integer>> _clickEvent = new MutableLiveData<Event<Integer>>();
    LiveData<Event<Integer>> clickEvent = _clickEvent;

<<<<<<< HEAD
    public AddMissionViewModel(@NonNull Application application) {
        super(application);
=======
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
>>>>>>> d7e87be... tmp
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
<<<<<<< HEAD

    public TextWatcher onTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
=======
>>>>>>> d7e87be... tmp
}
