package com.yumin.pomodoro.utils;

import android.content.Context;
import android.os.CountDownTimer;

public class EventCountdownTimer {
    private final String TAG = getClass().getSimpleName();
    private static Context context;
    private BreakTimerListener breakTimerListener;
    private MissionTimerListener missionTimerListener;
    private long missionLeftTimeMilli;
    private long breakLeftTimeMilli;
    private CountDownTimer missionCountDownTimer;
    private CountDownTimer breakCountDownTimer;

    public EventCountdownTimer(Context context, MissionTimerListener missionTimerListener, BreakTimerListener breakTimerListener) {
        this.context = context;
        this.missionTimerListener = missionTimerListener;
        this.breakTimerListener = breakTimerListener;
    }

//    public void setMissionTimeMilli(long timeMilli){
//        this.missionTimeMilli = timeMilli;
//    }
//
//    public void setBreakTimeMilli(long timeMilli){
//        this.breakTimeMilli = timeMilli;
//    }

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

    public void startMissionCountdown(long timeMilli){
        LogUtil.logE(TAG,"[startMissionCountdown]");
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
            }

        }.start();
    }

    public void pauseMissionCountdown(){
        missionCountDownTimer.cancel();
    }

    public void continueMissionCount(){
        startMissionCountdown(missionLeftTimeMilli);
    }

    public void pauseBreakCountdown() {
        breakCountDownTimer.cancel();
    }

    public void continueBreakCount() {
        startBreakCountdown(breakLeftTimeMilli);
    }

    public interface MissionTimerListener {
        public void onMissionTimerTickResponse(long response);
        public void onMissionTimerFinishedResponse();
    }

    public interface BreakTimerListener {
        public void onBreakTimerTickResponse(long response);
        public void onBreakTimerFinishedResponse();
    }
}


