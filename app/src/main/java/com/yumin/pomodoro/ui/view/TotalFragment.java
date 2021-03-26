package com.yumin.pomodoro.ui.view;

import com.yumin.pomodoro.ui.main.viewmodel.TotalViewModel;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;

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