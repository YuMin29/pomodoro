package com.yumin.pomodoro.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentSettingsBinding;
import com.yumin.pomodoro.viewmodel.SettingsViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends DataBindingFragment {
    private SettingsViewModel mSettingsViewModel;
    private FragmentSettingsBinding mFragmentSettingsBinding;
    public static final String KEY_BACKUP_TIME = "_backup";
    public static final String KEY_RESTORE_TIME = "_restore";
    public static final String NAV_ITEM_SHARED_PREFERENCE = "nav_item";

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
        ((MainActivity)getActivity()).fullScreenMode(false);
        ((MainActivity)getActivity()).fabVisible(View.INVISIBLE);
        mFragmentSettingsBinding = (FragmentSettingsBinding) getBinding();
        observeViewModel();

        if (FirebaseAuth.getInstance().getCurrentUser() != null || getArguments() != null) {
            setBackupTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
            setRestoreTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    public void setBackupTime(String uid) {
        updateNaSubtitle(mFragmentSettingsBinding.backupSubTitle, uid == null ? null : uid + KEY_BACKUP_TIME);
    }

    public void setRestoreTime(String uid) {
        updateNaSubtitle(mFragmentSettingsBinding.restoreSubTitle, uid == null ? null : uid + KEY_RESTORE_TIME);
    }

    private void updateNaSubtitle(TextView subTitle, String key) {
        String backupTime = "";
        if (key != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(NAV_ITEM_SHARED_PREFERENCE, MODE_PRIVATE);
            backupTime = sharedPreferences.getString(key, "");
        }
        subTitle.setText(backupTime);
        subTitle.setVisibility(View.VISIBLE);
    }

    private void showAlertDialog(int title, int message, int fragmentId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                navigate(fragmentId);
            }

        });
        dialog.show();
    }

    private void showLockAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("高級版功能");
        dialog.setMessage("即將上線!");
        dialog.setPositiveButton(R.string.ok,null);
        dialog.show();
    }

    private void navigate(int fragmentId){
        NavHostFragment.findNavController(this).navigate(fragmentId);
    }

    private boolean isLoginFirebase() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getActivity(), R.string.require_login, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

        public void onBackupClick(){
            // 解鎖
            if (isLoginFirebase()) {
//                showAlertDialog(R.string.menu_backup,R.string.backup_message,R.id.fragment_backup);
                showLockAlertDialog();
            }
        }

        public void onRestoreClick(){
            // 解鎖
            if (isLoginFirebase()) {
//                showAlertDialog(R.string.menu_restore,R.string.restore_message,R.id.fragment_restore);
                showLockAlertDialog();
            }
        }
    }
}