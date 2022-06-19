package com.yumin.pomodoro.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.adapter.MissionStateAdapter;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import com.yumin.pomodoro.databinding.FragmentCalenderBinding;
import com.yumin.pomodoro.viewmodel.CalenderViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalenderFragment extends DataBindingFragment implements CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener {
    private static final String TAG = CalenderFragment.class.getSimpleName();
    private CalenderViewModel mCalenderViewModel;
    private FragmentCalenderBinding mFragmentCalenderBinding;
    private TextView mTextMonthDay;
    private TextView mTextYear;
    private TextView mTextLunar;
    private TextView mTextCurrentDay;
    private CalendarView mCalendarView;
    private int mCurrentYear;
    private CalendarLayout mCalendarLayout;
    private Map<String,List<UserMission>> mUserMissionMap;
    private Map<Long,List<MissionState>> mMissionStateMap;
    private RecyclerView mRecyclerView;
    private MissionStateAdapter mMissionStateAdapter;

    // singleton
    public static CalenderFragment newInstance(){
        return new CalenderFragment();
    }

    @Override
    protected void initViewModel() {
        mCalenderViewModel = getFragmentScopeViewModel(CalenderViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_calender, BR.calenderViewModel, mCalenderViewModel)
                .addBindingParam(BR.calenderClickProxy, new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentCalenderBinding = (FragmentCalenderBinding) getBinding();
        initView();
        initObserver();
    }

    private void initView() {
        mTextMonthDay = mFragmentCalenderBinding.tvMonthDay;
        mTextYear = mFragmentCalenderBinding.tvYear;
        mTextLunar = mFragmentCalenderBinding.tvLunar;
        mCalendarView = mFragmentCalenderBinding.calendarView;
        mTextCurrentDay = mFragmentCalenderBinding.tvCurrentDay;
        mCalendarLayout = mFragmentCalenderBinding.calendarLayout;
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mCurrentYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + getString(R.string.calender_month)
                + mCalendarView.getCurDay() + getString(R.string.calender_day));
        mTextLunar.setText(R.string.calender_today);
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        mRecyclerView = mFragmentCalenderBinding.recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMissionStateAdapter = new MissionStateAdapter(getContext());
        mRecyclerView.setAdapter(mMissionStateAdapter);
    }

    private void initObserver() {
        mCalenderViewModel.getMissionResult().observe(getViewLifecycleOwner(), new Observer<CalenderViewModel.MissionResult>() {
            @Override
            public void onChanged(CalenderViewModel.MissionResult result) {
                if (result == null || !result.isComplete())
                    return;

                Map<String, Calendar> calendarMap = new HashMap<>();
                List<MissionState> missionStateList;
                mMissionStateMap = new HashMap<>();
                mUserMissionMap = new HashMap<>();
                for (MissionState missionState : result.mAllMissionStates) {
                    long recordDay = missionState.getRecordDay();
                    if (mMissionStateMap.containsKey(recordDay)) {
                        mMissionStateMap.get(recordDay).add(missionState);
                    } else {
                        missionStateList = new ArrayList<>();
                        missionStateList.add(missionState);
                        mMissionStateMap.put(recordDay, missionStateList);
                    }
                }
                for (Map.Entry<Long,List<MissionState>> entry : mMissionStateMap.entrySet()) {
                    long recordDay = entry.getKey();
                    List<MissionState> missionStates = entry.getValue();

                    LogUtil.logE(TAG, "[getMediatorMissionFromViewModel] entry set key: "+recordDay +
                            " ,value size : "+missionStates.size());

                    int year = Integer.parseInt(TimeToMillisecondUtil.getYear(recordDay));
                    int month = Integer.parseInt(TimeToMillisecondUtil.getMonth(recordDay));
                    int day = Integer.parseInt(TimeToMillisecondUtil.getDay(recordDay));

                    Calendar calendar = getSchemeCalendar(year,month,day);
                    List<UserMission> userMissions = new ArrayList<>();
                    for (MissionState missionState : missionStates) {
                        for (UserMission userMission : result.mAllUserMissions) {
                            UserMission fetchUserMission = null;

                            if (Integer.valueOf(missionState.getMissionId()) == userMission.getId())
                                fetchUserMission = userMission;

                            if (null != fetchUserMission) {
                                LogUtil.logE(TAG,"fetchUserMission , ID = "+missionState.getMissionId());
                                calendar.addScheme(fetchUserMission.getColor()," ");
                                userMissions.add(fetchUserMission);
                            }
                        }
                        mUserMissionMap.put(String.valueOf(recordDay),userMissions);
                    }
                    calendarMap.put(getSchemeCalendar(year,month,day).toString(),calendar);
                }
                mCalendarView.setSchemeDate(calendarMap);
                updateRecyclerViewData(mCalendarView.getSelectedCalendar());
            }
        });
    }


    private void setUpRecyclerViewAdapter(List<UserMission> userMissionList, List<MissionState> missionStateList) {
        mMissionStateAdapter.setDataList(userMissionList,missionStateList);
    }

    private Calendar getSchemeCalendar(int year, int month, int day) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        LogUtil.logE(TAG,"[onCalendarSelect] calendar.getYear() = "+calendar.getYear()+
                        ",calendar.getMonth() = "+calendar.getMonth()+",calendar.getDay() = "+calendar.getDay());
        if (isClick)
            updateRecyclerViewData(calendar);
    }

    private void updateRecyclerViewData(Calendar calendar){
        String currentTime = String.valueOf(TimeToMillisecondUtil.getStartTime(calendar.getYear(),calendar.getMonth(),calendar.getDay()));
        LogUtil.logE(TAG,"[onCalendarSelect] day = " +currentTime);
        List<MissionState> missionStateList = mMissionStateMap.get(Long.valueOf(currentTime));
        if (null == missionStateList) {
            mFragmentCalenderBinding.noMissionStateLinearLayout.setVisibility(View.VISIBLE);
            mFragmentCalenderBinding.recyclerView.setVisibility(View.GONE);
        } else {
            setUpRecyclerViewAdapter(mUserMissionMap.get(currentTime), mMissionStateMap.get(Long.valueOf(currentTime)));
            mFragmentCalenderBinding.noMissionStateLinearLayout.setVisibility(View.GONE);
            mFragmentCalenderBinding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onYearChange(int year) {

    }

    public class ClickProxy {
        public void onFrameLayoutClick() {
            mCalendarView.scrollToCurrent();
        }

        public void onTextMonthClick() {
            if (!mCalendarLayout.isExpand()) {
                mCalendarLayout.expand();
                return;
            }
            mCalendarView.showYearSelectLayout(mCurrentYear);
            mTextLunar.setVisibility(View.GONE);
            mTextYear.setVisibility(View.GONE);
            mTextMonthDay.setText(String.valueOf(mCurrentYear));
        }
    }
}