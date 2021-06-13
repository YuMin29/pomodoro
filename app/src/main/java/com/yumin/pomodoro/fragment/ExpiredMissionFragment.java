package com.yumin.pomodoro.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.databinding.FragmentExpiredMissionBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.adapter.ExpandableBaseAdapter;
import com.yumin.pomodoro.viewmodel.ExpiredMissionViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ExpiredMissionFragment extends DataBindingFragment {
    private static String TAG = ExpiredMissionFragment.class.getSimpleName();
    private ExpiredMissionViewModel mExpiredMissionViewModel;
    private FragmentExpiredMissionBinding mFragmentExpiredMissionBinding;
    private List<Category> mCategory = new ArrayList<>();
    private List<UserMission> mFinishedMissions = new ArrayList<>();
    ExpandableBaseAdapter mExpandableViewAdapter;;

    @Override
    protected void initViewModel() {
        mExpiredMissionViewModel = getFragmentScopeViewModel(ExpiredMissionViewModel.class);
    }

    private void observeViewModel(){
        mExpiredMissionViewModel.getPastMissions().observe(getViewLifecycleOwner(), new Observer<ExpiredMissionViewModel.Result>() {
            @Override
            public void onChanged(ExpiredMissionViewModel.Result result) {
                if (result.isComplete()) {
                    List<UserMission> pastMissions = new ArrayList<>();
                    pastMissions.addAll(result.mPastNoneRepeatMissions);
                    pastMissions.addAll(result.mPastRepeatCustomizeMissions);
                    pastMissions.addAll(result.mPastRepeatEverydayMissions);

                    Map<String,List<UserMission>> remapUserMission = new TreeMap<>();

                    for (UserMission userMission : pastMissions) {
                        LogUtil.logE(TAG,"[getPastMissions] usermissions = "+userMission.toString());
                        long diff;
                        if (userMission.getRepeat() == UserMission.TYPE_DEFINE) {
                            diff = TimeToMillisecondUtil.getTodayEndTime() - userMission.getRepeatStart();
                        } else {
                            diff = TimeToMillisecondUtil.getTodayEndTime() - userMission.getOperateDay();
                        }
                        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        LogUtil.logE(TAG,"[getPastMissions] days "+days);

                        if (userMission.getRepeat() == UserMission.TYPE_EVERYDAY ||
                            userMission.getRepeat() == UserMission.TYPE_DEFINE) {
                            for (int i=1 ; i<=days ; i++) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(TimeToMillisecondUtil.getTodayEndTime());
                                calendar.add(Calendar.DAY_OF_MONTH, i * -1);
                                String dateString = TimeToMillisecondUtil.getDateString(calendar.getTimeInMillis());
                                LogUtil.logE(TAG,"TYPE_EVERYDAY TYPE_DEFINE  dateString"+dateString);
                                if (!remapUserMission.containsKey(dateString)) {
                                    remapUserMission.put(dateString,new ArrayList<>());
                                }
                                remapUserMission.get(dateString).add(userMission);
                            }
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(TimeToMillisecondUtil.getTodayEndTime());
                            calendar.add(Calendar.DAY_OF_MONTH, days * -1);
                            String dateString = TimeToMillisecondUtil.getDateString(calendar.getTimeInMillis());
                            LogUtil.logE(TAG,"NONE dateString"+dateString);
                            if (!remapUserMission.containsKey(dateString)) {
                                remapUserMission.put(dateString,new ArrayList<>());
                            }
                            remapUserMission.get(dateString).add(userMission);
                        }
                    }

                    List<Category> categoryList = new ArrayList<>();
                    for (Map.Entry<String, List<UserMission>> entry : remapUserMission.entrySet()) {
                        Category category = new Category(entry.getKey());
                        category.addAllMission(entry.getValue());
                        categoryList.add(category);
                    }
                    mExpandableViewAdapter.updateCategory(categoryList);

                    for(int i = 0; i < mExpandableViewAdapter.getGroupCount(); i++)
                        mFragmentExpiredMissionBinding.expiredMissionList.expandGroup(i);
                }
            }
        });

        mExpiredMissionViewModel.getMissionStates().observe(getViewLifecycleOwner(), new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStateList) {
                if (null != missionStateList) {
                    mExpandableViewAdapter.flashMissionState(missionStateList);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentExpiredMissionBinding = (FragmentExpiredMissionBinding) getBinding();
        mExpandableViewAdapter = new ExpandableBaseAdapter(getContext(),mCategory,mFinishedMissions);
        // set up adapter
        mFragmentExpiredMissionBinding.expiredMissionList.setAdapter(mExpandableViewAdapter);

        mExpandableViewAdapter.setCustomizeItemBehavior(new ExpandableBaseAdapter.CustomizeItemBehavior() {
            @Override
            public void onBindItemBehavior(MissionItemLayoutBinding binding, View view, int groupPosition, boolean isFinished, UserMission userMission) {
                // custom bind item behavior
                // gray out item if it finished
                if (isFinished) {
                    view.setAlpha(0.5f); // set opacity
                    binding.colorView.setVisibility(View.INVISIBLE);
                    binding.finishedIcon.setVisibility(View.VISIBLE);
                } else {
                    view.setAlpha(1f); // set opacity
                    binding.colorView.setVisibility(View.VISIBLE);
                    binding.finishedIcon.setVisibility(View.INVISIBLE);
                }

                binding.itemOperateDay.setVisibility(View.VISIBLE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
                Date date = new Date(userMission.getOperateDay());
                String dateStr = simpleDateFormat.format(date);
                binding.itemOperateDay.setText(getContext().getString(R.string.mission_operate_day) + dateStr);
            }
        });
        observeViewModel();
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_expired_mission, BR.expiredViewModel, mExpiredMissionViewModel);
    }
}
