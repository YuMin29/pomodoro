package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener,ItemDateView.OnOperateDayChanged {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    SharedViewModel mSharedViewModel;
    FragmentAddMissionBinding fragmentAddMissionBinding;
    private static final int REPEAT_NONE = 0;
    private static final int REPEAT_EVERYDAY = 1;
    private static final int REPEAT_DEFINE = 2;
    private Mission mMission = null;
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
                repeatStart = time;
            }
        });

        mSharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                mAddMissionViewModel.updateRepeatEnd(time);
                repeatEnd = time;
            }
        });

        mAddMissionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                LogUtil.logD(TAG,"[Observe][getMission] mission = "+mission);
                if (mMission == null || mMission != mission) {
                    mMission = mission;
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
        if (repeatStart != -1L && repeatEnd != -1L) {
            Bundle bundle = new Bundle();
            bundle.putLong("repeat_start",repeatStart);
            bundle.putLong("repeat_end",repeatEnd);
            MainActivity.getNavController().navigate(R.id.fragment_range_calender,bundle);
        } else {
            MainActivity.getNavController().navigate(R.id.fragment_range_calender);
        }

    }

    @Override
    public void onOperateChanged(long time) {
        LogUtil.logD(TAG,"[onOperateChanged] = "+getTransferDate(time));
        if (mMission != null && mMission.getRepeat() == REPEAT_DEFINE &&
                mMission.getRepeatStart() != -1L &&
                mMission.getRepeatEnd() != -1L) {
            // TODO: 12/29/20 compare operate day with defined repeat day
            if (time > mMission.getRepeatStart() ||
                    time > mMission.getRepeatEnd()) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("執行日小於重複區間")
                        .setMessage("清除已設置的重複區間")
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAddMissionViewModel.updateRepeatStart(-1L);
                                mAddMissionViewModel.updateRepeatEnd(-1L);
                                fragmentAddMissionBinding.itemOperate.updateUI(time);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        } else {
            fragmentAddMissionBinding.itemOperate.updateUI(time);
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
