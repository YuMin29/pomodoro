package com.yumin.pomodoro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.viewmodel.BackupViewModel;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupFragment extends DataBindingFragment {
    private static final String TAG = BackupFragment.class.getSimpleName();
    private BackupViewModel mBackupViewModel;

    @Override
    protected void initViewModel() {
        mBackupViewModel = getFragmentScopeViewModel(BackupViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void observeViewModel(){
        mBackupViewModel.getProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean progress) {
                String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.NAV_ITEM_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        MainActivity.KEY_BACKUP_TIME,"上次備份時間:" + nowDate).commit();

                if (!progress) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("backupFinished",true);
                    navigateUp(bundle);
                }

//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.setBackupTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

        mBackupViewModel.getRoomMissions().observe(getViewLifecycleOwner(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                LogUtil.logE(TAG, "[getRoomMissions] isEmpty = " +
                        userMissionList.isEmpty());
                mBackupViewModel.setIsRoomMissionsExist(!userMissionList.isEmpty());
                mBackupViewModel.operateBackup();
            }
        });

        mBackupViewModel.getRoomMissionStates().observe(getViewLifecycleOwner(), new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStates) {
                LogUtil.logE(TAG, "[getRoomMissionStates] isEmpty = " +
                        missionStates.isEmpty());
                mBackupViewModel.setIsRoomMissionStatesExist(!missionStates.isEmpty());
            }
        });
    }

    private void navigateUp(Bundle bundle){
        NavHostFragment.findNavController(this).navigate(R.id.nav_settings,bundle);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_backup, BR.backup_view_model,mBackupViewModel);
    }
}