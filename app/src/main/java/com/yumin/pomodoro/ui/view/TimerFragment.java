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

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.base.TimerViewModelFactory;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.CircleTimer;
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

        // TODO：1209 handle back key in here
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                if (fragmentTimerBinding.missionTimer.getTimerStatus() == CircleTimer.TimerStatus.STARTED)
                    fragmentTimerBinding.missionTimer.pauseTimer();

                // showing a dialog to check whether to exit this page or not
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("結束任務")
                        .setMessage("確認結束任務？")
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // exit & cancel notification
                                if (fragmentTimerBinding.missionTimer.getTimerStatus() != CircleTimer.TimerStatus.STOPPED) {
                                    fragmentTimerBinding.missionTimer.onClickReset();
                                    notificationHelper.cancelNotification();
                                }
                                MainActivity.getNavController().navigateUp();
                                // update finish status
                                if ((missionCount - numberOfCompletion) < 1) {
                                    timerViewModel.updateIsFinishedById(true);
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // resume
                                fragmentTimerBinding.missionTimer.onClickStartStop();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        observeViewModel();
        fragmentTimerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer,container,false);
        fragmentTimerBinding.setLifecycleOwner(this);
        fragmentTimerBinding.setViewmodel(timerViewModel);
        missionTimer = fragmentTimerBinding.missionTimer;
        missionTimer.setCountDownTimerListener(new CircleTimer.CountDownTimerListener() {
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
                    notificationHelper.notify(notificationBuilder);
                }
            }

            @Override
            public void onFinished() {
                LogUtil.logD(TAG,"[mission timer][onFinished]");
                // update finished goal ui
                numberOfCompletion++;
                LogUtil.logD(TAG,"[mission timer][onFinish] numberOfCompletion = "+numberOfCompletion);
                timerViewModel.updateNumberOfCompletionById(numberOfCompletion);

                // vibrate for remind
                if (enabledVibrate) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                }

                // update notification
                if (enabledNotification) {
                    notificationBuilder.setContentTitle("休息一下吧！");
                    notificationBuilder.setContentText("");
                    notificationHelper.notify(notificationBuilder);
                }

                // switch to break timer
                Bundle bundle = new Bundle();
                bundle.putInt("itemId",itemId);
                MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.fragment_break_timer,bundle);
            }

            @Override
            public void onTick(long millisecond) {
                if (enabledNotification) {
                    notificationBuilder.setContentText(msTimeFormatter(millisecond));
                    notificationHelper.notify(notificationBuilder);
                }
            }
        });
        return fragmentTimerBinding.getRoot();
    }

    private void initViewModel() {
        timerViewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
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
