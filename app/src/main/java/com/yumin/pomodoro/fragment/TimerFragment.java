package com.yumin.pomodoro.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.base.MissionManager;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.PrefUtils;
import com.yumin.pomodoro.viewmodel.TimerViewModel;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends DataBindingFragment {
    private static final String TAG = TimerFragment.class.getSimpleName();
    FragmentTimerBinding mFragmentTimerBinding;
    TimerViewModel mTimerViewModel;
    boolean mEnabledVibrate;
    boolean mEnabledNotification;
    int mMissionCount;
    int mNumberOfCompletion;
    TimerStatus timerStatus = TimerStatus.MISSION_INIT;
    long mMissionTime;
    TimerStatusReceiver mTimerStatusReceiver;
    private int mMissionBackgroundColor;
    private boolean mIsAutoStartMission = false;
    private int mBreakBackgroundColor = Color.parseColor("#87CEEB");
    private long mMissionBreakTime;
    private MissionState mMissionState = null;

    public enum TimerStatus {
        MISSION_INIT,
        MISSION_START,
        MISSION_PAUSE,
        MISSION_STOP,
        MISSION_KEEP_PAUSE,
        BREAK_START,
        BREAK_PAUSE,
        BREAK_STOP,
        BREAK_KEEP_PAUSE,
        MISSION_FINISHED
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mTimerStatusReceiver = new TimerStatusReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CountDownTimerService.ACTION_TIMER_INFO);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mTimerStatusReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTimerStatusReceiver);
    }

    private void stopService(){
        Intent intent = new Intent(getActivity(), CountDownTimerService.class);
        intent.setAction(CountDownTimerService.ACTION_STOP_SERVICE);
        getActivity().stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "[onDestroy]");
        PrefUtils.clearTimerServiceStatus(getContext());
        stopService();
    }

    @Override
    protected void initViewModel() {
        mTimerViewModel = getFragmentScopeViewModel(TimerViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_timer, BR.timerViewModel, mTimerViewModel)
                .addBindingParam(BR.timerClickProxy, new ClickProxy());
    }

    void navigateUp() {
        LogUtil.logE(TAG, "[navigateUp]");
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragmentTimerBinding = (FragmentTimerBinding) getBinding();
        LogUtil.logE(TAG, "[onViewCreated]");

        mTimerViewModel.setMissionStringId(MissionManager.getInstance().getStrOperateId());
        observeViewModel();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                if (timerStatus == TimerStatus.MISSION_START || timerStatus == TimerStatus.BREAK_START)
                    pauseTimer();
                else if (timerStatus == TimerStatus.MISSION_PAUSE)
                    timerStatus = TimerStatus.MISSION_KEEP_PAUSE;
                else if (timerStatus == TimerStatus.BREAK_PAUSE)
                    timerStatus = TimerStatus.BREAK_KEEP_PAUSE;

                // showing a dialog to check whether to exit this page or not
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.finish_mission)
                        .setMessage(R.string.check_finish_mission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // exit & cancel notification
                                if (timerStatus != TimerStatus.MISSION_STOP ||
                                        timerStatus != TimerStatus.BREAK_STOP) {
                                }
                                navigateUp();

                                // update completed status
                                if (mMissionCount != -1 && mNumberOfCompletion != -1) {
                                    if ((mMissionCount - mNumberOfCompletion) < 1) {
                                        mTimerViewModel.updateMissionState(true, mNumberOfCompletion);
                                    }
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (timerStatus == TimerStatus.BREAK_PAUSE || timerStatus == TimerStatus.MISSION_PAUSE)
                                    continueTimer();
                                else if (timerStatus == TimerStatus.BREAK_KEEP_PAUSE)
                                    timerStatus = TimerStatus.BREAK_PAUSE;
                                else if (timerStatus == TimerStatus.MISSION_KEEP_PAUSE)
                                    timerStatus = TimerStatus.MISSION_PAUSE;
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    public void setStatusBarColor(int color) {
        LogUtil.logE(TAG, "[setStatusBarColor] color = " + color);
        ((MainActivity) getContext()).getWindow().setStatusBarColor(color);
    }

    private void observeViewModel() {
        mTimerViewModel.gerFetchDataResult().observe(getViewLifecycleOwner(), new Observer<TimerViewModel.Result>() {
            @Override
            public void onChanged(TimerViewModel.Result result) {
                if (result.isInit()) {
                    LogUtil.logE(TAG, "[gerFetchDataResult] isInit");
                    mNumberOfCompletion = result.missionNumberOfCompletion;
                    mMissionState = result.missionState;

                    UserMission mission = result.mission;
                    // format
                    mMissionTime = Long.valueOf(mission.getTime() * 60 * 1000);
                    mMissionBreakTime = Long.valueOf(mission.getShortBreakTime() * 60 * 1000);
                    // assign value
                    mMissionCount = mission.getGoal();
                    mEnabledVibrate = mission.isEnableVibrate();
                    mEnabledNotification = mission.isEnableNotification();
                    mMissionBackgroundColor = mission.getColor();

                    if (timerStatus == TimerStatus.MISSION_INIT) {
                        initTimerLayout(mMissionTime, mMissionBackgroundColor);
                        if (mMissionTime != 0) {
                            if (mIsAutoStartMission) {
                                startTimer();
                            }
                        }
                    }
                }
            }
        });

        mTimerViewModel.getTimerServiceStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                timerStatus = TimerStatus.values()[integer];
                if (timerStatus == TimerStatus.BREAK_START) {
                    mFragmentTimerBinding.timerRelativeLayout.setBackgroundColor(mBreakBackgroundColor);
                    setStatusBarColor(mBreakBackgroundColor);
                    initTimerValues(mMissionBreakTime);
                }

                if (timerStatus == TimerStatus.MISSION_INIT) {
                    initTimerLayout(mMissionTime, mMissionBackgroundColor);
                }

                if (timerStatus == TimerStatus.MISSION_STOP) {
                    initTimerLayout(mMissionBreakTime, mBreakBackgroundColor);
                }

                if (timerStatus == TimerStatus.MISSION_START) {
                    mFragmentTimerBinding.timerRelativeLayout.setBackgroundColor(mMissionBackgroundColor);
                    setStatusBarColor(mMissionBackgroundColor);
                    initTimerValues(mMissionTime);
                }

                if (timerStatus == TimerStatus.MISSION_FINISHED) {
                    stopService();
                    navigateUp();
                }
            }
        });

        mTimerViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mIsAutoStartMission = aBoolean;
            }
        });
    }

    String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    private void initTimerValues(long timeMilli) {
        mFragmentTimerBinding.progressBarCircle.setMax((int) timeMilli / 1000);
    }

    void setProgressBarValues(long time) {
        mFragmentTimerBinding.progressBarCircle.setProgress((int) time / 1000);
    }

    private void startService(String action){
        Intent intent = new Intent(getContext(), CountDownTimerService.class);
        intent.setAction(action);
        intent.putExtra(CountDownTimerService.EXTRA_MISSION_ID, MissionManager.getInstance().getStrOperateId());
        intent.putExtra(CountDownTimerService.EXTRA_MISSION_TIME, mMissionTime);
        intent.putExtra(CountDownTimerService.EXTRA_BREAK_TIME, mMissionBreakTime);
        getActivity().startService(intent);
    }

    private void startTimer() {
        if (mMissionState == null)
            mTimerViewModel.initMissionState();

        // changing play icon to stop icon
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
        // changing the timer status to started
        if (timerStatus == TimerStatus.MISSION_INIT) {
            timerStatus = TimerStatus.MISSION_START;
            startService(CountDownTimerService.ACTION_START_MISSION);

        } else if (timerStatus == TimerStatus.BREAK_STOP || timerStatus == TimerStatus.MISSION_STOP) {
            timerStatus = TimerStatus.BREAK_START;
            startService(CountDownTimerService.ACTION_START_BREAK);
        }
    }

    public void initTimerLayout(long time, int backgroundColor) {
        setStatusBarColor(backgroundColor);
        mFragmentTimerBinding.timerRelativeLayout.setBackgroundColor(backgroundColor);
        initTimerValues(time);
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(time));
        setProgressBarValues(time);
    }

    public void pauseTimer() {
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.VISIBLE);

        if (timerStatus == TimerStatus.MISSION_START) {
            timerStatus = TimerStatus.MISSION_PAUSE;
            startService(CountDownTimerService.ACTION_PAUSE_MISSION);
        } else if (timerStatus == TimerStatus.BREAK_START) {
            timerStatus = TimerStatus.BREAK_PAUSE;
            startService(CountDownTimerService.ACTION_PAUSE_BREAK);
        }
    }

    private void continueTimer() {
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);

        if (timerStatus == TimerStatus.MISSION_PAUSE) {
            timerStatus = TimerStatus.MISSION_START;
            startService(CountDownTimerService.ACTION_CONTINUE_MISSION);
        } else if (timerStatus == TimerStatus.BREAK_PAUSE) {
            timerStatus = TimerStatus.BREAK_START;
            startService(CountDownTimerService.ACTION_CONTINUE_BREAK);
        }
    }

    private void resetTimer() {
        long timeMilli;
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        if (timerStatus == TimerStatus.BREAK_PAUSE) {
            timerStatus = TimerStatus.BREAK_STOP;
            timeMilli = mMissionBreakTime;
            startService(CountDownTimerService.ACTION_RESET_BREAK);
        } else {
            timerStatus = TimerStatus.MISSION_INIT;
            timeMilli = mMissionTime;
            startService(CountDownTimerService.ACTION_RESET_MISSION);
        }
        initTimerValues(timeMilli);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(timeMilli));
        setProgressBarValues(timeMilli);
    }

    public class ClickProxy {
        public void onStartClick() {
            if (timerStatus == TimerStatus.MISSION_INIT) {
                startTimer();
            } else if (timerStatus == TimerStatus.MISSION_START ||
                    timerStatus == TimerStatus.BREAK_START) {
                pauseTimer();
            } else if (timerStatus == TimerStatus.MISSION_PAUSE ||
                    timerStatus == TimerStatus.BREAK_PAUSE) {
                continueTimer();
            } else if (timerStatus == TimerStatus.MISSION_STOP ||
                    timerStatus == TimerStatus.BREAK_STOP) {
                startTimer();
            }
        }

        public void onResetClick() {
            resetTimer();
        }
    }

    private class TimerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && CountDownTimerService.ACTION_TIMER_INFO.equals(intent.getAction())) {
                long timeLong = intent.getLongExtra(CountDownTimerService.EXTRA_TIME_LONG, 0);
                String timeStr = intent.getStringExtra(CountDownTimerService.EXTRA_TIME_STR);
                mFragmentTimerBinding.textViewTime.setText(timeStr);
                setProgressBarValues(timeLong);
            }
        }
    }
}
