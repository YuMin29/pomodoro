package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentBreakTimerBinding;
import com.yumin.pomodoro.ui.base.TimerViewModelFactory;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.CircleTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.util.concurrent.TimeUnit;

public class BreakTimerFragment extends DataBindingFragment {
    private static final String TAG = "[BreakTimerFragment]";
    private FragmentBreakTimerBinding fragmentBreakTimerBinding;
    private TimerViewModel timerViewModel;
    private boolean enabledVibrate;
    private boolean enabledNotification;
    private int missionCount;
    private CircleTimer breakTimer;
    private int numberOfCompletion;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationHelper notificationHelper;
    private static final int NOTIFICATION_ID = 1000;
    private String missionTitle;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        LogUtil.logD(TAG,"[onResume]");
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        LogUtil.logD(TAG,"[onStop]");
    }

    @Override
    protected void initViewModel() {
        timerViewModel = getFragmentScopeViewModel(TimerViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_break_timer, BR.viewmodel, timerViewModel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentBreakTimerBinding = (FragmentBreakTimerBinding) getBinding();

        observeViewModel();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                if (fragmentBreakTimerBinding.breakTimer.getTimerStatus() == CircleTimer.TimerStatus.STARTED)
                    fragmentBreakTimerBinding.breakTimer.pauseTimer();

                // showing a dialog to check whether to exit this page or not
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("結束任務")
                        .setMessage("確認結束任務？")
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // exit & cancel notification
                                if (fragmentBreakTimerBinding.breakTimer.getTimerStatus() != CircleTimer.TimerStatus.STOPPED) {
                                    fragmentBreakTimerBinding.breakTimer.onClickReset();
                                    notificationHelper.cancelNotification();
                                }
                                // update finish status
                                if ((missionCount - numberOfCompletion) < 1) {
                                    timerViewModel.updateIsFinishedById(true);
                                }
                                MainActivity.getNavController().navigateUp();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // resume
                                fragmentBreakTimerBinding.breakTimer.onClickStartStop();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        breakTimer = fragmentBreakTimerBinding.breakTimer;
        breakTimer.setCountDownTimerListener(new CircleTimer.CountDownTimerListener() {
            @Override
            public void onStarted() {
                // show notification in here
                if (enabledNotification) {
                    notificationHelper = new NotificationHelper(getContext());
                    Intent intent = getContext().getPackageManager()
                            .getLaunchIntentForPackage(getContext().getPackageName())
                            .setPackage(null)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
                    notificationBuilder = notificationHelper.getNotification("休息一下" + missionTitle,"",pendingIntent);
                    notificationHelper.notify(notificationBuilder);
                }
            }

            @Override
            public void onFinished() {
                if ((missionCount - numberOfCompletion) >= 1) {
                    LogUtil.logD(TAG,"[break timer][onFinished]");


                    // vibrate for remind
                    if (enabledVibrate) {
                        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                    }

                    if (enabledNotification) {
                        notificationBuilder.setContentTitle("執行蕃茄任務！");
                        notificationBuilder.setContentText("");
                        notificationHelper.notify(notificationBuilder);
                    }

                    // switch to mission timer
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemId", MissionManager.getInstance().getOperateId());
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.fragment_timer,bundle);

                } else {
                    LogUtil.logD(TAG,"[break timer][onFinished] 1");
                    // finished timer fragment
                    timerViewModel.updateIsFinishedById(true);
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.nav_home,null);

                    // cancel notification when finish the mission
                    if (enabledNotification) {
                        notificationHelper.getNotificationManager().cancel(NOTIFICATION_ID);
                    }
                }
            }

            @Override
            public void onTick(long millisecond) {
                if (enabledNotification) {
                    notificationBuilder.setContentText(msTimeFormatter(millisecond));
                    notificationHelper.notify(notificationBuilder);
                }
            }
        });
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
                    numberOfCompletion = mission.getNumberOfCompletions();
                    enabledVibrate = mission.isEnableVibrate();
                    enabledNotification = mission.isEnableNotification();
                    missionTitle = mission.getName();

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

                    // post value back to view model
                    timerViewModel.setMissionTime(msTimeFormatter(missionTime));
                    timerViewModel.setMissionBreakTime(msTimeFormatter(missionBreakTime));
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
