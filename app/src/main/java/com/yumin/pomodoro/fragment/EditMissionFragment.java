package com.yumin.pomodoro.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentEditMissionBinding;
import com.yumin.pomodoro.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.base.MissionBaseFragment;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.MissionManager;

public class EditMissionFragment extends MissionBaseFragment {
    private static final String TAG = EditMissionFragment.class.getSimpleName();
    EditMissionViewModel mEditMissionViewModel;
    FragmentEditMissionBinding mFragmentEditMissionBinding;
    UserMission mEditMission;

    @Override
    protected UserMission getMission() {
        return mEditMission;
    }

    @Override
    protected void updateItemOperateUI(long time) {
        mFragmentEditMissionBinding.missionAttributeView.getItemOperate().updateUI(time);
    }

    @Override
    protected void setRangeCalenderId() {
        MissionManager.getInstance().setRangeCalenderId(MissionManager.getInstance().getStrEditId());
    }

    @Override
    protected void updateEditMissionRepeatStart(long time) {
        mEditMissionViewModel.updateEditMissionRepeatStart(time);
    }

    @Override
    protected void updateEditMissionRepeatEnd(long time) {
        mEditMissionViewModel.updateEditMissionRepeatEnd(time);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mEditMissionViewModel = getFragmentScopeViewModel(EditMissionViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_edit_mission, BR.editMissionViewModel, mEditMissionViewModel)
                .addBindingParam(BR.editMissionClickProxy ,new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentEditMissionBinding = (FragmentEditMissionBinding) getBinding();
        mFragmentEditMissionBinding.missionAttributeView.getItemRepeat().setOnRepeatTypeListener(this);
        mFragmentEditMissionBinding.missionAttributeView.getItemOperate().setOperateDayListener(this);
        initObserver();
    }

    private void initObserver() {
        mEditMissionViewModel.getIsSaveButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[getSaveButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mEditMissionViewModel.getIsCancelButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[getCancelButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mEditMissionViewModel.getEditMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logE(TAG,"[onChanged] mission = "+mission.toString());
                if (mission != null) {
                    mEditMission = mission;
                    mOperateDay = mEditMission.getOperateDay();
                    mRepeatStart = mEditMission.getRepeatStart();
                    mRepeatEnd = mEditMission.getRepeatEnd();
                }
            }
        });

    }

    public class ClickProxy{
        public void onSaveButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick]");
            if (mFragmentEditMissionBinding.missionAttributeView.getMissionTitle().getText().toString().isEmpty()) {
                Toast.makeText(getContext(), R.string.notice_set_mission_title, Toast.LENGTH_SHORT).show();
                return;
            }

            mEditMissionViewModel.saveMission();
        }

        public void onCancelButtonClick(){
            LogUtil.logD(TAG,"[onCancelButtonClick]");
            mEditMissionViewModel.cancel();
        }
    }
}
