package com.yumin.pomodoro.fragment;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.EventCountdownTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends DataBindingFragment implements EventCountdownTimer.MissionTimerListener, EventCountdownTimer.BreakTimerListener {
    private static final String TAG = TimerFragment.class.getSimpleName();
    FragmentTimerBinding mFragmentTimerBinding;
    TimerViewModel mTimerViewModel;
    boolean mEnabledVibrate;
    boolean mEnabledNotification;
    private boolean mEnableKeepScreenOn;
    int mMissionCount;
    int mNumberOfCompletion;
    NotificationCompat.Builder mNotificationBuilder;
    NotificationHelper mNotificationHelper;
    private String mMissionTitle;
    private int mMissionBackgroundColor;
    private boolean mIsAutoStartMission = false;
    boolean mIsDisableBreak = false;
    private int mIndexOfBackgroundMusic = -1;
    private int mIndexOfRingtone = -1;
    private EventCountdownTimer eventCountdownTimer;
    TimerStatus timerStatus = TimerStatus.MISSION_INIT;
    private int mBreakBackgroundColor = Color.parseColor("#87CEEB");
    long mMissionTime;
    private long mMissionBreakTime;
    private boolean mIsAutoStartBreak = false;
    private boolean mEnabledSound = false;
    private MissionState mMissionState = null;

    @Override
    public boolean enabledSound() {
        return mEnabledSound;
    }

    public enum TimerStatus {
        MISSION_INIT,
        MISSION_START,
        MISSION_PAUSE,
        MISSION_STOP,
        MISSION_KEEP_PAUSE,
        BREAK_START,
        BREAK_PAUSE,
        BREAK_STOP,
        BREAK_KEEP_PAUSE
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        LogUtil.logD(TAG, "[onResume]");
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
        if (mEnableKeepScreenOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                getActivity().setTurnScreenOn(false);
            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragmentTimerBinding = (FragmentTimerBinding) getBinding();
        LogUtil.logE(TAG, "[onViewCreated]");

        eventCountdownTimer = new EventCountdownTimer(getContext(), this, this);
        mNotificationHelper = new NotificationHelper(getContext());
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
                                    resetTimer();
                                    if (mEnabledNotification)
                                        mNotificationHelper.cancelNotification();
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
        mTimerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                if (mission != null) {
                    // format
                    mMissionTime = Long.valueOf(mission.getTime() * 1 * 1000);
                    mMissionBreakTime = Long.valueOf(mission.getShortBreakTime() * 1 * 1000);
                    // assign value
                    mMissionCount = mission.getGoal();
                    mEnabledVibrate = mission.isEnableVibrate();
                    mEnabledNotification = mission.isEnableNotification();
                    mEnableKeepScreenOn = mission.isKeepScreenOn();
                    mMissionTitle = mission.getName();
                    mMissionBackgroundColor = mission.getColor();
                    mEnabledSound = mission.isEnableSound();

                    if (timerStatus == TimerStatus.MISSION_INIT) {
                        LogUtil.logE(TAG,"[Observe] getMission timerStatus == TimerStatus.MISSION_INIT");
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

        mTimerViewModel.getMissionNumberOfCompletion().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG, "[getNumberOfCompletion] = " + integer);
                mNumberOfCompletion = integer;
            }
        });

        mTimerViewModel.getMissionState().observe(getViewLifecycleOwner(), new Observer<MissionState>() {
            @Override
            public void onChanged(MissionState missionState) {
                //TODO 20210613 Change init mission state logic to other place
                LogUtil.logE(TAG, "[getMissionState] = " + missionState);
                mMissionState = missionState;
            }
        });

        mTimerViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG, "[getAutoStartNextMission] = " + aBoolean);
                mIsAutoStartMission = aBoolean;
            }
        });

        mTimerViewModel.getAutoStartBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG, "[getDisableBreak] = " + aBoolean);
                mIsAutoStartBreak = aBoolean;
            }
        });

        mTimerViewModel.getDisableBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG, "[getDisableBreak] = " + aBoolean);
                mIsDisableBreak = aBoolean;
            }
        });

        mTimerViewModel.getIndexOfMissionBackgroundRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG, "[getIndexOfMissionBackgroundRingtone] index = " + integer);
                mIndexOfBackgroundMusic = integer;
            }
        });

        mTimerViewModel.getIndexOfFinishedMissionRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG, "[getIndexOfFinishedMissionRingtone] index = " + integer);
                mIndexOfRingtone = integer;
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
        LogUtil.logE(TAG, "[initTimerValues]");
        mFragmentTimerBinding.progressBarCircle.setMax((int) timeMilli / 1000);
    }

    void setProgressBarValues(long time) {
        LogUtil.logE(TAG, "[setProgressBarValues]");
        mFragmentTimerBinding.progressBarCircle.setProgress((int) time / 1000);
    }

    private void startTimer() {
        int backgroundColor = 0;
        // changing play icon to stop icon
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
        // changing the timer status to started
        if (timerStatus == TimerStatus.MISSION_INIT) {
            timerStatus = TimerStatus.MISSION_START;
            eventCountdownTimer.startMissionCountdown(mMissionTime,mIndexOfBackgroundMusic,mIndexOfRingtone);
            backgroundColor = mMissionBackgroundColor;
        } else if (timerStatus == TimerStatus.BREAK_STOP || timerStatus == TimerStatus.MISSION_STOP) {
            timerStatus = TimerStatus.BREAK_START;
            eventCountdownTimer.startBreakCountdown(mMissionBreakTime);
            backgroundColor = mBreakBackgroundColor;
        }

        if (mEnabledNotification)
            createNotification(getString(R.string.notification_title) + mMissionTitle, backgroundColor);

        if (mEnableKeepScreenOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                getActivity().setTurnScreenOn(true);
            } else {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        }

        //TODO 20210613 Change init mission state logic to other place
        if (mMissionState == null)
            mTimerViewModel.initMissionState();
    }

    public void initTimerLayout(long time, int backgroundColor) {
        LogUtil.logE(TAG, "[initTimerLayout]");
        setStatusBarColor(backgroundColor);
        mFragmentTimerBinding.timerRelativeLayout.setBackgroundColor(backgroundColor);
        initTimerValues(time);
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(time));
        setProgressBarValues(time);
    }

    private void createNotification(String title, int backgroundColor) {
        // show notification in here
        // Create an explicit intent for an Activity in your app
        Intent intent = getContext().getPackageManager()
                .getLaunchIntentForPackage(getContext().getPackageName())
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        mNotificationBuilder = mNotificationHelper.getNotificationBuilder(title, pendingIntent, backgroundColor);
        mNotificationHelper.notify(mNotificationBuilder);
    }

    @Override
    public void onMissionTimerTickResponse(long response) {
        LogUtil.logE(TAG, "[onMissionTimerTickResponse]");
        // update progress bar & left time milliseconds
        setProgressBarValues(response);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(response));

        if (mEnabledNotification) {
            updateNotification(msTimeFormatter(response));
        }
    }

    @Override
    public void onMissionTimerFinishedResponse() {
        timerStatus = TimerStatus.MISSION_STOP;

        LogUtil.logD(TAG, "[mission timer][onFinished]");
        if (mNumberOfCompletion != -1) {
            // update finished goal ui
            mNumberOfCompletion++;
            LogUtil.logD(TAG, "[mission timer][onFinish] numberOfCompletion = " + mNumberOfCompletion);

            mTimerViewModel.updateMissionNumberOfCompletion(mNumberOfCompletion);
            if (mNumberOfCompletion == mMissionCount) {
                // Finished mission
                LogUtil.logD(TAG, "[mission timer][onFinished] mNumberOfCompletion == missionCount");
                mTimerViewModel.updateMissionState(true, mNumberOfCompletion);

                if (mIsDisableBreak) {
                    if (mEnabledNotification) {
                        mNotificationHelper.cancelNotification();
                    }
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(), R.id.timer_to_home, null);
                    navigateUp();
                    return;
                }
            }
        }

        // vibrate for remind
        if (mEnabledVibrate) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

        // update notification
        if (mEnabledNotification && !mIsDisableBreak) {
            updateNotification(getString(R.string.notification_break_message));
        }

        if (mIsDisableBreak) {
            // repeat mission timer again
            timerStatus = TimerStatus.MISSION_INIT;
            initTimerLayout(mMissionTime, mMissionBackgroundColor);
            updateNotification(getString(R.string.notification_mission_message));

            if (mIsAutoStartMission)
                startTimer();

        } else {
            // switch to break
            LogUtil.logE(TAG, "switch to break timer");
            initTimerLayout(mMissionBreakTime, mBreakBackgroundColor);

            if (mIsAutoStartBreak)
                startTimer();
        }
    }

    private void updateNotification(String s) {
        mNotificationHelper.changeRemoteContent(s);
        mNotificationHelper.notify(mNotificationBuilder);
    }

    @Override
    public void onBreakTimerTickResponse(long response) {
        setProgressBarValues(response);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(response));

        if (mEnabledNotification) {
            updateNotification(msTimeFormatter(response));
        }
    }

    @Override
    public void onBreakTimerFinishedResponse() {
        LogUtil.logD(TAG, "[break timer][onFinished] missionCount = " + mMissionCount +
                " ,numberOfCompletion = " + mNumberOfCompletion);
        timerStatus = TimerStatus.BREAK_STOP;
        if (mMissionCount != -1 && mNumberOfCompletion != -1) {
            if ((mMissionCount - mNumberOfCompletion) >= 1) {
                // vibrate for remind
                if (mEnabledVibrate) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                }

                if (mEnabledNotification) {
                    updateNotification(getString(R.string.notification_mission_message));
                }

                // switch to mission timer
                initTimerLayout(mMissionTime, mMissionBackgroundColor);
                timerStatus = TimerStatus.MISSION_INIT;

                if (mIsAutoStartMission)
                    startTimer();

            } else {
                MainActivity.commitWhenLifecycleStarted(getLifecycle(), R.id.timer_to_home, null);
                navigateUp();

                // cancel notification when finish the mission
                if (mEnabledNotification) {
                    mNotificationHelper.cancelNotification();
                }
            }
        } else {
            MainActivity.commitWhenLifecycleStarted(getLifecycle(), R.id.timer_to_home, null);
            navigateUp();
        }
    }

    public void pauseTimer() {
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.VISIBLE);

        if (timerStatus == TimerStatus.MISSION_START) {
            timerStatus = TimerStatus.MISSION_PAUSE;
            eventCountdownTimer.pauseMissionCountdown();
        } else if (timerStatus == TimerStatus.BREAK_START) {
            eventCountdownTimer.pauseBreakCountdown();
            timerStatus = TimerStatus.BREAK_PAUSE;
        }
    }

    private void continueTimer() {
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);

        if (timerStatus == TimerStatus.MISSION_PAUSE) {
            timerStatus = TimerStatus.MISSION_START;
            eventCountdownTimer.continueMissionCount(mIndexOfBackgroundMusic);
        } else if (timerStatus == TimerStatus.BREAK_PAUSE) {
            timerStatus = TimerStatus.BREAK_START;
            eventCountdownTimer.continueBreakCount();
        }
    }

    private void resetTimer() {
        long timeMilli;
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        if (timerStatus == TimerStatus.BREAK_PAUSE) {
            timerStatus = TimerStatus.BREAK_STOP;
            timeMilli = mMissionBreakTime;
        } else {
            timerStatus = TimerStatus.MISSION_INIT;
            timeMilli = mMissionTime;
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
}
