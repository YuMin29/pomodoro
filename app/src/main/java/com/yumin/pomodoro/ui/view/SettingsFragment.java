package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentSettingsBinding;
import com.yumin.pomodoro.ui.main.viewmodel.SettingsViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;

public class SettingsFragment extends DataBindingFragment {
    private SettingsViewModel mSettingsViewModel;
    private FragmentSettingsBinding mFragmentSettingsBinding;

    @Override
    protected void initViewModel() {
        mSettingsViewModel = getFragmentScopeViewModel(SettingsViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_settings, BR.settingsViewModel, mSettingsViewModel)
                .addBindingParam(BR.settingsClickProxy,new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentSettingsBinding = (FragmentSettingsBinding) getBinding();
        observeViewModel();
    }

    private void observeViewModel(){
        mSettingsViewModel.getAutoStartNextMission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mFragmentSettingsBinding.autoStartMissionSwitch.setChecked(aBoolean);
            }
        });

        mSettingsViewModel.getAutoStartBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mFragmentSettingsBinding.autoStartBreakSwitch.setChecked(aBoolean);
            }
        });

        mSettingsViewModel.getIndexOfMissionBackgroundRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (null != integer)
                    mFragmentSettingsBinding.missionBackgroundMusic.setSelection(integer);
            }
        });

        mSettingsViewModel.getIndexOfFinishedMissionRingtone().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (null != integer)
                    mFragmentSettingsBinding.missionFinishMusic.setSelection(integer);
            }
        });

        mSettingsViewModel.getDisableBreak().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mFragmentSettingsBinding.disableBreak.setChecked(aBoolean);
            }
        });
    }

    public class ClickProxy{
        public static final String TAG = "[Settings_ClickProxy]";

        public void onSelectMissionBackgroundRingtone(AdapterView<?> parent, View view, int pos, long id) {
            //pos                                 get selected item position
            //view.getText()                      get label of selected item
            //parent.getAdapter().getItem(pos)    get item by pos
            //parent.getAdapter().getCount()      get item count
            //parent.getCount()                   get item count
            //parent.getSelectedItem()            get selected item
            //and other...
            LogUtil.logE(TAG,"[onSelectMissionBackgroundRingtone] pos = "+pos+" ,val = "
                    +parent.getSelectedItem().toString());
            mSettingsViewModel.setMissionBackgroundRingtone(pos);
        }

        public void onSelectMissionFinishedRingtone(AdapterView<?> parent, View view, int pos, long id) {
            //pos                                 get selected item position
            //view.getText()                      get label of selected item
            //parent.getAdapter().getItem(pos)    get item by pos
            //parent.getAdapter().getCount()      get item count
            //parent.getCount()                   get item count
            //parent.getSelectedItem()            get selected item
            //and other...
            LogUtil.logE(TAG,"[onSelectMissionFinishedRingtone] pos = "+pos+" ,val = "
                    +parent.getSelectedItem().toString());
            mSettingsViewModel.setFinishedMissionRingtone(pos);
        }

        public void onAutoStartNextMissionChanged(CompoundButton compoundButton, boolean isChecked){
            LogUtil.logE(TAG,"[onAutoStartMissionSwitchChanged] isChecked = "+isChecked);
            mSettingsViewModel.setAutoStartNextMission(isChecked);
        }

        public void onAutoStartBreakChanged(CompoundButton compoundButton, boolean isChecked){
            LogUtil.logE(TAG,"[onAutoStartBreakSwitchChanged] isChecked = "+isChecked);
            mSettingsViewModel.setAutoStartBreak(isChecked);
        }

        public void onDisableBreakChanged(CompoundButton compoundButton, boolean isChecked){
            LogUtil.logE(TAG,"[onDisableBreak] isChecked = "+isChecked);
            mSettingsViewModel.setDisableBreak(isChecked);
        }
    }
}