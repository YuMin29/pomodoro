package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.viewmodel.SettingsViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;

public class SettingsFragment extends DataBindingFragment {

    private SettingsViewModel mSettingsViewModel;

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

    }

    public class ClickProxy{
        public static final String TAG = "[Settings_ClickProxy]";

        public void onSelectItem(AdapterView<?> parent, View view, int pos, long id) {
            //pos                                 get selected item position
            //view.getText()                      get label of selected item
            //parent.getAdapter().getItem(pos)    get item by pos
            //parent.getAdapter().getCount()      get item count
            //parent.getCount()                   get item count
            //parent.getSelectedItem()            get selected item
            //and other...
            LogUtil.logE(TAG,"[onSelectItem] pos = "+pos+" ,val = "
                    +parent.getSelectedItem().toString());
        }

        public void onAutoStartMissionSwitchChanged(CompoundButton compoundButton, boolean isChecked){
            LogUtil.logE(TAG,"[onAutoStartMissionSwitchChanged]");
        }

        public void onAutoStartBreakSwitchChanged(CompoundButton compoundButton, boolean isChecked){
            LogUtil.logE(TAG,"[onAutoStartBreakSwitchChanged]");
        }
    }
}