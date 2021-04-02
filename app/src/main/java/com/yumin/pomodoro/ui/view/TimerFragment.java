package com.yumin.pomodoro.ui.view;

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
import com.yumin.pomodoro.activity.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.base.MissionManager;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.EventCountdownTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends DataBindingFragment implements EventCountdownTimer.MissionTimerListener, EventCountdownTimer.BreakTimerListener {
    private static final String TAG = "[TimerFragment]";
    final int NOTIFICATION_ID = 1000;
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
    private EventCountdownTimer eventCountdownTimer;
    TimerStatus timerStatus = TimerStatus.MISSION_INIT;
    private int mBreakBackgroundColor = Color.parseColor("#87CEEB");
    long missionTime;
    private long missionBreakTime;
    private boolean mIsAutoStartBreak = false;

    public enum TimerStatus {
        MISSION_INIT,
        MISSION_START,
        MISSION_PAUSE,
        MISSION_STOP,
        BREAK_START,
        BREAK_PAUSE,
        BREAK_STOP
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
        LogUtil.logE(TAG,"navigateUp");
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

        eventCountdownTimer = new EventCountdownTimer(getContext(),this,this);
        mNotificationHelper = new NotificationHelper(getContext());
        observeViewModel();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                pauseTimer();

                // showing a dialog to check whether to exit this page or not
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("結束任務")
                        .setMessage("確認結束任務？")
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
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

                                // update finish status
                                if (mMissionCount != -1 && mNumberOfCompletion != -1) {
                                    if ((mMissionCount - mNumberOfCompletion) < 1) {
                                        mTimerViewModel.updateMissionFinishedState(true, mNumberOfCompletion);
                                    }
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // resume
                                startTimer();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    public void setStatusBarColor(int color){
        LogUtil.logE(TAG,"[setStatusBarColor] color = "+color);
        ((MainActivity) getContext()).getWindow().setStatusBarColor(color);
    }

    private void observeViewModel() {
        // TODO: 2/8/21 Use Mediator to observe mission from view model
        mTimerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logE(TAG, "[OBSERVE] getMission");
                if (mission != null) {
                    // format
                    missionTime = Long.valueOf(mission.getTime() * 1 * 1000);
                    missionBreakTime = Long.valueOf(mission.getShortBreakTime() * 1 * 1000);
                    // assign value
                    mMissionCount = mission.getGoal();
                    mEnabledVibrate = mission.isEnableVibrate();
                    mEnabledNotification = mission.isEnableNotification();
                    mEnableKeepScreenOn = mission.isKeepScreenOn();
                    mMissionTitle = mission.getName();
                    mMissionBackgroundColor = mission.getColor();

                    initTimerLayout(missionTime,mMissionBackgroundColor);

                    // auto start in here ???
                    if (missionTime != 0) {
                        if (mIsAutoStartMission && mIndexOfBackgroundMusic != -1) {
                            startTimer();
                        }
                    }
                }
            }
        });

        mTimerViewModel.getMissionNumberOfCompletion().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG, "[OBSERVE] getNumberOfCompletion = " + integer);
                mNumberOfCompletion = integer;
            }
        });

        mTimerViewModel.getMissionState().observe(getViewLifecycleOwner(), new Observer<MissionState>() {
            @Override
            public void onChanged(MissionState missionState) {
                LogUtil.logE(TAG, "[OBSERVE] getMissionState = " + missionState);
            }
        });

        mTimerViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG, "[OBSERVE][getAutoStartNextMission] aBoolean = " + aBoolean);
                mIsAutoStartMission = aBoolean;
            }
        });

        mTimerViewModel.getAutoStartBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mIsAutoStartBreak = aBoolean;
            }
        });

        mTimerViewModel.getDisableBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG, "[OBSERVE][getDisableBreak] aBoolean = " + aBoolean);
                mIsDisableBreak = aBoolean;
            }
        });

        mTimerViewModel.getIndexOfMissionBackgroundRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG, "[OBSERVE][getIndexOfMissionBackgroundRingtone] INDEX = " + integer);
                mIndexOfBackgroundMusic = integer;
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
        // changing play icon to stop icon
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_pause_24);
        // changing the timer status to started
        if (timerStatus == TimerStatus.MISSION_INIT) {
            timerStatus = TimerStatus.MISSION_START;
            eventCountdownTimer.startMissionCountdown(missionTime);
            createNotification("蕃茄任務:" + mMissionTitle, mMissionBackgroundColor);
        } else if (timerStatus == TimerStatus.BREAK_STOP ||
                timerStatus == TimerStatus.MISSION_STOP) {
            timerStatus = TimerStatus.BREAK_START;
            eventCountdownTimer.startBreakCountdown(missionBreakTime);
            createNotification("蕃茄任務:" + mMissionTitle, mBreakBackgroundColor);
        }

        if (mEnableKeepScreenOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                getActivity().setTurnScreenOn(true);
            } else {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        }
    }

    public void initTimerLayout(long time, int backgroundColor) {
        LogUtil.logE(TAG,"[initTimerLayout]");
        setStatusBarColor(backgroundColor);
        mFragmentTimerBinding.timerRelativeLayout.setBackgroundColor(backgroundColor);
        initTimerValues(time);
        mFragmentTimerBinding.imageViewStartPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mFragmentTimerBinding.imageViewReset.setVisibility(View.GONE);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(time));
        setProgressBarValues(time);
    }

    private void createNotification(String s, int mMissionBackgroundColor) {
        // show notification in here
        if (mEnabledNotification) {
            // Create an explicit intent for an Activity in your app
            Intent intent = getContext().getPackageManager()
                    .getLaunchIntentForPackage(getContext().getPackageName())
                    .setPackage(null)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            mNotificationBuilder = mNotificationHelper.getNotification(s, pendingIntent, mMissionBackgroundColor);
            mNotificationHelper.notify(mNotificationBuilder);
        }
    }

    @Override
    public void onMissionTimerTickResponse(long response) {
        LogUtil.logE(TAG,"[onMissionTimerTickResponse]");
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
                mTimerViewModel.updateMissionFinishedState(true, mNumberOfCompletion);

                if (mIsDisableBreak) {
                    //                Bundle bundle = new Bundle();
//                bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.timer_to_home,null);
                    navigateUp();

                    // cancel notification when finish the mission
                    if (mEnabledNotification) {
                        mNotificationHelper.getNotificationManager().cancel(NOTIFICATION_ID);
                    }
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
            updateNotification("休息一下吧！");
        }

        if (mIsDisableBreak) {
            // repeat mission timer again
            timerStatus = TimerStatus.MISSION_INIT;
            initTimerLayout(missionTime,mMissionBackgroundColor);
            updateNotification("執行蕃茄任務！");
        } else {
            // switch to break timer
            initTimerLayout(missionBreakTime,mBreakBackgroundColor);

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
        // change ui
        setProgressBarValues(response);
        mFragmentTimerBinding.textViewTime.setText(msTimeFormatter(response));

        if (mEnabledNotification) {
            updateNotification(msTimeFormatter(response));
        }
    }

    @Override
    public void onBreakTimerFinishedResponse() {
        LogUtil.logD(TAG,"[break timer][onFinished] 1");
        LogUtil.logD(TAG,"[break timer][onFinished] missionCount = "+mMissionCount+
                " ,numberOfCompletion = "+mNumberOfCompletion);
        timerStatus = TimerStatus.BREAK_STOP;
        if (mMissionCount != -1 && mNumberOfCompletion != -1) {
            if ((mMissionCount - mNumberOfCompletion) >= 1) {
                LogUtil.logD(TAG,"[break timer][onFinished] 2");
                // vibrate for remind
                if (mEnabledVibrate) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                }

                if (mEnabledNotification) {
                    updateNotification("執行蕃茄任務！");
                }

                // switch to mission timer
                initTimerLayout(missionTime,mMissionBackgroundColor);
                timerStatus = TimerStatus.MISSION_INIT;

                if (mIsAutoStartMission)
                    startTimer();

            } else {
                LogUtil.logD(TAG,"[break timer][onFinished] 3");
                MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.timer_to_home,null);
                navigateUp();

                // cancel notification when finish the mission
                if (mEnabledNotification) {
                    mNotificationHelper.getNotificationManager().cancel(NOTIFICATION_ID);
                }
            }
        } else {
            //                Bundle bundle = new Bundle();
//                bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
            MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.timer_to_home,null);
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
            eventCountdownTimer.continueMissionCount();
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
            timeMilli = missionBreakTime;
        } else {
            timerStatus = TimerStatus.MISSION_INIT;
            timeMilli = missionTime;
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
