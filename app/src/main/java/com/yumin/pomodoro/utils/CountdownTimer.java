package com.yumin.pomodoro.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.yumin.pomodoro.R;

public class CountdownTimer {
    private final String TAG = CountdownTimer.class.getSimpleName();
    private static Context mContext;
    private BreakTimerListener mBreakTimerListener;
    private MissionTimerListener mMissionTimerListener;
    private long mMissionLeftTimeMilli;
    private int mIndexOfRingtone = 0;
    private int mIndexOfMissionBackground = 0;
    private long mBreakLeftTimeMilli;
    private CountDownTimer mMissionCountDownTimer;
    private CountDownTimer mBreakCountDownTimer;
    private MediaPlayer mMediaPlayer;
    private int[] mMissionBackgroundIdArray = {
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

    private int[] mRingtoneIdArray = {
            R.raw.ringtone_bell_ding,
            R.raw.ringtone_clay_chime_thunk,
            R.raw.ringtone_high_pitch_short_bell,
            R.raw.ringtone_timer_bell,
            R.raw.ringtone_toy_train_whistle,
            R.raw.ringtone_woodpecker_peck
    };

    public CountdownTimer(Context context, MissionTimerListener missionTimerListener, BreakTimerListener breakTimerListener) {
        mContext = context;
        mMissionTimerListener = missionTimerListener;
        mBreakTimerListener = breakTimerListener;
    }

    public void startBreakCountdown(long breakTime){
        LogUtil.logE(TAG,"[startBreakCountdown]");
        mBreakCountDownTimer = new CountDownTimer(breakTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.logE(TAG,"[startCountdown]");
                mBreakLeftTimeMilli = millisUntilFinished;
                mBreakTimerListener.onBreakTimerTickResponse(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                mBreakTimerListener.onBreakTimerFinishedResponse();
            }

        }.start();
    }

    public void startMissionCountdown(long timeMilli,int indexOfBackgroundMusic,int indexOfRingtone){
        LogUtil.logE(TAG,"[startMissionCountdown]");

        if (mMissionTimerListener.enabledSound() && indexOfBackgroundMusic != 0) {
            mMediaPlayer = MediaPlayer.create(mContext, mMissionBackgroundIdArray[indexOfBackgroundMusic - 1]);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
            mIndexOfMissionBackground = indexOfBackgroundMusic;
        }

        mMissionCountDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.logE(TAG,"[startCountdown] millisUntilFinished = "+millisUntilFinished);
                mMissionLeftTimeMilli = millisUntilFinished;
                mMissionTimerListener.onMissionTimerTickResponse(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mMissionTimerListener.onMissionTimerFinishedResponse();
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }

                if (mMissionTimerListener.enabledSound() && indexOfRingtone != 0) {
                    playFinishedRingtone(indexOfRingtone);
                }
            }

        }.start();
    }

    private void playFinishedRingtone(int indexOfRingtone){
        mMediaPlayer = MediaPlayer.create(mContext, mRingtoneIdArray[indexOfRingtone - 1]);
        mMediaPlayer.start();
        mIndexOfRingtone = indexOfRingtone;
    }

    public void cancelMissionCountdown(){
        if (mMissionCountDownTimer != null)
            mMissionCountDownTimer.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void continueMissionCount(){
        startMissionCountdown(mMissionLeftTimeMilli,mIndexOfMissionBackground,mIndexOfRingtone);
    }

    public void cancelBreakCountdown() {
        if (mBreakCountDownTimer != null)
            mBreakCountDownTimer.cancel();
    }

    public void continueBreakCount() {
        startBreakCountdown(mBreakLeftTimeMilli);
    }

    public interface TimerListener {
        boolean enabledSound();
    }

    public interface MissionTimerListener extends TimerListener{
        void onMissionTimerTickResponse(long response);
        void onMissionTimerFinishedResponse();
    }

    public interface BreakTimerListener{
        void onBreakTimerTickResponse(long response);
        void onBreakTimerFinishedResponse();
    }
}


