package com.yumin.pomodoro.ui.timer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.MissionDBManager;
import com.yumin.pomodoro.data.repository.room.MissionDao;
import com.yumin.pomodoro.data.repository.room.MissionStateDao;
import com.yumin.pomodoro.utils.CountdownTimer;
import com.yumin.pomodoro.utils.PrefUtils;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CountDownTimerService extends Service implements CountdownTimer.BreakTimerListener, CountdownTimer.MissionTimerListener {
    private static final String TAG = CountDownTimerService.class.getSimpleName();
    public static final String ACTION_TIMER_INFO = "timer_info";
    public static final String ACTION_MISSION_FINISHED = "mission_finished";

    public static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default Channel";
    private static final String CHANNEL_DESCRIPTION = "this is default channel!";
    private static final int NOTIFICATION_ID = 1001;

    public static final String ACTION_START_MISSION = "start_mission";
    public static final String ACTION_PAUSE_MISSION = "pause_mission";
    public static final String ACTION_CONTINUE_MISSION = "continue_mission";
    public static final String ACTION_RESET_MISSION = "reset_mission";
    public static final String ACTION_START_BREAK = "start_break";
    public static final String ACTION_PAUSE_BREAK = "pause_break";
    public static final String ACTION_CONTINUE_BREAK = "continue_break";
    public static final String ACTION_RESET_BREAK = "reset_break";
    public static final String ACTION_STOP_SERVICE = "stop_service";


    public static final String EXTRA_MISSION_ID = "missionId";
    public static final String EXTRA_MISSION_TIME = "mission_time";
    public static final String EXTRA_BREAK_TIME = "break_time";

    public static final String EXTRA_TIME_STR = "time_str";
    public static final String EXTRA_TIME_LONG = "time_long";

    NotificationManager mNotificationManager = null;
    NotificationChannel mNotificationChannel = null;
    RemoteViews mNotificationRemoteViews;
    long mMissionTime;
    long mBreakTime;
    NotificationCompat.Builder mNotificationBuilder;
    CountdownTimer mCountdownTimer;
    UserMission mUserMission;

    boolean mAutoStartMission;
    boolean mAutoStartBreak;
    boolean mDisableBreak;
    int mBackgroundRingtoneIndex;
    int mFinishedRingtoneIndex;
    MissionStateDao mMissionStateDao;
    MissionState mMissionState;
    int mCompletionOfMission;
    boolean mIsQuickMission = false;
    String mMissionId;
    PowerManager.WakeLock mPartialWakeLock;
    PowerManager.WakeLock mKeepScreenOnWakeLock;
    private int mBreakBackgroundColor = Color.parseColor("#87CEEB");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    private void initMissionState(String missionId) {
        if (missionId.equals("quick_mission"))
            return;

        MissionDBManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMissionState = new MissionState(0, false,
                        TimeToMillisecondUtil.getTodayStartTime(), -1, missionId);
                mMissionStateDao.insert(mMissionState);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMissionId = intent.getStringExtra(EXTRA_MISSION_ID);
        mMissionTime = intent.getLongExtra(EXTRA_MISSION_TIME,0);
        mBreakTime = intent.getLongExtra(EXTRA_BREAK_TIME,0);

        MissionDBManager missionDBManager = MissionDBManager.getInstance(this);
        MissionDao missionDao = missionDBManager.getMissionDao();

        mMissionStateDao = missionDBManager.getMissionStateDao();
        if (!mMissionId.equals("quick_mission")) {
            mUserMission = missionDao.getMissionByIdForService(Integer.valueOf(mMissionId));
            mMissionState = mMissionStateDao.getMissionStateByTodayForService(Integer.valueOf(mMissionId),
                    TimeToMillisecondUtil.getTodayStartTime());
            if (mMissionState == null) {
                initMissionState(mMissionId);
            }
            mIsQuickMission = false;
        } else {
            mUserMission = new UserMission(25, 5, Color.parseColor("#e57373"));
            mIsQuickMission = true;
        }
        changeNotificationColor(mUserMission.getColor());

        Log.e(TAG, "[onStartCommand] EXTRA missionTime = " + mMissionTime + ", breakTime = " + mBreakTime);
        Log.e(TAG, "[onStartCommand] EXTRA missionId = " + mMissionId);

        mAutoStartMission = PrefUtils.getAutoMission(this);
        mAutoStartBreak = PrefUtils.getAutoBreak(this);
        mDisableBreak = PrefUtils.getDisableBreak(this);
        mBackgroundRingtoneIndex = PrefUtils.getIndexOfBackgroundRingtone(this);
        mFinishedRingtoneIndex = PrefUtils.getIndexOfFinishedRingtone(this);

        Log.e(TAG, "[onStartCommand] autoMission = " + mAutoStartMission + " ,autoBreak = " + mAutoStartBreak
                + " ,disableBreak = " + mDisableBreak + " ,backgroundRingtoneIndex = " + mBackgroundRingtoneIndex
                + " ,finishedRingtoneIndex = " + mFinishedRingtoneIndex);

        handleAction(intent.getAction());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        mPartialWakeLock.acquire();

        if (mUserMission.isKeepScreenOn()) {
            mKeepScreenOnWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getCanonicalName());
            mKeepScreenOnWakeLock.acquire();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        release();
    }

    private void handleAction(String action) {
        switch (action) {
            case ACTION_START_MISSION:
                mCompletionOfMission = mIsQuickMission ? -1 : mMissionState.getNumberOfCompletion();
                mCountdownTimer = new CountdownTimer(this, this, this);
                mCountdownTimer.startMissionCountdown(mMissionTime, mBackgroundRingtoneIndex, mFinishedRingtoneIndex);
                break;

            case ACTION_PAUSE_MISSION:
                mCountdownTimer.cancelMissionCountdown();
                break;

            case ACTION_CONTINUE_MISSION:
                mCountdownTimer.continueMissionCount();
                break;

            case ACTION_RESET_MISSION:
                changeNotificationContent(msTimeFormatter(mMissionTime));
                sendTimeInfoBroadCast(msTimeFormatter(mMissionTime), mMissionTime);
                break;

            case ACTION_START_BREAK:
                changeNotificationColor(mBreakBackgroundColor);
                mCountdownTimer.startBreakCountdown(mBreakTime);
                break;

            case ACTION_PAUSE_BREAK:
                changeNotificationColor(mBreakBackgroundColor);
                mCountdownTimer.cancelBreakCountdown();
                break;

            case ACTION_CONTINUE_BREAK:
                changeNotificationColor(mBreakBackgroundColor);
                mCountdownTimer.continueBreakCount();
                break;

            case ACTION_RESET_BREAK:
                changeNotificationColor(mBreakBackgroundColor);
                changeNotificationContent(msTimeFormatter(mBreakTime));
                sendTimeInfoBroadCast(msTimeFormatter(mBreakTime), mBreakTime);
                break;

            case ACTION_STOP_SERVICE:
                release();
                break;
        }
    }

    private void createNotification() {
        Intent intent = this.getPackageManager()
                .getLaunchIntentForPackage(this.getPackageName())
                .setPackage(null);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.setDescription(CHANNEL_DESCRIPTION);
            getNotificationManager().createNotificationChannel(mNotificationChannel);
        }
        mNotificationRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);
        mNotificationRemoteViews.setTextViewText(R.id.left_time_textview, msTimeFormatter(mMissionTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            mNotificationBuilder = new NotificationCompat.Builder(this);
            mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        mNotificationBuilder.setCustomContentView(mNotificationRemoteViews)
                .setSmallIcon(R.drawable.ic_tomato_24)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public void changeNotificationContent(String title) {
        mNotificationRemoteViews.setTextViewText(R.id.left_time_textview, title);
        getNotificationManager().notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    public void changeNotificationColor(int backgroundColor) {
        mNotificationRemoteViews.setInt(R.id.left_time_textview, "setBackgroundColor", backgroundColor);
        getNotificationManager().notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        // release wack lock
        if (mPartialWakeLock != null && mPartialWakeLock.isHeld())
            mPartialWakeLock.release();

        if (mKeepScreenOnWakeLock != null && mKeepScreenOnWakeLock.isHeld()) {
            mKeepScreenOnWakeLock.release();
        }

        PrefUtils.clearTimerServiceStatus(this);
        stopForeground(true);
        this.stopSelf();

        if (mCountdownTimer != null) {
            mCountdownTimer.cancelMissionCountdown();
            mCountdownTimer.cancelBreakCountdown();
        }
    }

    private void sendTimeInfoBroadCast(String timeStr, long timeLong) {
        Intent timerInfoIntent = new Intent(ACTION_TIMER_INFO);
        timerInfoIntent.putExtra(EXTRA_TIME_STR, timeStr);
        timerInfoIntent.putExtra(EXTRA_TIME_LONG, timeLong);
        LocalBroadcastManager.getInstance(CountDownTimerService.this).sendBroadcast(timerInfoIntent);
    }

    @Override
    public boolean enabledSound() {
        return mUserMission.isEnableSound();
    }

        @Override
    public void onMissionTimerTickResponse(long response) {
        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_START);
        changeNotificationContent(msTimeFormatter(response));
        sendTimeInfoBroadCast(msTimeFormatter(response), response);
    }

@Override
    public void onMissionTimerFinishedResponse() {
        // STATUS
        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_STOP);

        if (mUserMission.isEnableVibrate()) {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

        if (!mIsQuickMission) {
            mCompletionOfMission++;
            mMissionStateDao.updateNumberOfCompletionsById(Integer.valueOf(mMissionId), mCompletionOfMission, TimeToMillisecondUtil.getTodayInitTime());
            boolean isFinishMission = (mCompletionOfMission == mUserMission.getGoal()) ? true : false;
            // UPDATE MISSION STATE
            if (isFinishMission) {
                mMissionStateDao.updateIsFinishedById(Integer.valueOf(mMissionId), true ,TimeToMillisecondUtil.getTodayStartTime());
                mMissionStateDao.updateFinishedDayById(Integer.valueOf(mMissionId), new Date().getTime(),TimeToMillisecondUtil.getTodayStartTime());
            }

            if (!isFinishMission) {
                if (mDisableBreak) {
                    PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_INIT);
                    changeNotificationColor(mUserMission.getColor());
                    changeNotificationContent(getString(R.string.notification_mission_message));
                    sendTimeInfoBroadCast(msTimeFormatter(mMissionTime), mMissionTime);

                    if (mAutoStartMission) {
                        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_START);
                        mCountdownTimer = new CountdownTimer(this, this, this);
                        mCountdownTimer.startMissionCountdown(mMissionTime, mBackgroundRingtoneIndex, mFinishedRingtoneIndex);
                    }
                } else {
                    changeNotificationColor(mBreakBackgroundColor);
                    changeNotificationContent(getString(R.string.notification_break_message));
                    sendTimeInfoBroadCast(msTimeFormatter(mBreakTime), mBreakTime);

                    if (mAutoStartBreak) {
                        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.BREAK_START);
                        mCountdownTimer.startBreakCountdown(mBreakTime);
                    }
                }
            } else {
                changeNotificationColor(mBreakBackgroundColor);
                changeNotificationContent(getString(R.string.notification_break_message));
                sendTimeInfoBroadCast(msTimeFormatter(mBreakTime), mBreakTime);

                if (mDisableBreak) {
                    changeNotificationContent(getString(R.string.notification_finished_message));
                    PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_FINISHED);
                } else {
                    if (mAutoStartBreak) {
                        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.BREAK_START);
                        mCountdownTimer.startBreakCountdown(mBreakTime);
                    }
                }
            }
        } else {
            changeNotificationColor(mBreakBackgroundColor);
            changeNotificationContent(getString(R.string.notification_break_message));
            sendTimeInfoBroadCast(msTimeFormatter(mBreakTime), mBreakTime);

            if (mDisableBreak) {
                changeNotificationContent(getString(R.string.notification_finished_message));
                PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_FINISHED);
            } else {
                if (mAutoStartBreak) {
                    PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.BREAK_START);
                    mCountdownTimer.startBreakCountdown(mBreakTime);
                }
            }
        }
}

    @Override
    public void onBreakTimerTickResponse(long response) {
        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.BREAK_START);
        changeNotificationContent(msTimeFormatter(response));
        sendTimeInfoBroadCast(msTimeFormatter(response), response);
    }

    @Override
    public void onBreakTimerFinishedResponse() {
        PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.BREAK_STOP);

        if (mUserMission.isEnableVibrate()) {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

        boolean isFinishMission = (mCompletionOfMission == mUserMission.getGoal()) ? true : false;
        if (!mMissionId.equals("quick_mission")) {
            if (!isFinishMission) {
                PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_INIT);
                changeNotificationColor(mUserMission.getColor());
                changeNotificationContent(getString(R.string.notification_mission_message));
                sendTimeInfoBroadCast(msTimeFormatter(mMissionTime), mMissionTime);

                if (mAutoStartMission) {
                    PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_START);
                    mCountdownTimer = new CountdownTimer(this, this, this);
                    mCountdownTimer.startMissionCountdown(mMissionTime, mBackgroundRingtoneIndex, mFinishedRingtoneIndex);
                }
            } else {
                changeNotificationContent(getString(R.string.notification_finished_message));
                PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_FINISHED);
            }
        } else {
            changeNotificationContent(getString(R.string.notification_finished_message));
            PrefUtils.setTimerServiceStatus(this, TimerFragment.TimerStatus.MISSION_FINISHED);
        }
    }
}
