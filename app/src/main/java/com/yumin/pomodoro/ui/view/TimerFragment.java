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
import com.yumin.pomodoro.utils.LogUtil;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment {
    private static final String TAG = "[TimerFragment]";
    private FragmentTimerBinding fragmentTimerBinding;
    private TimerViewModel timerViewModel;
    private int itemId;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TimerStatus breakTimerStatus = TimerStatus.STOPPED;
    private CountDownTimer missionCountDownTimer;
    private CountDownTimer breakCountDownTimer;
    private long missionTime = 1 * 60000; // 1 min
    private long initMissionTime;
    private long missionTimeLeft;

    private int missionCount;

    private long breakTime = 1 * 60000; // 1 min
    private long initBreakTime;
    private long breakTimeLeft;

    private enum TimerStatus{
        STARTED,
        STOPPED,
        PAUSED
    }

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
        fragmentTimerBinding.setClick(new ClickProxy());
        fragmentTimerBinding.setViewmodel(timerViewModel);
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
                    initMissionTime = missionTime;
                    initBreakTime = missionBreakTime;
                    missionCount = mission.getGoal();
                    // post value back to view model
                    timerViewModel.getMissionTime().postValue(msTimeFormatter(missionTime));
                    timerViewModel.getMissionBreakTime().postValue(msTimeFormatter(missionBreakTime));
                }
            }
        });
    }

    public void setProgressBarValues(long milliLeft){
        fragmentTimerBinding.progressBarCircle.setMax((int) missionTime / 1000);
        fragmentTimerBinding.progressBarCircle.setProgress((int) milliLeft / 1000);
    }

    public void setBreakProgressBarValues(long milliLeft){
        fragmentTimerBinding.breakProgressBarCircle.setMax((int) breakTime / 1000);
        fragmentTimerBinding.breakProgressBarCircle.setProgress((int) milliLeft / 1000);
    }

    public void startBreakCountDownTimer(long timeMilli){
        fragmentTimerBinding.timerRelativelayout.setVisibility(View.GONE);
        fragmentTimerBinding.timerBreak.setVisibility(View.VISIBLE);

        breakCountDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                breakTimeLeft = millisUntilFinished;
                fragmentTimerBinding.breakTextViewTime.setText(msTimeFormatter(millisUntilFinished));
                fragmentTimerBinding.breakProgressBarCircle.setProgress((int) (millisUntilFinished) / 1000);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void startCountDownTimer(long timeMilli){
        missionCountDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                missionTimeLeft = millisUntilFinished;
                fragmentTimerBinding.textViewTime.setText(msTimeFormatter(millisUntilFinished));
                fragmentTimerBinding.progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                fragmentTimerBinding.textViewTime.setText(msTimeFormatter(missionTime));
                setProgressBarValues(missionTime);
                fragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                timerStatus = TimerStatus.STOPPED;
                // switch to break time mode

                initBreakTimerValue(); // init
                setBreakProgressBarValues(breakTime);
                fragmentTimerBinding.breakImageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
                fragmentTimerBinding.breakImageViewReset.setVisibility(View.GONE);
                breakTimerStatus = TimerStatus.STARTED;
                startBreakCountDownTimer(breakTime);
            }

        }.start();
    }

    public void pauseCountDownTimer(){
        missionCountDownTimer.cancel();
    }

    public void continueCountDownTimer(){
        startCountDownTimer(missionTimeLeft);
    }

    public void pauseBreakCountDownTimer(){
        breakCountDownTimer.cancel();
    }

    public void continueBreakCountDownTimer(){
        startBreakCountDownTimer(breakTimeLeft);
    }

    private String hmsTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public void initTimerValue(){
        missionTime = initMissionTime;
    }

    public void initBreakTimerValue(){
        breakTime = initBreakTime;
    }

    public class ClickProxy{
        public void missionReset(){
            // reset timer
            initTimerValue(); // init value
            fragmentTimerBinding.textViewTime.setText(msTimeFormatter(missionTime));
            setProgressBarValues(missionTime);
            timerStatus = TimerStatus.STOPPED;
            fragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        }

        public void missionStartPause(){
            if (timerStatus == TimerStatus.STOPPED) {
                initTimerValue(); // init
                setProgressBarValues(missionTime);
                fragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
                fragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
                timerStatus = TimerStatus.STARTED;
                startCountDownTimer(missionTime);
            } else if (timerStatus == TimerStatus.STARTED){
                fragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                fragmentTimerBinding.imageViewReset.setVisibility(View.VISIBLE);
                timerStatus = TimerStatus.PAUSED;
                pauseCountDownTimer();
            } else if (timerStatus == TimerStatus.PAUSED) {
                fragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
                timerStatus = TimerStatus.STARTED;
                fragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
                continueCountDownTimer();
                setProgressBarValues(missionTimeLeft);
            }
        }

        public void breakReset(){
            // reset timer
            initBreakTimerValue(); // init value
            fragmentTimerBinding.breakTextViewTime.setText(msTimeFormatter(breakTime));
            setBreakProgressBarValues(breakTime);
            breakTimerStatus = TimerStatus.STOPPED;
            fragmentTimerBinding.breakImageViewReset.setVisibility(View.GONE);
        }

        public void breakStartPause(){
            if (breakTimerStatus == TimerStatus.STOPPED) {
                initBreakTimerValue(); // init
                setBreakProgressBarValues(breakTime);
                fragmentTimerBinding.breakImageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
                fragmentTimerBinding.breakImageViewReset.setVisibility(View.GONE);
                breakTimerStatus = TimerStatus.STARTED;
                startBreakCountDownTimer(breakTime);
            } else if (breakTimerStatus == TimerStatus.STARTED){
                fragmentTimerBinding.breakImageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                fragmentTimerBinding.breakImageViewReset.setVisibility(View.VISIBLE);
                breakTimerStatus = TimerStatus.PAUSED;
                pauseBreakCountDownTimer();
            } else if (breakTimerStatus == TimerStatus.PAUSED) {
                fragmentTimerBinding.breakImageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
                breakTimerStatus = TimerStatus.STARTED;
                fragmentTimerBinding.breakImageViewReset.setVisibility(View.GONE);
                continueBreakCountDownTimer();
                setBreakProgressBarValues(breakTimeLeft);
            }
        }
    }
}
