package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.base.TimerViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.CircleTimer;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment {
    private static final String TAG = "[TimerFragment]";
    private FragmentTimerBinding fragmentTimerBinding;
    private TimerViewModel timerViewModel;
    private int itemId;
    private int missionCount;
    private CircleTimer missionTimer;
    private CircleTimer breakTimer;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null)
            itemId = bundle.getInt("itemId");

        LogUtil.logD(TAG,"[onCreateView] itemId = "+itemId);

        initViewModel();
        observeViewModel();
        fragmentTimerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer,container,false);
        fragmentTimerBinding.setLifecycleOwner(this);
//        fragmentTimerBinding.setClick(new ClickProxy());
        fragmentTimerBinding.setViewmodel(timerViewModel);

        missionTimer = fragmentTimerBinding.missionTimer;
        missionTimer.setOnFinishCountDownListener(new CircleTimer.OnFinishCountDownListener() {
            @Override
            public void onFinished() {
                LogUtil.logD(TAG,"[mission timer][onFinished]");
                    // switch to break timer
                    fragmentTimerBinding.missionTimer.setVisibility(View.GONE);
                    fragmentTimerBinding.breakTimer.setVisibility(View.VISIBLE);

            }
        });

        breakTimer = fragmentTimerBinding.breakTimer;
        breakTimer.setOnFinishCountDownListener(new CircleTimer.OnFinishCountDownListener() {
            @Override
            public void onFinished() {
                LogUtil.logD(TAG,"[break timer][onFinished]");
                if (missionCount > 1) {
                    // switch to mission timer

                }
            }
        });
        return fragmentTimerBinding.getRoot();
    }

    private void initViewModel() {
        timerViewModel = new ViewModelProvider(this, new TimerViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(getActivity().getApplication()),getContext()),itemId)).get(TimerViewModel.class);
    }

    private void observeViewModel(){
        timerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                if (mission != null) {
                    // format
                    long missionTime = Long.valueOf(mission.getTime() * 60 * 1000);
                    long missionBreakTime = Long.valueOf(mission.getShortBreakTime() * 60 * 1000);
                    // assign value
                    missionCount = mission.getGoal();
                    // post value back to view model
                    timerViewModel.getMissionTime().postValue(msTimeFormatter(missionTime));
                    timerViewModel.getMissionBreakTime().postValue(msTimeFormatter(missionBreakTime));
                }
            }
        });
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }
}