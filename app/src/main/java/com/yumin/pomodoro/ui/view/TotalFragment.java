package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.viewmodel.TotalViewModel;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

public class TotalFragment extends DataBindingFragment {
    TotalViewModel mTotalViewModel;

    // TODO: 3/16/21 need to implement total calender in here
    @Override
    protected void initViewModel() {
        mTotalViewModel = getFragmentScopeViewModel(TotalViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return null;
    }
}