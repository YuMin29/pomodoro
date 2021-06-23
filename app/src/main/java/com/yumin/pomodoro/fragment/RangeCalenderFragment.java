package com.yumin.pomodoro.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentRangeCalenderBinding;
import com.yumin.pomodoro.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RangeCalenderFragment extends DataBindingFragment implements CalendarView.OnCalendarRangeSelectListener,
        CalendarView.OnMonthChangeListener, CalendarView.OnCalendarInterceptListener, View.OnClickListener {
    private static final String TAG = RangeCalenderFragment.class.getSimpleName();
    private FragmentRangeCalenderBinding mFragmentRangeCalenderBinding;
    private int mCalendarHeight;
    private static final String[] WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private SharedViewModel mSharedViewModel;
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    private long mMissionOperateDay = -1L;
    private long mLatestRepeatStart = -1L;
    private long mLatestRepeatEnd = -1L;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    protected void initViewModel() {
        mSharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_range_calender, -1, null)
                .addBindingParam(BR.rangeCalenderClickProxy, new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFragmentRangeCalenderBinding = (FragmentRangeCalenderBinding) getBinding();
        initView();
    }

    private void initView() {
        mFragmentRangeCalenderBinding.calendarView.setOnCalendarRangeSelectListener(this);
        mFragmentRangeCalenderBinding.calendarView.setOnMonthChangeListener(this);
        mFragmentRangeCalenderBinding.calendarView.setOnCalendarInterceptListener(this);
        mFragmentRangeCalenderBinding.tvTitle.setOnClickListener(this);
        mCalendarHeight = dipToPx(getContext(), 46);
        mFragmentRangeCalenderBinding.calendarView.post(new Runnable() {
            @Override
            public void run() {
                mFragmentRangeCalenderBinding.calendarView.scrollToCurrent();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            mLatestRepeatStart = bundle.getLong(MissionBaseFragment.REPEAT_START);
            mLatestRepeatEnd = bundle.getLong(MissionBaseFragment.REPEAT_END);
            mMissionOperateDay = bundle.getLong(MissionBaseFragment.MISSION_OPERATE_DAY);

            if (mLatestRepeatStart == -1L) {
                mFragmentRangeCalenderBinding.tvLeftWeek.setText(getString(R.string.range_start));
                mFragmentRangeCalenderBinding.tvLeftDate.setText("");
            } else {
                // convert to date
                mFragmentRangeCalenderBinding.tvLeftDate.setText(getMonth(mLatestRepeatStart) + "/" + getDay(mLatestRepeatStart));
                mStartYear = Integer.valueOf(getYear(mLatestRepeatStart));
                mStartMonth = Integer.valueOf(getMonth(mLatestRepeatStart));
                mStartDay = Integer.valueOf(getDay(mLatestRepeatStart));
            }

            if (mLatestRepeatEnd == -1L) {
                mFragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
                mFragmentRangeCalenderBinding.tvRightDate.setText("");
            } else {
                // convert to date
                mFragmentRangeCalenderBinding.tvRightDate.setText(getMonth(mLatestRepeatEnd) + "/" + getDay(mLatestRepeatEnd));
                mEndYear = Integer.valueOf(getYear(mLatestRepeatEnd));
                mEndMonth = Integer.valueOf(getMonth(mLatestRepeatEnd));
                mEndDay = Integer.valueOf(getDay(mLatestRepeatEnd));
            }
            mFragmentRangeCalenderBinding.calendarView.setSelectCalendarRange(mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay);
            mFragmentRangeCalenderBinding.calendarView.updateCurrentDate();

            if (mMissionOperateDay == -1L) {
                mFragmentRangeCalenderBinding.calendarView.setRange(mFragmentRangeCalenderBinding.calendarView.getCurYear(), mFragmentRangeCalenderBinding.calendarView.getCurMonth(),
                        mFragmentRangeCalenderBinding.calendarView.getCurDay(), 2030, 12, 31
                );
            } else {
                mFragmentRangeCalenderBinding.calendarView.setRange(Integer.valueOf(getYear(mMissionOperateDay)),
                        Integer.valueOf(getMonth(mMissionOperateDay)),
                        Integer.valueOf(getDay(mMissionOperateDay)), 2030, 12, 31);
            }
        }
    }

    public static String getYear(long milli) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static String getMonth(long milli) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("M");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static String getDay(long milli) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("d");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    @Override
    public void onCalendarSelectOutOfRange(Calendar calendar) {

    }

    @Override
    public void onSelectOutOfRange(Calendar calendar, boolean isOutOfMinRange) {

    }

    @Override
    public void onCalendarRangeSelect(Calendar calendar, boolean isEnd) {
        if (!isEnd) {
            mFragmentRangeCalenderBinding.tvLeftDate.setText(calendar.getMonth() + getString(R.string.range_month) +
                    calendar.getDay() + getString(R.string.range_day));
            mFragmentRangeCalenderBinding.tvLeftWeek.setText(WEEK[calendar.getWeek()]);
            mFragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
            mFragmentRangeCalenderBinding.tvRightDate.setText("");
        } else {
            mFragmentRangeCalenderBinding.tvRightDate.setText(calendar.getMonth() + getString(R.string.range_month) +
                    calendar.getDay() + getString(R.string.range_day));
            mFragmentRangeCalenderBinding.tvRightWeek.setText(WEEK[calendar.getWeek()]);
        }
    }

    @Override
    public void onMonthChange(int year, int month) {

    }

    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        if (mMissionOperateDay != -1L &&
                calendar.getTimeInMillis() < mMissionOperateDay) {
            return true;
        }
        return false;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {

    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {

    }

    private void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    public class ClickProxy {
        public void onIncrease() {
            mCalendarHeight += dipToPx(getContext(), 8);
            if (mCalendarHeight >= dipToPx(getContext(), 90)) {
                mCalendarHeight = dipToPx(getContext(), 90);
            }
            mFragmentRangeCalenderBinding.calendarView.setCalendarItemHeight(mCalendarHeight);
        }

        public void onReduce() {
            mCalendarHeight -= dipToPx(getContext(), 8);
            if (mCalendarHeight <= dipToPx(getContext(), 46)) {
                mCalendarHeight = dipToPx(getContext(), 46);
            }
            mFragmentRangeCalenderBinding.calendarView.setCalendarItemHeight(mCalendarHeight);
        }

        public void onClear() {
            mFragmentRangeCalenderBinding.calendarView.clearSelectRange();
            mFragmentRangeCalenderBinding.tvLeftWeek.setText(getString(R.string.range_start));
            mFragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
            mFragmentRangeCalenderBinding.tvLeftDate.setText("");
            mFragmentRangeCalenderBinding.tvRightDate.setText("");
            navigateUp();
        }

        public void onCommit() {
            List<Calendar> calendars = mFragmentRangeCalenderBinding.calendarView.getSelectCalendarRange();
            if (calendars == null || calendars.size() <= 1) {
                return;
            }
            for (Calendar c : calendars) {
                LogUtil.logE(TAG, c.toString() + "-" + c.getScheme() + "-" + c.getLunar());
            }
            long start = TimeToMillisecondUtil.getStartTime(calendars.get(0).getTimeInMillis());
            long end = TimeToMillisecondUtil.getEndTime(calendars.get(calendars.size() - 1).getTimeInMillis());

            mSharedViewModel.setRepeatStart(start);
            mSharedViewModel.setRepeatEnd(end);
            navigateUp();
        }
    }
}
