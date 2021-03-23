package com.yumin.pomodoro.ui.view.calender;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;

import com.yumin.pomodoro.databinding.FragmentCalenderBinding;
import com.yumin.pomodoro.ui.main.viewmodel.CalenderViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalenderFragment extends DataBindingFragment implements CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener {
    private static final String TAG = "[CalenderFragment]";
    private CalenderViewModel mCalenderViewModel;
    private FragmentCalenderBinding mFragmentCalenderBinding;
    private TextView mTextMonthDay;
    private TextView mTextYear;
    private TextView mTextLunar;
    private TextView mTextCurrentDay;
    private CalendarView mCalendarView;
    private RelativeLayout mRelativeTool;
    private int mCurrentYear;
    private CalendarLayout mCalendarLayout;
    private Map<String,List<UserMission>> userMissionMap = new HashMap<>();
    private Map<Long,List<MissionState>> missionStateMap = new HashMap<>();
    private RecyclerView mRecyclerView;
    private MissionStateAdapter mMissionStateAdapter;

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
        mRelativeTool = mFragmentCalenderBinding.rlTool;
        mCalendarView = mFragmentCalenderBinding.calendarView;
        mTextCurrentDay = mFragmentCalenderBinding.tvCurrentDay;
        mCalendarLayout = mFragmentCalenderBinding.calendarLayout;
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mCurrentYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        mRecyclerView = mFragmentCalenderBinding.recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMissionStateAdapter = new MissionStateAdapter(getContext());
        mRecyclerView.setAdapter(mMissionStateAdapter);
    }

    private void initObserver() {
        getMediatorMissionFromViewModel().observe(getViewLifecycleOwner(), new Observer<MissionResult>() {
            @Override
            public void onChanged(MissionResult result) {
                if (null == result || !result.isComplete())
                    return;

                Map<String, Calendar> calendarMap = new HashMap<>();
                List<MissionState> repeat;
                // remap for the mission state has same record day
                for (MissionState missionState : result.allMissionStates) {
                    long recordDay = missionState.getRecordDay();
                    if (missionStateMap.containsKey(recordDay)) {
                        missionStateMap.get(recordDay).add(missionState);
                    } else {
                        repeat = new ArrayList<>();
                        repeat.add(missionState);
                        missionStateMap.put(recordDay, repeat);
                    }
                }
                // go through all the map data
                for (Map.Entry<Long,List<MissionState>> entry : missionStateMap.entrySet()) {
                    long recordDay = entry.getKey();
                    List<MissionState> missionStates = entry.getValue();

                    LogUtil.logE(TAG, "[initObserver] Entry SET KEY: "+recordDay +
                            " ,VALUE SIZE : "+missionStates.size());

                    int year = Integer.parseInt(TimeMilli.getYear(recordDay));
                    int month = Integer.parseInt(TimeMilli.getMonth(recordDay));
                    int day = Integer.parseInt(TimeMilli.getDay(recordDay));

                    // create Calender
                    Calendar calendar = getSchemeCalendar(year,month,day);
                    List<UserMission> userMissions = new ArrayList<>();
                    // double confirm whether if the mission state exist
                    for (MissionState missionState : missionStates) {
                        for (UserMission userMission : result.allUserMissions) {
                            UserMission fetchUserMission = null;

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                if (userMission.getFirebaseMissionId().equals(missionState.getMissionId()))
                                    fetchUserMission = userMission;
                            } else {
                                if (Integer.valueOf(missionState.getMissionId()) == userMission.getId())
                                    fetchUserMission = userMission;
                            }

                            if (null != fetchUserMission) {
                                LogUtil.logE(TAG,"[initObserver] null != fetchUserMission , ID = "+missionState.getMissionId());
                                calendar.addScheme(fetchUserMission.getColor()," ");
                                userMissions.add(fetchUserMission);
                            }
                        }
                        userMissionMap.put(String.valueOf(recordDay),userMissions);
                    }
                    LogUtil.logE(TAG,"[initObserver] map put calender size = "+calendar.getSchemes().size());
                    calendarMap.put(getSchemeCalendar(year,month,day).toString(),calendar);
                }
                // set up calender view data
                mCalendarView.setSchemeDate(calendarMap);
            }
        });
    }

    private MediatorLiveData<MissionResult> getMediatorMissionFromViewModel() {
        // observe coming missions
        MediatorLiveData<MissionResult> mediatorMissions = new MediatorLiveData<>();
        final MissionResult missionResult = new MissionResult();
        mediatorMissions.addSource(mCalenderViewModel.getAllMissionStates(), new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStates) {
                missionResult.setAllMissionStates(missionStates);
                mediatorMissions.setValue(missionResult);
            }
        });
        mediatorMissions.addSource(mCalenderViewModel.getAllUserMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                missionResult.setAllUserMissions(userMissions);
                mediatorMissions.setValue(missionResult);
            }
        });
        return mediatorMissions;
    }

    private void initRecyclerView(List<UserMission> userMissionList, List<MissionState> missionStateList) {
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
        // show missions info when calender click
        LogUtil.logE(TAG,"[onCalendarSelect] calendar.getYear() = "+calendar.getYear()+
                        ",calendar.getMonth() = "+calendar.getMonth()+",calendar.getDay() = "+calendar.getDay());
        String currentTime = String.valueOf(TimeMilli.getInitTime(calendar.getYear(),calendar.getMonth(),calendar.getDay()));
        LogUtil.logE(TAG,"[onCalendarSelect] DAY = " +currentTime);
        initRecyclerView(userMissionMap.get(currentTime),missionStateMap.get(Long.valueOf(currentTime)));
    }

    @Override
    public void onYearChange(int year) {

    }

    private class MissionResult {
        public List<UserMission> allUserMissions;
        public List<MissionState> allMissionStates;

        public MissionResult() {}

        public void setAllUserMissions(List<UserMission> missions) {
            this.allUserMissions = missions;
        }

        public void setAllMissionStates(List<MissionState> missionStates) {
            this.allMissionStates = missionStates;
        }

        boolean isComplete() {
            return (allMissionStates != null && allUserMissions != null);
        }
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