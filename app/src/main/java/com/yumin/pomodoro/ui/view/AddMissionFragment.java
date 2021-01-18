package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener,ItemDateView.OnOperateDayChanged {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    SharedViewModel mSharedViewModel;
    FragmentAddMissionBinding fragmentAddMissionBinding;
    private static final int REPEAT_NONE = 0;
    private static final int REPEAT_EVERYDAY = 1;
    private static final int REPEAT_DEFINE = 2;
    private Mission mMission = null;
    private long latestRepeatStart = -1L;
    private long latestRepeatEnd = -1L;
    private long operateDay = -1L;
    private long repeatStart = -1L;
    private long repeatEnd = -1L;

    public AddMissionFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentAddMissionBinding = (FragmentAddMissionBinding) getBinding();
        fragmentAddMissionBinding.itemRepeat.setOnCalenderListener(this);
        fragmentAddMissionBinding.itemOperate.setOperateDayListener(this);
        initObserver();
    }

    private void initObserver() {
        mAddMissionViewModel.getSaveButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getSaveButtonClick] click = "+click);
                if (click) {
                    MainActivity.getNavController().navigateUp();
                }
            }
        });

        mAddMissionViewModel.getCancelButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getCancelButtonClick] click = "+click);
                if (click) {
                    MainActivity.getNavController().navigateUp();
                }
            }
        });

        mSharedViewModel.getRepeatStart().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatStart] time = "+time);
                mAddMissionViewModel.updateRepeatStart(time);
                latestRepeatStart = time;
            }
        });

        mSharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                mAddMissionViewModel.updateRepeatEnd(time);
                latestRepeatEnd = time;
            }
        });

        mAddMissionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                LogUtil.logD(TAG,"[Observe][getMission] mission = "+mission);
                if (mMission == null || mMission != mission) {
                    mMission = mission;
                    operateDay = mission.getOperateDay();
                    repeatStart = mission.getRepeatStart();
                    repeatEnd = mission.getRepeatEnd();
                }
            }
        });
    }

    private String getTransferDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(time));
    }

    @Override
    protected void initViewModel() {
        mAddMissionViewModel = getFragmentScopeViewModel(AddMissionViewModel.class);
        mSharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_add_mission, BR.viewmodel, mAddMissionViewModel)
                .addBindingParam(BR.clickProxy, new ClickProxy());
    }

    @Override
    public void onOpened() {
        MissionManager.getInstance().setRangeCalenderId(-1);
        Bundle bundle = new Bundle();
        bundle.putLong("repeat_start", (latestRepeatStart != -1L) ? latestRepeatStart : repeatStart);
        bundle.putLong("repeat_end", (latestRepeatEnd != -1L) ? latestRepeatEnd : repeatEnd);
        bundle.putLong("mission_operate_day",operateDay);
        MainActivity.getNavController().navigate(R.id.fragment_range_calender,bundle);
    }

    @Override
    public void onOperateChanged(long time) {
        LogUtil.logD(TAG,"[onOperateChanged] = "+getTransferDate(time));
        if (mMission != null && mMission.getRepeat() == REPEAT_DEFINE &&
                mMission.getRepeatStart() != -1L &&
                mMission.getRepeatEnd() != -1L) {
            if (time > mMission.getRepeatStart() ||
                    time > mMission.getRepeatEnd()) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("執行日小於重複區間")
                        .setMessage("清除已設置的重複區間")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                latestRepeatStart = -1L;
                                latestRepeatEnd = -1L;
                                repeatStart = -1L;
                                repeatEnd = -1L;

                                fragmentAddMissionBinding.itemOperate.updateUI(time);
                                operateDay = time;
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        } else {
            fragmentAddMissionBinding.itemOperate.updateUI(time);
            operateDay = time;
        }
    }

    public class ClickProxy{
        public void onSaveButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick]");
            mAddMissionViewModel.saveMission();
        }

        public void onCancelButtonClick(){
            LogUtil.logD(TAG,"[onCancelButtonClick]");
            mAddMissionViewModel.cancel();
        }
    }
}
