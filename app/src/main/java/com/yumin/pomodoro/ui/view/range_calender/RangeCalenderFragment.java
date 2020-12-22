package com.yumin.pomodoro.ui.view.range_calender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.databinding.FragmentRangeCalenderBinding;
import com.yumin.pomodoro.ui.base.EditViewModelFactory;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RangeCalenderFragment extends Fragment implements CalendarView.OnCalendarRangeSelectListener,
        CalendarView.OnMonthChangeListener, CalendarView.OnCalendarInterceptListener, View.OnClickListener {
    private static final String TAG = "[RangeCalenderFragment]";
    private FragmentRangeCalenderBinding fragmentRangeCalenderBinding;
    private int mCalendarHeight;
    private static final String[] WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private RangeCalenderViewModel rangeCalenderViewModel;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int missionId = -1;
        if (bundle != null) {
            missionId = bundle.getInt("missionId");
        }
        fragmentRangeCalenderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_range_calender,container,false);
        fragmentRangeCalenderBinding.setLifecycleOwner(this);
        fragmentRangeCalenderBinding.setClickProxy(new ClickProxy());

        initViewModel(missionId);
        initView();
        initObserve();
        return fragmentRangeCalenderBinding.getRoot();
    }

    // TODO: put chosen date to start &　end. If no data, just init.
    private void initView(){
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        fragmentRangeCalenderBinding.calendarView.setOnCalendarRangeSelectListener(this);
        fragmentRangeCalenderBinding.calendarView.setOnMonthChangeListener(this);
        //设置日期拦截事件，当前有效
        fragmentRangeCalenderBinding.calendarView.setOnCalendarInterceptListener(this);
        fragmentRangeCalenderBinding.tvTitle.setOnClickListener(this);

        mCalendarHeight = dipToPx(getContext(), 46);

        fragmentRangeCalenderBinding.calendarView.setRange(fragmentRangeCalenderBinding.calendarView.getCurYear(), fragmentRangeCalenderBinding.calendarView.getCurMonth(),
                fragmentRangeCalenderBinding.calendarView.getCurDay(),2030,12,31
        );
        fragmentRangeCalenderBinding.calendarView.post(new Runnable() {
            @Override
            public void run() {
                fragmentRangeCalenderBinding.calendarView.scrollToCurrent();
            }
        });
    }

    private void initViewModel(int id){
        rangeCalenderViewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(getActivity().getApplication()),getContext()),id)).get(RangeCalenderViewModel.class);
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
                        Date date = new Date(start);
                        LogUtil.logD(TAG,"[initObserve] simpleDateFormat = "+simpleDateFormat.format(date));
                        fragmentRangeCalenderBinding.tvLeftDate.setText(simpleDateFormat.format(date));
                        fragmentRangeCalenderBinding.calendarView.setSelectStartCalendar(date.getYear(),date.getMonth(),date.getDay());
                    }
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
                        Date date = new Date(end);
                        LogUtil.logD(TAG,"[initObserve] simpleDateFormat = "+simpleDateFormat.format(date));
                        fragmentRangeCalenderBinding.tvLeftDate.setText(simpleDateFormat.format(date));
                        fragmentRangeCalenderBinding.calendarView.setSelectEndCalendar(date.getYear(),date.getMonth(),date.getDay());
                    }
                }
            }
        });
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
                Log.e("SelectCalendarRange", c.toString()
                        + " -- " + c.getScheme()
                        + "  --  " + c.getLunar());
            }
            Toast.makeText(getContext(), String.format("选择了%s个日期: %s —— %s", calendars.size(),
                    calendars.get(0).toString(), calendars.get(calendars.size()-1).toString()),
                    Toast.LENGTH_SHORT).show();
            long start = calendars.get(0).getTimeInMillis();
            long end  = calendars.get(calendars.size()-1).getTimeInMillis();
            rangeCalenderViewModel.setRepeatStart(start);
            rangeCalenderViewModel.setRepeatEnd(end);
            rangeCalenderViewModel.setClickCommit(true);
        }
    }
}
