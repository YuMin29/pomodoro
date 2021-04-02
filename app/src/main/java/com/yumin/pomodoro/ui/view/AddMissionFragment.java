package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;
import com.yumin.pomodoro.ui.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener,ItemDateView.OnOperateDayChanged {
    private static final String TAG = "[AddMissionFragment]";
    private AddMissionViewModel mAddMissionViewModel;
    private SharedViewModel mSharedViewModel;
    private FragmentAddMissionBinding mFragmentAddMissionBinding;
    private UserMission mMission = null;
    private long mLatestRepeatStart = -1L;
    private long mLatestRepeatEnd = -1L;
    private long mOperateDay = -1L;
    private long mRepeatStart = -1L;
    private long mRepeatEnd = -1L;

    public AddMissionFragment(){}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentAddMissionBinding = (FragmentAddMissionBinding) getBinding();
        mFragmentAddMissionBinding.itemRepeat.setOnCalenderListener(this);
        mFragmentAddMissionBinding.itemOperate.setOperateDayListener(this);
        initObserver();
    }

    private void navigateUp(){
        NavHostFragment.findNavController(this).navigateUp();
    }

    private void initObserver() {
        mAddMissionViewModel.getIsSaveButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getSaveButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mAddMissionViewModel.getIsCancelButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getCancelButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mSharedViewModel.getRepeatStart().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatStart] time = "+time);
                mAddMissionViewModel.updateRepeatStart(time);
                mLatestRepeatStart = time;
            }
        });

        mSharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                mAddMissionViewModel.updateRepeatEnd(time);
                mLatestRepeatEnd = time;
            }
        });

        mAddMissionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logD(TAG,"[Observe][getMission] mission = "+mission);
                if (mMission == null || mMission != mission) {
                    mMission = mission;
                    mOperateDay = mission.getOperateDay();
                    mRepeatStart = mission.getRepeatStart();
                    mRepeatEnd = mission.getRepeatEnd();
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
        return new DataBindingConfig(R.layout.fragment_add_mission, BR.addMissionViewModel, mAddMissionViewModel)
                .addBindingParam(BR.addMissionClickProxy, new ClickProxy());
    }

    @Override
    public void onOpened() {
        MissionManager.getInstance().setRangeCalenderId("-1");
        Bundle bundle = new Bundle();
        bundle.putLong("repeat_start", (mLatestRepeatStart != -1L) ? mLatestRepeatStart : mRepeatStart);
        bundle.putLong("repeat_end", (mLatestRepeatEnd != -1L) ? mLatestRepeatEnd : mRepeatEnd);
        bundle.putLong("mission_operate_day", mOperateDay);
        NavHostFragment.findNavController(this).navigate(R.id.fragment_range_calender,bundle);
    }

    @Override
    public void onOperateChanged(long time) {
        LogUtil.logD(TAG,"[onOperateChanged] = "+getTransferDate(time));
        if (mMission != null && mMission.getRepeat() == UserMission.TYPE_DEFINE &&
                mMission.getRepeatStart() != -1L && mMission.getRepeatEnd() != -1L) {
            if (time > mMission.getRepeatStart() || time > mMission.getRepeatEnd()) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.notice_choose_operate_day)
                        .setMessage(R.string.notice_clear_repeat_range)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLatestRepeatStart = -1L;
                                mLatestRepeatEnd = -1L;
                                mRepeatStart = -1L;
                                mRepeatEnd = -1L;

                                mFragmentAddMissionBinding.itemOperate.updateUI(time);
                                mOperateDay = time;
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                mFragmentAddMissionBinding.itemOperate.updateUI(time);
                mOperateDay = time;
            }
        } else {
            mFragmentAddMissionBinding.itemOperate.updateUI(time);
            mOperateDay = time;
        }
    }

    public class ClickProxy{
        public void onAddMissionButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick] GET MISSION TITLE = " +
                    mFragmentAddMissionBinding.missionTitle.getText().toString());

            if (mFragmentAddMissionBinding.missionTitle.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), R.string.notice_set_mission_title, Toast.LENGTH_SHORT).show();
                return;
            }

            mAddMissionViewModel.saveMission();
        }

        public void onCancelButtonClick(){
            LogUtil.logD(TAG,"[onCancelButtonClick]");
            mAddMissionViewModel.cancel();
        }
    }
}
