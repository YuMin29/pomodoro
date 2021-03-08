package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.utils.CircleTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends DataBindingFragment {
    private static final String TAG = "[TimerFragment]";
    private FragmentTimerBinding fragmentTimerBinding;
    private TimerViewModel timerViewModel;
    private boolean enabledVibrate;
    private boolean enabledNotification;
    private boolean enableKeepScreenOn;
    private int missionCount;
    private CircleTimer missionTimer;
    private int mNumberOfCompletion;
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
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        LogUtil.logD(TAG,"[onDestroy]");
    }

    @Override
    protected void initViewModel() {
        timerViewModel = getFragmentScopeViewModel(TimerViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_timer, BR.viewmodel,timerViewModel);
    }

    private void navigateUp(){
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentTimerBinding = (FragmentTimerBinding) getBinding();
        LogUtil.logE(TAG,"[onViewCreated]");
        observeViewModel();

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
                                    if (enabledNotification)
                                        notificationHelper.cancelNotification();
                                }
//                                MainActivity.getNavController().navigateUp();
                                navigateUp();
                                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                                undoStatusBarColor();
                                // update finish status
                                if (missionCount != -1 && mNumberOfCompletion != -1) {
                                    if ((missionCount - mNumberOfCompletion) < 1) {
                                        timerViewModel.updateIsFinishedById(true, mNumberOfCompletion);
                                    }
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

                // create mission state
                // or save mission state
            }

            @Override
            public void onFinished() {
                LogUtil.logD(TAG,"[mission timer][onFinished]");
                if (mNumberOfCompletion != -1) {
                    // update finished goal ui
                    mNumberOfCompletion++;
                    LogUtil.logD(TAG, "[mission timer][onFinish] numberOfCompletion = " + mNumberOfCompletion);
                    timerViewModel.updateNumberOfCompletionById(mNumberOfCompletion);
                }

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
                bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
//                bundle.putInt("itemId", MissionManager.getInstance().getOperateId());
                MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.action_timer_to_break_timer,bundle);
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

    private void undoStatusBarColor(){
        ((MainActivity)getContext()).getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    private void observeViewModel(){
        // TODO: 2/8/21 Use Mediator to observe mission from view model
        timerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logE(TAG,"[OBSERVE] getMission");
                if (mission != null) {
                    // format
                    long missionTime = Long.valueOf(mission.getTime() * 60 * 1000);
                    long missionBreakTime = Long.valueOf(mission.getShortBreakTime() * 60 * 1000);
                    // assign value
                    missionCount = mission.getGoal();
//                    mNumberOfCompletion = mission.getNumberOfCompletions();
                    enabledVibrate = mission.isEnableVibrate();
                    enabledNotification = mission.isEnableNotification();
                    enableKeepScreenOn = mission.isKeepScreenOn();
                    missionTitle = mission.getName();

                    if (enableKeepScreenOn) {
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

        timerViewModel.getNumberOfCompletion().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG,"[OBSERVE] getNumberOfCompletion = "+integer);
                mNumberOfCompletion = integer;
            }
        });

        timerViewModel.getMissionState().observe(getViewLifecycleOwner(), new Observer<MissionState>() {
            @Override
            public void onChanged(MissionState missionState) {
                LogUtil.logE(TAG,"[OBSERVE] getMissionState = "+missionState);
            }
        });

//        timerViewModel.getMissionState().observe(getViewLifecycleOwner(), new Observer<MissionState>() {
//            @Override
//            public void onChanged(MissionState missionState) {
//                if (null != missionState) {
//                    LogUtil.logE(TAG,"[OBSERVE] getMissionState , mNumberOfCompletion= "+missionState.numberOfCompletion);
//                    mNumberOfCompletion = missionState.numberOfCompletion;
//                } else {
//                    LogUtil.logE(TAG,"[OBSERVE] getMissionState = "+missionState);
//                }
//            }
//        });
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }
}
