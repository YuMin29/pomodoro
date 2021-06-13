package com.yumin.pomodoro.customize;

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
import com.yumin.pomodoro.utils.LogUtil;

import java.util.concurrent.TimeUnit;

public class CircleTimer extends RelativeLayout implements View.OnClickListener{
    private static final String TAG = CircleTimer.class.getSimpleName();
    private CircleTimerBinding mCircleTimerBinding;
    private TimerStatus mTimerStatus = TimerStatus.STOPPED;
    private Context mContext;
    private ProgressBar mProgressBarCircle;
    private TextView mTextViewTime;
    private ImageView mImageViewReset;
    private ImageView mImageViewStartStop;
    private CountDownTimer mCountDownTimer;
    private long mTimeCountInMilliSeconds = 1 * 60000;
    private long mMissionTime;
    private long mMissionTimeLeft;
    private CountDownTimerListener mCountDownTimerListener;
    private Type mType;
    MediaPlayer mMediaPlayer = null;
    private boolean mEnabledSound;

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
        mContext = context;
        initViews();
        initListeners();
    }

    public void setCountDownTimerListener(CountDownTimerListener listener){
        mCountDownTimerListener = listener;
    }

    public interface CountDownTimerListener {
        void onStarted();
        void onFinished();
        void onTick(long millisecond);
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

    private void initViews(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCircleTimerBinding = DataBindingUtil.inflate(inflater,R.layout.circle_timer,this,true);
        mProgressBarCircle = mCircleTimerBinding.progressBarCircle;
        mTextViewTime = mCircleTimerBinding.textViewTime;
        mImageViewReset = mCircleTimerBinding.imageViewReset;
        mImageViewStartStop = mCircleTimerBinding.imageViewStartPause;
    }

    private void initListeners() {
        mCircleTimerBinding.imageViewReset.setOnClickListener(this);
        mCircleTimerBinding.imageViewStartPause.setOnClickListener(this);
    }

    public void onClickReset() {
        initTimerValues();
        mTextViewTime.setText(msTimeFormatter(mMissionTime));
        setProgressBarValues(mMissionTime);
        mImageViewReset.setVisibility(GONE);
        mTimerStatus = TimerStatus.STOPPED;
    }

    public void onClickStartStop() {
        if (mTimerStatus == TimerStatus.STOPPED) {
            startTimer();
        } else if (mTimerStatus == TimerStatus.STARTED) {
            pauseTimer();
        } else if (mTimerStatus == TimerStatus.PAUSE){
            continueTimer();
        }
    }

    public TimerStatus getTimerStatus(){
        return mTimerStatus;
    }

    private void continueTimer() {
        mImageViewStartStop.setImageResource(R.drawable.ic_baseline_pause_24);
        mTimerStatus = TimerStatus.STARTED;
        mImageViewReset.setVisibility(View.GONE);
        continueCountDownTimer();
        setProgressBarValues(mMissionTimeLeft);
    }

    public void pauseTimer() {
        mImageViewStartStop.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mImageViewReset.setVisibility(View.VISIBLE);
        mTimerStatus = TimerStatus.PAUSE;
        pauseCountDownTimer();
    }

    public void startTimer() {
        initTimerValues();
        setProgressBarValues(mMissionTime);
        mImageViewReset.setVisibility(View.GONE);
        mImageViewStartStop.setImageResource(R.drawable.ic_baseline_pause_24);
        mTimerStatus = TimerStatus.STARTED;
        startCountDownTimer(mMissionTime);
    }

    private void pauseCountDownTimer(){
        mCountDownTimer.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void continueCountDownTimer(){
        startCountDownTimer(mMissionTimeLeft);
    }

    private void initTimerValues() {
        LogUtil.logE(TAG,"[initTimerValues]");
        mProgressBarCircle.setMax((int) mTimeCountInMilliSeconds / 1000);
        mMissionTime = mTimeCountInMilliSeconds;
    }

    private void startCountDownTimer(long timeMilli) {
        // init media player
        if (mType == Type.MISSION && mEnabledSound) {
            mMediaPlayer = MediaPlayer.create(mContext,R.raw.sound_effect_clock);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }

        if (mCountDownTimerListener != null)
            mCountDownTimerListener.onStarted();

        // start count down
        mCountDownTimer = new CountDownTimer(timeMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMissionTimeLeft = millisUntilFinished;
                mTextViewTime.setText(msTimeFormatter(millisUntilFinished));
                mProgressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                if (mCountDownTimerListener != null)
                    mCountDownTimerListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mTextViewTime.setText(msTimeFormatter(mMissionTime));
                setProgressBarValues(mMissionTime);
                mImageViewReset.setVisibility(View.GONE);
                mImageViewStartStop.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                mTimerStatus = TimerStatus.STOPPED;

                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }

                if (mCountDownTimerListener != null) {
                    mCountDownTimerListener.onFinished();
                    undoStatusBarColor();
                }
            }
        }.start();
    }

    private void setProgressBarValues(long time) {
        LogUtil.logE(TAG,"[setProgressBarValues] time = "+time);
        mProgressBarCircle.setProgress((int) time / 1000);
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public void setTimeCountInMilliSeconds(LiveData<String> time){
        mCircleTimerBinding.textViewTime.setText(time.getValue());
    }

    public void setTimerBackgroundColor(int color) {
        LogUtil.logD(TAG,"[setTimerBackgroundColor] color = "+color);
        mCircleTimerBinding.timerRelativelayout.setBackgroundColor(color);
        setStatusBarColor(color);
    }

    public void setStatusBarColor(int color){
        ((MainActivity)mContext).getWindow().setStatusBarColor(color);
    }

    private void undoStatusBarColor(){
        ((MainActivity)mContext).getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.colorPrimary));
    }

    public void setTimerType(String type){
        LogUtil.logD(TAG,"[setTimerType] = "+type);
        if (!TextUtils.isEmpty(type))
            mType = Type.getEnum(type);
    }

    public void setMissionTime(int time){
        LogUtil.logE(TAG,"[setMissionTime] time = "+time);
        mTimeCountInMilliSeconds = Long.valueOf(time * 1 * 1000);
    }

    public void setMissionName(String name){
        if (name != null) {
            mCircleTimerBinding.timerName.setVisibility(VISIBLE);
            mCircleTimerBinding.timerName.setText(name);
        }
    }

    public void setMissionGoal(int goal){
        if (goal != -1)
            mCircleTimerBinding.timerGoal.setText("/"+getResources().getString(R.string.mission_goal)+String.valueOf(goal));
    }

    public void setMissionFinished(int num){
        LogUtil.logE(TAG,"[setMissionFinished] num = "+num);
        if (num != -1)
            mCircleTimerBinding.timerFinish.setText(getResources().getString(R.string.mission_finish_goal)+String.valueOf(num));
    }

    public void setMissionEnabledSound(boolean enabled){
        mEnabledSound = enabled;
    }

    public void setMissionTimeLeft(long millisecond){
        mMissionTimeLeft = millisecond;
    }

    public void setBackgroundMusic(int index){
        LogUtil.logE(TAG,"[setBackgroundMusic] index = "+index);
    }
}
