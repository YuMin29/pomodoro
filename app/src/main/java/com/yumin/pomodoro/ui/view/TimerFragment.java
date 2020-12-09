package com.yumin.pomodoro.ui.view;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkBuilder;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.base.TimerViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.CircleTimer;
import com.yumin.pomodoro.utils.CircleTimerReceiver;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment {
    private static final String TAG = "[TimerFragment]";
    private FragmentTimerBinding fragmentTimerBinding;
    private TimerViewModel timerViewModel;
    private boolean enabledVibrate;
    private boolean enabledNotification;
    private int itemId;
    private int missionCount;
    private CircleTimer missionTimer;
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
    public void onPause() {
        super.onPause();
        LogUtil.logD(TAG,"[onPause]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.logD(TAG,"[onDestroy]");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logD(TAG,"[onCreate]");
        Bundle bundle = getArguments();
        if (bundle != null)
            itemId = bundle.getInt("itemId");
        LogUtil.logD(TAG,"[onCreateView] itemId = "+itemId);
        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        observeViewModel();
        fragmentTimerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer,container,false);
        fragmentTimerBinding.setLifecycleOwner(this);
        fragmentTimerBinding.setViewmodel(timerViewModel);
        missionTimer = fragmentTimerBinding.missionTimer;
        missionTimer.setOnFinishCountDownListener(new CircleTimer.OnFinishCountDownListener() {
            @Override
            public void onStarted() {
                // show notification in here
                if (enabledNotification) {
                    // Create an explicit intent for an Activity in your app
                    Intent intent = getContext().getPackageManager()
                            .getLaunchIntentForPackage(getContext().getPackageName())
                            .setPackage(null)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                    notificationHelper = new NotificationHelper(getContext());
                    notificationBuilder = notificationHelper.getNotification("蕃茄任務:" + missionTitle,"執行中",pendingIntent);
                    notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                }
            }

            @Override
            public void onFinished() {
                LogUtil.logD(TAG,"[mission timer][onFinished]");
                // update finished goal ui
                numberOfCompletion++;
                LogUtil.logD(TAG,"[mission timer][onFinish] numberOfCompletion = "+numberOfCompletion);
                timerViewModel.updateNumberOfCompletionById(numberOfCompletion);

                // switch to break timer
                fragmentTimerBinding.missionTimer.setVisibility(View.GONE);
                fragmentTimerBinding.breakTimer.setVisibility(View.VISIBLE);

                // vibrate for remind
                if (enabledVibrate) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                }

                if (enabledNotification) {
                    notificationBuilder.setContentTitle("休息一下吧！");
                    notificationBuilder.setContentText("");
                    notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                }
            }

            @Override
            public void onTick(long millisecond) {
                if (enabledNotification) {
                    notificationBuilder.setContentText(msTimeFormatter(millisecond));
                    notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                }
            }
        });

        breakTimer = fragmentTimerBinding.breakTimer;
        breakTimer.setOnFinishCountDownListener(new CircleTimer.OnFinishCountDownListener() {
            @Override
            public void onStarted() {
                // show notification in here
                if (enabledNotification) {
                    notificationBuilder.setContentTitle("休息一下");
                    notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                }
            }

            @Override
            public void onFinished() {
                if ((missionCount - numberOfCompletion) >= 1) {
                    LogUtil.logD(TAG,"[break timer][onFinished]");
                    // switch to mission timer
                    fragmentTimerBinding.missionTimer.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.breakTimer.setVisibility(View.GONE);

                    // vibrate for remind
                    if (enabledVibrate) {
                        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                    }

                    if (enabledNotification) {
                        notificationBuilder.setContentTitle("執行蕃茄任務！");
                        notificationBuilder.setContentText("");
                        notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                    }
                } else {
                    LogUtil.logD(TAG,"[break timer][onFinished] 1");
                    // finished timer fragment
                    timerViewModel.updateIsFinishedById(true);
                    MainActivity.commitWhenStarted(getLifecycle(),R.id.nav_home);

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
                    notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
                }
            }
        });
        return fragmentTimerBinding.getRoot();
    }

    // TODO：1209 應該監聽返回事件 完成數-1


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
