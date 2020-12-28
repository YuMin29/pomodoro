package com.yumin.pomodoro.ui.view;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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

import java.util.ArrayList;
import java.util.List;

public class AddMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    SharedViewModel mSharedViewModel;
    FragmentAddMissionBinding fragmentAddMissionBinding;

    public AddMissionFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentAddMissionBinding = (FragmentAddMissionBinding) getBinding();
        fragmentAddMissionBinding.itemRepeat.setOnCalenderListener(this);
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
                mAddMissionViewModel.getMission().getValue().setRepeatStart(time);
            }
        });

        mSharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                mAddMissionViewModel.getMission().getValue().setRepeatEnd(time);
            }
        });
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
        MainActivity.getNavController().navigate(R.id.fragment_range_calender);
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
