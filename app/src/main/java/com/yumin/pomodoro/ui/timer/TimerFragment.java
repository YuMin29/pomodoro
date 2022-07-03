package com.yumin.pomodoro.ui.timer;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.main.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.base.MissionManager;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.PrefUtils;

import java.util.concurrent.TimeUnit;


public class TimerFragment extends DataBindingFragment {
    private static final String TAG = TimerFragment.class.getSimpleName();
    public static final String BUNDLE_FLOAT_VIEW_BACKGROUND_COLOR = "floatViewBackgroundColor";
    private FragmentTimerBinding mFragmentTimerBinding;
    private TimerViewModel mTimerViewModel;
    private int mMissionCount;
    private int mNumberOfCompletion;
    private TimerStatus mTimerStatus = TimerStatus.MISSION_INIT;
    private long mMissionTime;
    private TimerStatusReceiver mTimerStatusReceiver;
    private int mMissionBackgroundColor;
    private boolean mIsAutoStartMission = false;
    private int mBreakBackgroundColor;
    private long mMissionBreakTime;
    private MissionState mMissionState = null;
    private int mFloatViewBackgroundColor;

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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).fullScreenMode(true);
        mBreakBackgroundColor = ContextCompat.getColor(getActivity(),R.color.break_timer_background);
        mFloatViewBackgroundColor = this.getArguments() != null ? this.getArguments().getInt(BUNDLE_FLOAT_VIEW_BACKGROUND_COLOR,
                ContextCompat.getColor(getActivity(),R.color.colorPrimary)) : ContextCompat.getColor(getActivity(),R.color.colorPrimary);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mTimerViewModel.setMissionStringId(MissionManager.getInstance().getStrOperateId());
        observeViewModel();
        mFragmentTimerBinding = (FragmentTimerBinding) getBinding();
        // set up float view background when init timer fragment
        mFragmentTimerBinding.floatView.setBackgroundColor(mFloatViewBackgroundColor);
        mFragmentTimerBinding.floatView.setVisibility(View.VISIBLE);
        mFragmentTimerBinding.floatView.bringToFront();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LogUtil.logE(TAG, "[onViewCreated]");

        mFragmentTimerBinding.closeIcon.setPadding(0,getStatusBarHeight(),0,0);
        mFragmentTimerBinding.closeIcon.setOnClickListener(v -> stopTimer());


        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                stopTimer();
            }
        });

        mFragmentTimerBinding.waveView.setWaveStart(true);
        mFragmentTimerBinding.textViewTime.bringToFront();
    }

    private void stopTimer() {
        // pause timer if started
        if (mTimerStatus == TimerStatus.MISSION_START || mTimerStatus == TimerStatus.BREAK_START)
            pauseTimer();
        else if (mTimerStatus == TimerStatus.MISSION_PAUSE)
            mTimerStatus = TimerStatus.MISSION_KEEP_PAUSE;
        else if (mTimerStatus == TimerStatus.BREAK_PAUSE)
            mTimerStatus = TimerStatus.BREAK_KEEP_PAUSE;

        // showing a dialog to check whether to exit this page or not
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.finish_mission)
                .setMessage(R.string.check_finish_mission)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // exit & cancel notification
                    if (mTimerStatus != TimerStatus.MISSION_STOP ||
                            mTimerStatus != TimerStatus.BREAK_STOP) {
                    }
                    navigateUp();

                    // update completed status
                    if (mMissionCount != -1 && mNumberOfCompletion != -1) {
                        if ((mMissionCount - mNumberOfCompletion) < 1) {
                            mTimerViewModel.updateMissionState(true, mNumberOfCompletion);
                        }
                    }
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (mTimerStatus == TimerStatus.BREAK_PAUSE || mTimerStatus == TimerStatus.MISSION_PAUSE)
                        continueTimer();
                    else if (mTimerStatus == TimerStatus.BREAK_KEEP_PAUSE)
                        mTimerStatus = TimerStatus.BREAK_PAUSE;
                    else if (mTimerStatus == TimerStatus.MISSION_KEEP_PAUSE)
                        mTimerStatus = TimerStatus.MISSION_PAUSE;
                })
                .create();
        alertDialog.show();
    }

    private void observeViewModel() {
        mTimerViewModel.gerFetchDataResult().observe(getViewLifecycleOwner(), result -> {
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
                mMissionBackgroundColor = mission.getColor();

                if (mTimerStatus == TimerStatus.MISSION_INIT) {
                    initTimerLayout(mMissionTime, mMissionBackgroundColor);
                    if (mMissionTime != 0) {
                        if (mIsAutoStartMission) {
                            startTimer();
                        }
                    }
                }

                // floatView invisible
                mFragmentTimerBinding.floatView.setVisibility(View.INVISIBLE);
            }
        });

        mTimerViewModel.getTimerServiceStatus().observe(getViewLifecycleOwner(), integer -> {
            mTimerStatus = TimerStatus.values()[integer];
            if (mTimerStatus == TimerStatus.BREAK_START) {
                mFragmentTimerBinding.timerConstraintLayout.setBackgroundColor(mBreakBackgroundColor);
            }

            if (mTimerStatus == TimerStatus.MISSION_INIT) {
                initTimerLayout(mMissionTime, mMissionBackgroundColor);
            }

            if (mTimerStatus == TimerStatus.MISSION_STOP) {
                initTimerLayout(mMissionBreakTime, mBreakBackgroundColor);
            }

            if (mTimerStatus == TimerStatus.MISSION_START) {
                mFragmentTimerBinding.timerConstraintLayout.setBackgroundColor(mMissionBackgroundColor);
            }

            if (mTimerStatus == TimerStatus.MISSION_FINISHED) {
                stopService();
                navigateUp();
            }
        });

        mTimerViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), aBoolean -> mIsAutoStartMission = aBoolean);
    }

    String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    private void startService(String action){
        Intent intent = new Intent(getContext(), CountDownTimerService.class);
        intent.setAction(action);
        intent.putExtra(CountDownTimerService.EXTRA_MISSION_ID, MissionManager.getInstance().getStrOperateId());
        intent.putExtra(CountDownTimerService.EXTRA_MISSION_TIME, mMissionTime);
        intent.putExtra(CountDownTimerService.EXTRA_BREAK_TIME, mMissionBreakTime);
        getActivity().startService(intent);
    }

    private Point getWindowSize(){
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    private void startTimer() {
        LogUtil.logD(TAG,"[startTimer]");
        if (mMissionState == null)
            mTimerViewModel.initMissionState();

        mFragmentTimerBinding.imageViewStartPause.setText(R.string.timer_pause);

        // changing the timer status to started
        if (mTimerStatus == TimerStatus.MISSION_INIT) {
            mTimerStatus = TimerStatus.MISSION_START;
            startService(CountDownTimerService.ACTION_START_MISSION);

        } else if (mTimerStatus == TimerStatus.BREAK_STOP || mTimerStatus == TimerStatus.MISSION_STOP) {
            mTimerStatus = TimerStatus.BREAK_START;
            startService(CountDownTimerService.ACTION_START_BREAK);
        }
    }

    public void initTimerLayout(long time, int backgroundColor) {
        mFragmentTimerBinding.timerConstraintLayout.setBackgroundColor(backgroundColor);
        mFragmentTimerBinding.imageViewStartPause.setText(R.string.timer_start);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(time));
    }

    public void pauseTimer() {
        mFragmentTimerBinding.imageViewStartPause.setText(R.string.timer_start);
        mFragmentTimerBinding.imageViewStartPause.setVisibility(View.INVISIBLE);

        double toX = getWindowSize().x * 0.25; // move x
        mFragmentTimerBinding.stop.setVisibility(View.VISIBLE);
        mFragmentTimerBinding.stop.animate().translationX((float) toX)
                .setDuration(500)
                .alpha((float) 1.0)
                .setInterpolator(new DecelerateInterpolator());

        mFragmentTimerBinding.timerContinue.setVisibility(View.VISIBLE);
        mFragmentTimerBinding.timerContinue.animate().translationX((float) -toX)
                .setDuration(500)
                .alpha((float) 1.0)
                .setInterpolator(new DecelerateInterpolator());

        if (mTimerStatus == TimerStatus.MISSION_START) {
            mTimerStatus = TimerStatus.MISSION_PAUSE;
            startService(CountDownTimerService.ACTION_PAUSE_MISSION);
        } else if (mTimerStatus == TimerStatus.BREAK_START) {
            mTimerStatus = TimerStatus.BREAK_PAUSE;
            startService(CountDownTimerService.ACTION_PAUSE_BREAK);
        }
    }

    private void continueTimer() {
        LogUtil.logD(TAG,"[continueTimer]");
        mFragmentTimerBinding.imageViewStartPause.setText(R.string.timer_pause);
        mFragmentTimerBinding.stop.animate().translationX((float) 0)
                .setDuration(500)
                .alpha((float) 0.0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mTimerStatus == TimerStatus.MISSION_START) {
                            LogUtil.logD(TAG,"[onAnimationEnd]");
                            mFragmentTimerBinding.timerContinue.setVisibility(View.INVISIBLE);
                            mFragmentTimerBinding.stop.setVisibility(View.INVISIBLE);
                            mFragmentTimerBinding.imageViewStartPause.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

        mFragmentTimerBinding.timerContinue.animate().translationX((float) 0)
                .setDuration(500)
                .alpha((float) 0.0)
                .setInterpolator(new DecelerateInterpolator());

        if (mTimerStatus == TimerStatus.MISSION_PAUSE) {
            mTimerStatus = TimerStatus.MISSION_START;
            startService(CountDownTimerService.ACTION_CONTINUE_MISSION);
        } else if (mTimerStatus == TimerStatus.BREAK_PAUSE) {
            mTimerStatus = TimerStatus.BREAK_START;
            startService(CountDownTimerService.ACTION_CONTINUE_BREAK);
        }
    }

    public class ClickProxy {
        public void onStartClick() {
            if (mTimerStatus == TimerStatus.MISSION_INIT) {
                startTimer();
            } else if (mTimerStatus == TimerStatus.MISSION_START ||
                    mTimerStatus == TimerStatus.BREAK_START) {
                pauseTimer();
            } else if (mTimerStatus == TimerStatus.MISSION_PAUSE ||
                    mTimerStatus == TimerStatus.BREAK_PAUSE) {
                continueTimer();
            } else if (mTimerStatus == TimerStatus.MISSION_STOP ||
                    mTimerStatus == TimerStatus.BREAK_STOP) {
                startTimer();
            }
        }

        public void onStopClick() {
            LogUtil.logD(TAG,"[onStopClick]");
            stopTimer();
        }
    }

    private class TimerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && CountDownTimerService.ACTION_TIMER_INFO.equals(intent.getAction())) {
                String timeStr = intent.getStringExtra(CountDownTimerService.EXTRA_TIME_STR);
                mFragmentTimerBinding.textViewTime.setText(timeStr);
                mFragmentTimerBinding.textViewTime.bringToFront(); // on top
            }
        }
    }
}
