package com.yumin.pomodoro.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.yumin.pomodoro.R;

public class EventCountdownTimer {
    private final String TAG = getClass().getSimpleName();
    private static Context context;
    private BreakTimerListener breakTimerListener;
    private MissionTimerListener missionTimerListener;
    private long missionLeftTimeMilli;
    private int mIndexOfRingtone = 0;
    private int mIndexOfMissionBackground = 0;
    private long breakLeftTimeMilli;
    private CountDownTimer missionCountDownTimer;
    private CountDownTimer breakCountDownTimer;
    private MediaPlayer mediaPlayer;
    private int[] missionBackgroundIdArray = {
            R.raw.sound_effect_clock,
            R.raw.sound_effect_clock2,
            R.raw.sound_effect_sea,
            R.raw.sound_effect_sea2,
            R.raw.sound_effect_water,
            R.raw.sound_effect_water2,
            R.raw.dolphin_esque,
            R.raw.frolic,
            R.raw.in_3,
            R.raw.interplanetary_alignment,
            R.raw.meeting_again,
            R.raw.nebular_focus
    };

    private int[] ringtoneIdArray = {
            R.raw.ringtone_bell_ding,
            R.raw.ringtone_clay_chime_thunk,
            R.raw.ringtone_high_pitch_short_bell,
            R.raw.ringtone_timer_bell,
            R.raw.ringtone_toy_train_whistle,
            R.raw.ringtone_woodpecker_peck
    };

    public EventCountdownTimer(Context context, MissionTimerListener missionTimerListener, BreakTimerListener breakTimerListener) {
        this.context = context;
        this.missionTimerListener = missionTimerListener;
        this.breakTimerListener = breakTimerListener;
    }

    public void startBreakCountdown(long breakTime){
        LogUtil.logE(TAG,"[startBreakCountdown]");

        breakCountDownTimer = new CountDownTimer(breakTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.logE(TAG,"[startCountdown]");
                breakLeftTimeMilli = millisUntilFinished;
                breakTimerListener.onBreakTimerTickResponse(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                breakTimerListener.onBreakTimerFinishedResponse();
            }

        }.start();
    }

    public void startMissionCountdown(long timeMilli,int indexOfBackgroundMusic,int indexOfRingtone){
        LogUtil.logE(TAG,"[startMissionCountdown]");

        if (missionTimerListener.enabledSound() && indexOfBackgroundMusic != 0) {
            mediaPlayer = MediaPlayer.create(context, missionBackgroundIdArray[indexOfBackgroundMusic - 1]);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            this.mIndexOfMissionBackground = indexOfBackgroundMusic;
        }

        missionCountDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.logE(TAG,"[startCountdown] millisUntilFinished = "+millisUntilFinished);
                missionLeftTimeMilli = millisUntilFinished;
                missionTimerListener.onMissionTimerTickResponse(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                missionTimerListener.onMissionTimerFinishedResponse();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (missionTimerListener.enabledSound() && indexOfRingtone != 0) {
                    playFinishedRingtone(indexOfRingtone);
                }
            }

        }.start();
    }

    private void playFinishedRingtone(int indexOfRingtone){
        mediaPlayer = MediaPlayer.create(context, ringtoneIdArray[indexOfRingtone - 1]);
        mediaPlayer.start();
        mIndexOfRingtone = indexOfRingtone;
    }

    public void pauseMissionCountdown(){
        missionCountDownTimer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void continueMissionCount(int indexOfBackgroundMusic){
        startMissionCountdown(missionLeftTimeMilli,mIndexOfMissionBackground,mIndexOfRingtone);
    }

    public void pauseBreakCountdown() {
        breakCountDownTimer.cancel();
    }

    public void continueBreakCount() {
        startBreakCountdown(breakLeftTimeMilli);
    }

    public interface TimerListener {
        public boolean enabledSound();
    }

    public interface MissionTimerListener extends TimerListener{
        public void onMissionTimerTickResponse(long response);
        public void onMissionTimerFinishedResponse();
    }

    public interface BreakTimerListener{
        public void onBreakTimerTickResponse(long response);
        public void onBreakTimerFinishedResponse();
    }
}


