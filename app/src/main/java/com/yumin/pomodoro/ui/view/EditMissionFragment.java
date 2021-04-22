package com.yumin.pomodoro.ui.view;

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
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.view.mission.MissionBaseFragment;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.MissionManager;

public class EditMissionFragment extends MissionBaseFragment {
    private static final String TAG = "[EditMissionFragment]";
    EditMissionViewModel editMissionViewModel;
    FragmentEditMissionBinding fragmentEditMissionBinding;
    UserMission editMission;

    public EditMissionFragment() {}

    @Override
    protected UserMission getMission() {
        return editMission;
    }

    @Override
    protected void updateItemOperateUI(long time) {
        fragmentEditMissionBinding.missionAttributeView.getItemOperate().updateUI(time);
    }

    @Override
    protected void setRangeCalenderId() {
        MissionManager.getInstance().setRangeCalenderId(MissionManager.getInstance().getStrEditId());
    }

    @Override
    protected void updateEditMissionRepeatStart(long time) {
        editMissionViewModel.updateEditMissionRepeatStart(time);
    }

    @Override
    protected void updateEditMissionRepeatEnd(long time) {
        editMissionViewModel.updateEditMissionRepeatEnd(time);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        editMissionViewModel = getFragmentScopeViewModel(EditMissionViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_edit_mission, BR.editMissionViewModel, editMissionViewModel)
                .addBindingParam(BR.editMissionClickProxy ,new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentEditMissionBinding = (FragmentEditMissionBinding) getBinding();
        fragmentEditMissionBinding.missionAttributeView.getItemRepeat().setOnRepeatTypeListener(this);
        fragmentEditMissionBinding.missionAttributeView.getItemOperate().setOperateDayListener(this);
        initObserver();
    }

    private void initObserver() {
        editMissionViewModel.getIsSaveButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getSaveButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        editMissionViewModel.getIsCancelButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getCancelButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        editMissionViewModel.getEditMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logE(TAG,"[onChanged] MISSION = "+mission.toString());
                if (mission != null) {
                    editMission = mission;
                    mOperateDay = editMission.getOperateDay();
                    mRepeatStart = editMission.getRepeatStart();
                    mRepeatEnd = editMission.getRepeatEnd();
                }
            }
        });

    }

    public class ClickProxy{
        public void onSaveButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick]");

            if (fragmentEditMissionBinding.missionAttributeView.getMissionTitle().getText().toString().isEmpty()) {
                Toast.makeText(getContext(), R.string.notice_set_mission_title, Toast.LENGTH_SHORT).show();
                return;
            }

            editMissionViewModel.saveMission();
        }

        public void onCancelButtonClick(){
            LogUtil.logD(TAG,"[onCancelButtonClick]");
            editMissionViewModel.cancel();
        }
    }
}
