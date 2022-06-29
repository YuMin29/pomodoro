package com.yumin.pomodoro.customize;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.yumin.pomodoro.R;

import java.util.ArrayList;
import java.util.List;

public class CircleWaveView extends View {
    private int mCenterColor;
    private int mCenterRadius;
    private int mMaxRadius;
    private long mWaveIntervalTime;
    private long mWaveDuration;
    private boolean mRunning = false;
    private List<Wave> mWaveList = new ArrayList<Wave>();
    private int mWaveWidth;
    private Paint mPaint = new Paint();

    public CircleWaveView(Context context) {
        this(context, null);
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView,
                defStyleAttr, 0);
        mCenterColor = typedArray.getInt(R.styleable.WaveView_center_color,
                ContextCompat.getColor(context, R.color.colorPrimaryText));

        mCenterRadius = (int) typedArray.getDimension(R.styleable.WaveView_center_radius, 34f);
        mMaxRadius = (int) typedArray.getDimension(R.styleable.WaveView_max_radius, 44f);
        mWaveWidth = (int) typedArray.getDimension(R.styleable.WaveView_wave_width, 1.0f);
        mWaveIntervalTime = typedArray.getInt(R.styleable.WaveView_wave_interval_time, 2000);
        mWaveDuration = typedArray.getInt(R.styleable.WaveView_wave_duration, 5000);
        mPaint.setColor(mCenterColor);
        typedArray.recycle();
    }

    public void setWaveStart(Boolean waveStart) {
        if (waveStart) {
            if (!mRunning) {
                mRunning = true;
                mWaveList.add(new Wave());
            }
        } else {
            mRunning = false;

            for (Wave wave : mWaveList) {
                wave.cancelAnimation();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int radius = (int) (Math.min(w, h) / 2.0f);
        if (radius < mMaxRadius) {
            mMaxRadius = radius;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Wave wave : mWaveList) {
            mPaint.setAlpha(wave.getAlpha());
            mPaint.setStrokeWidth((float) mWaveWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2),
                    wave.getCurrentRadius(), mPaint);
        }

        if (mWaveList.size() > 0) {
            mPaint.setAlpha(255);
            mPaint.setStrokeWidth(10);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2),
                    (float) mCenterRadius, mPaint);
        }
    }

    private int dip2px(Context context, Float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class Wave {
        private boolean hasCreateNewWave = false;
        private float percent = 0f;
        private ObjectAnimator createWaveAnimation;

        public Wave() {
            createWaveAnimation = ObjectAnimator.ofFloat(this, "percent", 0f, 1.0f);
            createWaveAnimation.setDuration(mWaveDuration);
            createWaveAnimation.setInterpolator(new LinearInterpolator());
            createWaveAnimation.start();
            createWaveAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setPercent((float) animation.getAnimatedValue());
                }
            });
            createWaveAnimation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mRunning)
                        mWaveList.remove(this);
                }
            });
        }

        public void setPercent(float value) {
            percent = value;
            if (mRunning && value >= (float) mWaveIntervalTime / (float) mWaveDuration && !hasCreateNewWave) {
                mWaveList.add(new Wave());
                hasCreateNewWave = true;
            }
            invalidate();
        }

        private void cancelAnimation() {
            createWaveAnimation.cancel();
        }

        private int getAlpha() {
            return (int) (255 * (1 - percent));
        }

        private float getCurrentRadius() {
            return mCenterRadius + percent * (mMaxRadius - mCenterRadius);
        }
    }
}
