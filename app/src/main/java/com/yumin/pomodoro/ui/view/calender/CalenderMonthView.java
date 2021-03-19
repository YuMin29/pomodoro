package com.yumin.pomodoro.ui.view.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

import java.util.List;

public class CalenderMonthView extends MonthView {
    private Paint mSchemeBasicPaint = new Paint();
    private int mPadding;
    private int mH, mW;

    public CalenderMonthView(Context context) {
        super(context);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xff333333);
        mSchemeBasicPaint.setFakeBoldText(true);
        mPadding = dipToPx(getContext(), 4);
        mH = dipToPx(getContext(), 2);
        mW = dipToPx(getContext(), 8);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + mPadding, y + mPadding, x + mItemWidth - mPadding, y + mItemHeight - mPadding, mSelectedPaint);
        return true;
    }

    /**
     * onDrawSelected
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        mSchemeBasicPaint.setColor(calendar.getSchemeColor());
        List<Calendar.Scheme> schemes = calendar.getSchemes();
        if (schemes == null || schemes.size() == 0) {
            return;
        }
        int space = dipToPx(getContext(), 2);
        int indexY = y + mItemHeight - 2 * space;
        int sw = dipToPx(getContext(), mItemWidth / 10);
        int sh = dipToPx(getContext(), 4);
        for (Calendar.Scheme scheme : schemes) {

            mSchemePaint.setColor(scheme.getShcemeColor());

            canvas.drawRect(x + mItemWidth - sw -  2 * space,

                    indexY - sh, x + mItemWidth - 2 * space, indexY, mSchemePaint);
            indexY = indexY - space -sh;
        }
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;
        if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            mCurMonthLunarTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mCurMonthLunarTextPaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
