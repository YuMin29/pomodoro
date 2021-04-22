package com.yumin.pomodoro.ui.view;

import android.os.Build;
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
import com.yumin.pomodoro.data.repository.firebase.User;
import com.yumin.pomodoro.databinding.FragmentExpiredMissionBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;
import com.yumin.pomodoro.ui.main.adapter.ExpandableViewAdapter;
import com.yumin.pomodoro.ui.main.viewmodel.ExpiredMissionViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.SortTimeUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ExpiredMissionFragment extends DataBindingFragment {
    private static String TAG = "[ExpiredMissionFragment]";
    private ExpiredMissionViewModel mExpiredMissionViewModel;
    private FragmentExpiredMissionBinding mFragmentExpiredMissionBinding;
    private List<Category> mCategory = new ArrayList<>();
    private List<UserMission> mFinishedMissions = new ArrayList<>();
    ExpandableViewAdapter expandableViewAdapter;;

    @Override
    protected void initViewModel() {
        mExpiredMissionViewModel = getFragmentScopeViewModel(ExpiredMissionViewModel.class);
    }

    private void observeViewModel(){
        // fetch mission list
        mExpiredMissionViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                if (userMissionList != null) {
                    mExpiredMissionViewModel.fetchPastMissions();
                }
            }
        });
        // category: date
        mExpiredMissionViewModel.getPastMissions().observe(getViewLifecycleOwner(), new Observer<ExpiredMissionViewModel.Result>() {
            @Override
            public void onChanged(ExpiredMissionViewModel.Result result) {
                if (result.isComplete()) {
                    List<UserMission> pastMissions = new ArrayList<>();
                    pastMissions.addAll(result.missionsByOperateDay);
                    pastMissions.addAll(result.missionsByRepeatRange);
                    pastMissions.addAll(result.missionsByRepeatType);

                    Map<String,List<UserMission>> remapUserMission = new TreeMap<>();

                    for (UserMission userMission : pastMissions) {
                        LogUtil.logE(TAG,"[getPastMissions] USERMISSION = "+userMission.toString());
                        long diff;
                        if (userMission.getRepeat() == UserMission.TYPE_DEFINE) {
                            diff = TimeToMillisecondUtil.getTodayEndTime() - userMission.getRepeatStart();
                        } else {
                            diff = TimeToMillisecondUtil.getTodayEndTime() - userMission.getOperateDay();
                        }
                        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        LogUtil.logE(TAG,"[getPastMissions][Days] "+days);

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
                    // tree map to array list
                    List<Category> categoryList = new ArrayList<>();
                    for (Map.Entry<String, List<UserMission>> entry : remapUserMission.entrySet()) {
                        Category category = new Category(entry.getKey());
                        category.addAllMission(entry.getValue());
                        categoryList.add(category);
                    }
                    expandableViewAdapter.flashCategory(categoryList);
                    // expand all group
                    for(int i=0; i < expandableViewAdapter.getGroupCount(); i++)
                        mFragmentExpiredMissionBinding.expiredMissionList.expandGroup(i);
                }
            }
        });

        mExpiredMissionViewModel.getPastFinishedMission().observe(getViewLifecycleOwner(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                if (userMissionList != null) {
//                    expandableViewAdapter.flashFinishedMission(userMissionList);
                }
            }
        });

        mExpiredMissionViewModel.getMissionStates().observe(getViewLifecycleOwner(), new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStateList) {
                if (null != missionStateList) {
                    expandableViewAdapter.flashMissionState(missionStateList);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentExpiredMissionBinding = (FragmentExpiredMissionBinding) getBinding();
        expandableViewAdapter = new ExpandableViewAdapter(getContext(),mCategory,mFinishedMissions);
        // set up adapter
        mFragmentExpiredMissionBinding.expiredMissionList.setAdapter(expandableViewAdapter);

        expandableViewAdapter.setOnBindView(new ExpandableViewAdapter.OnBindView() {
            @Override
            public void onBindItem(MissionItemLayoutBinding binding, View view, int groupPosition, boolean isFinished, UserMission userMission) {
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
