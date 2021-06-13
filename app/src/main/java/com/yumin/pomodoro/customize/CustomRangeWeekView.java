package com.yumin.pomodoro.customize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.RangeWeekView;
import com.yumin.pomodoro.utils.LogUtil;

public class CustomRangeWeekView extends RangeWeekView {
    private static final String TAG = CustomRangeWeekView.class.getSimpleName();
    private int mRadius;

    public CustomRangeWeekView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme,
                                     boolean isSelectedPre, boolean isSelectedNext) {
        LogUtil.logD(TAG, "[onDrawSelected]");
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;

        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(x, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            } else {
                canvas.drawRect(x, cy - mRadius, cx, cy + mRadius, mSelectedPaint);
                canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(cx, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            }
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        }


        if (onCalendarIntercept(calendar)) {
            // gray out
//            Paint grayOutPaint = new Paint();
//            grayOutPaint.setAntiAlias(true);
//            grayOutPaint.setStyle(Paint.Style.FILL);
//            grayOutPaint.setStrokeWidth(2);
//            grayOutPaint.setColor(mOtherMonthTextPaint);
            canvas.drawCircle(cx, cy, mRadius, mOtherMonthTextPaint);
        }
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, boolean isSelected) {
        LogUtil.logD(TAG, "[onDrawScheme]");
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        LogUtil.logD(TAG, "[onDrawText]");
        float baselineY = mTextBaseLine;
        int cx = x + mItemWidth / 2;
        boolean isInRange = isInRange(calendar);
        boolean isEnable = !onCalendarIntercept(calendar);
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
