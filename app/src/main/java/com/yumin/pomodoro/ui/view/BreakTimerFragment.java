package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.activity.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentBreakTimerBinding;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.ui.view.customize.CircleTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;
import com.yumin.pomodoro.ui.base.MissionManager;

import java.util.concurrent.TimeUnit;

public class BreakTimerFragment extends DataBindingFragment {
    private static final String TAG = BreakTimerFragment.class.getSimpleName();
    private FragmentBreakTimerBinding mFragmentBreakTimerBinding;
    private TimerViewModel mTimerViewModel;
    private boolean mEnabledVibrate;
    private boolean mEnabledNotification;
    private int mMissionCount;
    private CircleTimer mBreakTimer;
    private int mNumberOfCompletion;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationHelper mNotificationHelper;
    private static final int NOTIFICATION_ID = 1000;
    private String mMissionTitle;
    private boolean mIsAutoStartBreak = false;
    private Handler mHandler;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    protected void initViewModel() {
        mTimerViewModel = getFragmentScopeViewModel(TimerViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_break_timer, BR.breakTimerViewModel, mTimerViewModel);
    }

    private void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragmentBreakTimerBinding = (FragmentBreakTimerBinding) getBinding();
        mFragmentBreakTimerBinding.breakTimer.setStatusBarColor(
                getContext().getResources().getColor(R.color.break_timer_background));

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    LogUtil.logE(TAG, "[handleMessage] call onClickStartStop");
                    mFragmentBreakTimerBinding.breakTimer.onClickStartStop();
                }
                return true;
            }
        });

        observeViewModel();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mFragmentBreakTimerBinding.breakTimer.getTimerStatus() == CircleTimer.TimerStatus.STARTED)
                    mFragmentBreakTimerBinding.breakTimer.pauseTimer();

                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.finish_mission)
                        .setMessage(R.string.check_finish_mission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mFragmentBreakTimerBinding.breakTimer.getTimerStatus() != CircleTimer.TimerStatus.STOPPED) {
                                    mFragmentBreakTimerBinding.breakTimer.onClickReset();
                                    if (mEnabledNotification)
                                        mNotificationHelper.cancelNotification();
                                }

                                navigateUp();
                                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                                undoStatusBarColor();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFragmentBreakTimerBinding.breakTimer.onClickStartStop();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        mBreakTimer = mFragmentBreakTimerBinding.breakTimer;
        mBreakTimer.setCountDownTimerListener(new CircleTimer.CountDownTimerListener() {
            @Override
            public void onStarted() {
                if (mEnabledNotification) {
                    Intent intent = getContext().getPackageManager()
                            .getLaunchIntentForPackage(getContext().getPackageName())
                            .setPackage(null)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                    mNotificationHelper = new NotificationHelper(getContext());
                    mNotificationBuilder = mNotificationHelper.getNotificationBuilder(getString(R.string.notification_title) + mMissionTitle, pendingIntent,
                            ContextCompat.getColor(getContext(), R.color.break_timer_background));
                    mNotificationHelper.notify(mNotificationBuilder);
                }
            }

            @Override
            public void onFinished() {
                LogUtil.logD(TAG, "[break timer][onFinished] missionCount = " + mMissionCount + " ,numberOfCompletion = " + mNumberOfCompletion);
                if (mMissionCount != -1 && mNumberOfCompletion != -1) {
                    if ((mMissionCount - mNumberOfCompletion) >= 1) {
                        // vibrate for remind
                        if (mEnabledVibrate) {
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                        }

                        if (mEnabledNotification) {
                            mNotificationHelper.changeRemoteContent(getString(R.string.notification_mission_message));
                            mNotificationHelper.notify(mNotificationBuilder);
                        }

                        // switch to mission timer
                        Bundle bundle = new Bundle();
                        bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
                        MainActivity.commitWhenLifecycleStarted(getLifecycle(), R.id.break_timer_to_timer, bundle);
                    } else {
                        navigateUp();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                        undoStatusBarColor();

                        // cancel notification when finish the mission
                        if (mEnabledNotification) {
                            mNotificationHelper.cancelNotification();
                        }
                    }
                } else {
                    navigateUp();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    undoStatusBarColor();
                }
            }

            @Override
            public void onTick(long millisecond) {
                if (mEnabledNotification) {
                    mNotificationHelper.changeRemoteContent(msTimeFormatter(millisecond));
                    mNotificationHelper.notify(mNotificationBuilder);
                }
            }
        });
    }

    private void undoStatusBarColor() {
        ((MainActivity) getContext()).getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    private void observeViewModel() {
        mTimerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                if (mission != null) {
                    long missionTime = Long.valueOf(mission.getTime() * 60 * 1000);
                    long missionBreakTime = Long.valueOf(mission.getShortBreakTime() * 60 * 1000);
                    // assign value
                    mMissionCount = mission.getGoal();
                    mEnabledVibrate = mission.isEnableVibrate();
                    mEnabledNotification = mission.isEnableNotification();
                    mMissionTitle = mission.getName();

                    if (mission.isKeepScreenOn()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            getActivity().setTurnScreenOn(true);
                        } else {
                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            getActivity().setTurnScreenOn(false);
                        } else {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                        }
                    }

                    if (missionTime != 0) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.logE(TAG, "isAutoStartBreak = " + mIsAutoStartBreak);
                                if (mIsAutoStartBreak) {
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        });
                        thread.start();
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

        mTimerViewModel.getAutoStartBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mIsAutoStartBreak = aBoolean;
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
