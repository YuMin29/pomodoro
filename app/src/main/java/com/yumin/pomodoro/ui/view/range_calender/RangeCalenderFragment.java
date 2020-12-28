package com.yumin.pomodoro.ui.view.range_calender;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentRangeCalenderBinding;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RangeCalenderFragment extends DataBindingFragment implements CalendarView.OnCalendarRangeSelectListener,
        CalendarView.OnMonthChangeListener, CalendarView.OnCalendarInterceptListener, View.OnClickListener {
    private static final String TAG = "[RangeCalenderFragment]";
    private FragmentRangeCalenderBinding fragmentRangeCalenderBinding;
    private int mCalendarHeight;
    private static final String[] WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private RangeCalenderViewModel rangeCalenderViewModel;
    private SharedViewModel sharedViewModel;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;
    private long missionOperateDay = -1L;

    @Override
    public void onResume() {
        LogUtil.logD(TAG, "[onResume]");
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        LogUtil.logD(TAG, "[onStop]");
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    protected void initViewModel() {
        rangeCalenderViewModel = getFragmentScopeViewModel(RangeCalenderViewModel.class);
        sharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_range_calender,BR.viewModel,rangeCalenderViewModel)
                .addBindingParam(BR.clickProxy, new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentRangeCalenderBinding = (FragmentRangeCalenderBinding) getBinding();
        initView();
        initObserve();
    }

    private void initView(){
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        fragmentRangeCalenderBinding.calendarView.setOnCalendarRangeSelectListener(this);
        fragmentRangeCalenderBinding.calendarView.setOnMonthChangeListener(this);
        //设置日期拦截事件，当前有效
        fragmentRangeCalenderBinding.calendarView.setOnCalendarInterceptListener(this);
        fragmentRangeCalenderBinding.tvTitle.setOnClickListener(this);

        mCalendarHeight = dipToPx(getContext(), 46);

        if (missionOperateDay == -1L) {
            fragmentRangeCalenderBinding.calendarView.setRange(fragmentRangeCalenderBinding.calendarView.getCurYear(), fragmentRangeCalenderBinding.calendarView.getCurMonth(),
                    fragmentRangeCalenderBinding.calendarView.getCurDay(),2030,12,31
            );
        } else {
            // TODO: 2020/12/29 需要新增獲得即時的執行日期(temp operate day)
            fragmentRangeCalenderBinding.calendarView.setRange(Integer.valueOf(getYear(missionOperateDay)),
                    Integer.valueOf(getMonth(missionOperateDay)),
                    Integer.valueOf(getDay(missionOperateDay)),2030,12,31);
        }

        fragmentRangeCalenderBinding.calendarView.post(new Runnable() {
            @Override
            public void run() {
                fragmentRangeCalenderBinding.calendarView.scrollToCurrent();
            }
        });
    }

    private void initObserve() {
        rangeCalenderViewModel.getRepeatStart().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long start) {
                if (start != null) {
                    LogUtil.logD(TAG,"[initObserve] start = "+start);
                    if (start == -1L) {
                        fragmentRangeCalenderBinding.tvLeftWeek.setText(getString(R.string.range_start));
                        fragmentRangeCalenderBinding.tvLeftDate.setText("");
                    } else {
                        // convert to date
                        fragmentRangeCalenderBinding.tvLeftDate.setText(getMonth(start)+"/"+getDay(start));
                        startYear = Integer.valueOf(getYear(start));
                        startMonth = Integer.valueOf(getMonth(start));
                        startDay = Integer.valueOf(getDay(start));
                        fragmentRangeCalenderBinding.calendarView.setSelectCalendarRange(startYear,startMonth,startDay,endYear,endMonth,endDay);
                        fragmentRangeCalenderBinding.calendarView.updateCurrentDate();
                    }
                    sharedViewModel.setRepeatStart(start);
                }
            }
        });

        rangeCalenderViewModel.getRepeatEnd().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long end) {
                if (end != null) {
                    LogUtil.logD(TAG,"[initObserve] end = "+end);
                    if (end == -1L) {
                        fragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
                        fragmentRangeCalenderBinding.tvRightDate.setText("");
                    } else {
                        // convert to date
                        fragmentRangeCalenderBinding.tvRightDate.setText(getMonth(end)+"/"+getDay(end));
                        endYear = Integer.valueOf(getYear(end));
                        endMonth = Integer.valueOf(getMonth(end));
                        endDay = Integer.valueOf(getDay(end));
                        fragmentRangeCalenderBinding.calendarView.setSelectCalendarRange(startYear,startMonth,startDay,endYear,endMonth,endDay);
                        fragmentRangeCalenderBinding.calendarView.updateCurrentDate();
                    }
                    sharedViewModel.setRepeatEnd(end);
                }
            }
        });

        rangeCalenderViewModel.getMissionOperateDay().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long operateDay) {
                if (operateDay != null) {
                    if (operateDay != -1L) {
                        LogUtil.logD(TAG,"[initObserve] operateDay = "+operateDay);
                        missionOperateDay = operateDay;
                    }
                }
            }
        });
    }


    public static String getYear(long milli){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy");
        String ctime = formatter.format(new Date(milli));
        return ctime;
    }

    public static String getMonth(long milli){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("M");
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
            fragmentRangeCalenderBinding.tvLeftDate.setText(calendar.getMonth() + getString(R.string.range_month) +
                    calendar.getDay() + getString(R.string.range_day));
            fragmentRangeCalenderBinding.tvLeftWeek.setText(WEEK[calendar.getWeek()]);
            fragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
            fragmentRangeCalenderBinding.tvRightDate.setText("");
        } else {
            fragmentRangeCalenderBinding.tvRightDate.setText(calendar.getMonth() +  getString(R.string.range_month)  +
                    calendar.getDay() + getString(R.string.range_day));
            fragmentRangeCalenderBinding.tvRightWeek.setText(WEEK[calendar.getWeek()]);
        }
    }

    @Override
    public void onMonthChange(int year, int month) {

    }

    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        if (missionOperateDay != -1L &&
                calendar.getTimeInMillis() < missionOperateDay) {
            return true;
        }
        return false;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {

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

    @Override
    public void onClick(View v) {

    }

    public class ClickProxy {
        public void onIncrease() {
            mCalendarHeight += dipToPx(getContext(), 8);
            if (mCalendarHeight >= dipToPx(getContext(), 90)) {
                mCalendarHeight = dipToPx(getContext(), 90);
            }
            fragmentRangeCalenderBinding.calendarView.setCalendarItemHeight(mCalendarHeight);
        }

        public void onReduce() {
            mCalendarHeight -= dipToPx(getContext(), 8);
            if (mCalendarHeight <= dipToPx(getContext(), 46)) {
                mCalendarHeight = dipToPx(getContext(), 46);
            }
            fragmentRangeCalenderBinding.calendarView.setCalendarItemHeight(mCalendarHeight);
        }

        public void onClear() {
            fragmentRangeCalenderBinding.calendarView.clearSelectRange();
            fragmentRangeCalenderBinding.tvLeftWeek.setText(getString(R.string.range_start));
            fragmentRangeCalenderBinding.tvRightWeek.setText(getString(R.string.range_end));
            fragmentRangeCalenderBinding.tvLeftDate.setText("");
            fragmentRangeCalenderBinding.tvRightDate.setText("");
        }

        public void onCommit() {
            List<Calendar> calendars = fragmentRangeCalenderBinding.calendarView.getSelectCalendarRange();
            if (calendars == null || calendars.size() <= 1) {
                return;
            }
            for (Calendar c : calendars) {
                Log.e(TAG, c.toString()
                        + " -- " + c.getScheme()
                        + "  --  " + c.getLunar());
            }
            Toast.makeText(getContext(), String.format("选择了%s个日期: %s —— %s", calendars.size(),
                    calendars.get(0).toString(), calendars.get(calendars.size()-1).toString()),
                    Toast.LENGTH_SHORT).show();
            long start = calendars.get(0).getTimeInMillis();
            long end = calendars.get(calendars.size()-1).getTimeInMillis();
            Log.e(TAG,"SelectCalendarRange , start = " +start);
            Log.e(TAG,"SelectCalendarRange , start = " +start);
            sharedViewModel.setRepeatStart(start);
            sharedViewModel.setRepeatEnd(end);
            MainActivity.getNavController().navigateUp(); // back
        }
    }
}
