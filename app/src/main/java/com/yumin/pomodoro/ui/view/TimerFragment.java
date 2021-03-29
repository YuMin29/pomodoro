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
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.activity.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentTimerBinding;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;
import com.yumin.pomodoro.ui.view.customize.CircleTimer;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.NotificationHelper;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;
import com.yumin.pomodoro.ui.base.MissionManager;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends DataBindingFragment {
    private static final String TAG = "[TimerFragment]";
    private final int NOTIFICATION_ID = 1000;
    private FragmentTimerBinding mFragmentTimerBinding;
    private TimerViewModel mTimerViewModel;
    private boolean mEnabledVibrate;
    private boolean mEnabledNotification;
    private boolean mEnableKeepScreenOn;
    private int mMissionCount;
    private CircleTimer mMissionTimer;
    private int mNumberOfCompletion;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationHelper mNotificationHelper;
    private String mMissionTitle;
    private int mBackgroundColor;
    private boolean mIsAutoStartMission = false;
    private Handler mHandler;
    private boolean mIsDisableBreak = false;
    private int mIndexOfBackgroundMusic = -1;

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
        mTimerViewModel = getFragmentScopeViewModel(TimerViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_timer, BR.timerViewModel, mTimerViewModel);
    }

    private void navigateUp(){
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragmentTimerBinding = (FragmentTimerBinding) getBinding();
        LogUtil.logE(TAG,"[onViewCreated]");

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    LogUtil.logE(TAG,"[handleMessage] CALL onClickStartStop()");
                    mFragmentTimerBinding.missionTimer.onClickStartStop();
                }
                return true;
            }
        });

        observeViewModel();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pause timer if started
                if (mFragmentTimerBinding.missionTimer.getTimerStatus() == CircleTimer.TimerStatus.STARTED)
                    mFragmentTimerBinding.missionTimer.pauseTimer();

                // showing a dialog to check whether to exit this page or not
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("結束任務")
                        .setMessage("確認結束任務？")
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // exit & cancel notification
                                if (mFragmentTimerBinding.missionTimer.getTimerStatus() != CircleTimer.TimerStatus.STOPPED) {
                                    mFragmentTimerBinding.missionTimer.onClickReset();
                                    if (mEnabledNotification)
                                        mNotificationHelper.cancelNotification();
                                }
                                navigateUp();
                                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                                undoStatusBarColor();
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
                                mFragmentTimerBinding.missionTimer.onClickStartStop();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        mMissionTimer = mFragmentTimerBinding.missionTimer;
        mMissionTimer.setCountDownTimerListener(new CircleTimer.CountDownTimerListener() {
            @Override
            public void onStarted() {
                // show notification in here
                if (mEnabledNotification) {
                    // Create an explicit intent for an Activity in your app
                    Intent intent = getContext().getPackageManager()
                            .getLaunchIntentForPackage(getContext().getPackageName())
                            .setPackage(null)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                    mNotificationHelper = new NotificationHelper(getContext());
                    mNotificationBuilder = mNotificationHelper.getNotification("蕃茄任務:" + mMissionTitle,"執行中",pendingIntent, mBackgroundColor);
                    mNotificationHelper.notify(mNotificationBuilder);
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

                    mTimerViewModel.updateMissionNumberOfCompletion(mNumberOfCompletion);
                    if (mNumberOfCompletion == mMissionCount) {
                        LogUtil.logD(TAG,"[mission timer][onFinished] mNumberOfCompletion == missionCount");
                        mTimerViewModel.updateMissionFinishedState(true,mNumberOfCompletion);

                        if (mIsDisableBreak) {
                            navigateUp();
                            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                            undoStatusBarColor();

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
                if (mEnabledNotification) {
                    mNotificationHelper.changeRemoteContent("休息一下吧！");
                    mNotificationHelper.notify(mNotificationBuilder);
                }

                if (mIsDisableBreak) {
                    // repeat mission timer again
                    Bundle bundle = new Bundle();
                    bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.load_timer_again,bundle);
                } else {
                    // switch to break timer
                    Bundle bundle = new Bundle();
                    bundle.putString("itemId", MissionManager.getInstance().getStrOperateId());
                    MainActivity.commitWhenLifecycleStarted(getLifecycle(),R.id.action_timer_to_break_timer,bundle);
                }
            }

            @Override
            public void onTick(long millisecond) {
                if (mEnabledNotification) {
//                    notificationBuilder.setContentText(msTimeFormatter(millisecond));
                    mNotificationHelper.changeRemoteContent(msTimeFormatter(millisecond));
                    mNotificationHelper.notify(mNotificationBuilder);
                }
            }
        });
    }

    private void undoStatusBarColor(){
        ((MainActivity)getContext()).getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    private void observeViewModel(){
        // TODO: 2/8/21 Use Mediator to observe mission from view model
        mTimerViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logE(TAG,"[OBSERVE] getMission");
                if (mission != null) {
                    // format
                    long missionTime = Long.valueOf(mission.getTime() * 60 * 1000);
                    long missionBreakTime = Long.valueOf(mission.getShortBreakTime() * 60 * 1000);
                    // assign value
                    mMissionCount = mission.getGoal();
//                    mNumberOfCompletion = mission.getNumberOfCompletions();
                    mEnabledVibrate = mission.isEnableVibrate();
                    mEnabledNotification = mission.isEnableNotification();
                    mEnableKeepScreenOn = mission.isKeepScreenOn();
                    mMissionTitle = mission.getName();
                    mBackgroundColor = mission.getColor();

                    if (mEnableKeepScreenOn) {
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
                    mTimerViewModel.setMissionTime(msTimeFormatter(missionTime));
                    mTimerViewModel.setMissionBreakTime(msTimeFormatter(missionBreakTime));

                    // auto start in here ???
                    if (missionTime != 0){
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.logE(TAG,"isAutoStartMission = "+ mIsAutoStartMission);
                                if (mIsAutoStartMission && mIndexOfBackgroundMusic != -1) {
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
                LogUtil.logE(TAG,"[OBSERVE] getNumberOfCompletion = "+integer);
                mNumberOfCompletion = integer;
            }
        });

        mTimerViewModel.getMissionState().observe(getViewLifecycleOwner(), new Observer<MissionState>() {
            @Override
            public void onChanged(MissionState missionState) {
                LogUtil.logE(TAG,"[OBSERVE] getMissionState = "+missionState);
            }
        });

        mTimerViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG,"[OBSERVE][getAutoStartNextMission] aBoolean = "+aBoolean);
                mIsAutoStartMission = aBoolean;
            }
        });

        mTimerViewModel.getDisableBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG,"[OBSERVE][getDisableBreak] aBoolean = "+aBoolean);
                mIsDisableBreak = aBoolean;
            }
        });

        mTimerViewModel.getIndexOfMissionBackgroundRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtil.logE(TAG,"[OBSERVE][getIndexOfMissionBackgroundRingtone] INDEX = "+integer);
                mIndexOfBackgroundMusic = integer;
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
