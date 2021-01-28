package com.yumin.pomodoro.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.CircleTimerBinding;

import java.util.concurrent.TimeUnit;

public class CircleTimer extends RelativeLayout implements View.OnClickListener{
    private static final String TAG = "[CircleTimer]";
    private CircleTimerBinding circleTimerBinding;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private Context context;
    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 1 * 60000;
    private long missionTime;
    private long missionTimeLeft;
    private CountDownTimerListener countDownTimerListener;
    private Type type;
    MediaPlayer mediaPlayer = null;
    private boolean enabledSound;

    private enum Type{
        MISSION,
        BREAK;

        public static Type getEnum(String val){
            if (val == null || val.length() < 1) {
                return null;
            }

            for (Type type : values()) {
                if (type.name().equals(val)) {
                    return type;
                }
            }
            return null;
        }
    }


    public CircleTimer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews(context);
        initListeners();
    }

    public void setCountDownTimerListener(CountDownTimerListener listener){
        this.countDownTimerListener = listener;
    }

    public interface CountDownTimerListener {
        public void onStarted();
        public void onFinished();
        public void onTick(long millisecond);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                onClickReset();
                break;
            case R.id.imageViewStartPause:
                onClickStartStop();
                break;
        }
    }

    public enum TimerStatus {
        STARTED,
        PAUSE,
        STOPPED
    }

    private void initViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        circleTimerBinding = DataBindingUtil.inflate(inflater,R.layout.circle_timer,this,true);
        progressBarCircle = circleTimerBinding.progressBarCircle;
        textViewTime = circleTimerBinding.textViewTime;
        imageViewReset = circleTimerBinding.imageViewReset;
        imageViewStartStop = circleTimerBinding.imageViewStartPause;
    }

    private void initListeners() {
        circleTimerBinding.imageViewReset.setOnClickListener(this);
        circleTimerBinding.imageViewStartPause.setOnClickListener(this);
    }

    public void onClickReset() {
        initTimerValues();
        textViewTime.setText(msTimeFormatter(missionTime));
        setProgressBarValues(missionTime);
        imageViewReset.setVisibility(GONE);
        timerStatus = TimerStatus.STOPPED;
    }

    public void onClickStartStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            startTimer();
        } else if (timerStatus == TimerStatus.STARTED) {
            pauseTimer();
        } else if (timerStatus == TimerStatus.PAUSE){
            continueTimer();
        }
    }

    public TimerStatus getTimerStatus(){
        return timerStatus;
    }

    private void continueTimer() {
        imageViewStartStop.setImageResource(R.drawable.ic_baseline_pause_24);
        timerStatus = TimerStatus.STARTED;
        imageViewReset.setVisibility(View.GONE);
        continueCountDownTimer();
        setProgressBarValues(missionTimeLeft);
    }

    public void pauseTimer() {
        imageViewStartStop.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        imageViewReset.setVisibility(View.VISIBLE);
        timerStatus = TimerStatus.PAUSE;
        pauseCountDownTimer();
    }

    private void startTimer() {
        // call to initialize the timer values
        initTimerValues();
        // call to initialize the progress bar values
        setProgressBarValues(missionTime);
        // hide the reset icon
        imageViewReset.setVisibility(View.GONE);
        // changing play icon to stop icon
        imageViewStartStop.setImageResource(R.drawable.ic_baseline_pause_24);
        // changing the timer status to started
        timerStatus = TimerStatus.STARTED;
        // call to start the count down timer
        startCountDownTimer(missionTime);
    }

    private void pauseCountDownTimer(){
        countDownTimer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void continueCountDownTimer(){
        startCountDownTimer(missionTimeLeft);
    }

    private void initTimerValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        missionTime = timeCountInMilliSeconds;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer(long timeMilli) {
        // init media player
        if (this.type == Type.MISSION && enabledSound) {
            mediaPlayer = MediaPlayer.create(context,R.raw.sound_effect_clock);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        if (countDownTimerListener != null)
            countDownTimerListener.onStarted();

        // start count down
        countDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                missionTimeLeft = millisUntilFinished;
                textViewTime.setText(msTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                if (countDownTimerListener != null)
                    countDownTimerListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                textViewTime.setText(msTimeFormatter(missionTime));
                // call to initialize the progress bar values
                setProgressBarValues(missionTime);
                // hiding the reset icon
                imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (countDownTimerListener != null) {
                    countDownTimerListener.onFinished();
                    undoStatusBarColor();
                }
            }

        }.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues(long time) {
        progressBarCircle.setProgress((int) time / 1000);
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public void setTimeCountInMilliSeconds(LiveData<String> time){
        circleTimerBinding.textViewTime.setText(time.getValue());
    }

    public void setTimerBackgroundColor(int color) {
        LogUtil.logD(TAG,"[setTimerBackgroundColor] color = "+color);
        circleTimerBinding.timerRelativelayout.setBackgroundColor(color);
        setStatusBarColor(color);
    }

    public void setStatusBarColor(int color){
        ((MainActivity)context).getWindow().setStatusBarColor(color);
    }

    private void undoStatusBarColor(){
        ((MainActivity)context).getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimary));
    }

    public void setTimerType(String type){
        LogUtil.logD(TAG,"type = "+type);
        if (!TextUtils.isEmpty(type))
            this.type = Type.getEnum(type);
    }

    public void setMissionTime(int time){
        this.timeCountInMilliSeconds = Long.valueOf(time * 1 * 1000);
    }

    public void setMissionName(String name){
        if (name != null) {
            circleTimerBinding.timerName.setVisibility(VISIBLE);
            circleTimerBinding.timerName.setText(name);
        }
    }

    public void setMissionGoal(int goal){
        if (goal != -1)
            circleTimerBinding.timerGoal.setText("/"+getResources().getString(R.string.mission_goal)+String.valueOf(goal));
    }

    public void setMissionFinished(int num){
        if (num != -1)
            circleTimerBinding.timerFinish.setText(getResources().getString(R.string.mission_finish_goal)+String.valueOf(num));
    }

    public void setMissionEnabledSound(boolean enabled){
        this.enabledSound = enabled;
    }

    public void setMissionTimeLeft(long millisecond){
        this.missionTimeLeft = millisecond;
    }
}
