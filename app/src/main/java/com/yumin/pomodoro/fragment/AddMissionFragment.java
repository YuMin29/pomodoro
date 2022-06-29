package com.yumin.pomodoro.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.MissionManager;

public class AddMissionFragment extends MissionBaseFragment {
    private static final String TAG = AddMissionFragment.class.getSimpleName();
    private AddMissionViewModel mAddMissionViewModel;
    private FragmentAddMissionBinding mFragmentAddMissionBinding;
    private UserMission mMission = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).fabVisible(View.INVISIBLE);
        mFragmentAddMissionBinding = (FragmentAddMissionBinding) getBinding();
        mFragmentAddMissionBinding.missionAttributeView.getItemRepeat().setOnRepeatTypeListener(this);
        mFragmentAddMissionBinding.missionAttributeView.getItemOperate().setOperateDayListener(this);
        initObserver();
    }

    private void initObserver() {
        mAddMissionViewModel.getIsSaveButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[getSaveButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mAddMissionViewModel.getIsCancelButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[getCancelButtonClick] click = "+click);
                if (click) {
                    navigateUp();
                }
            }
        });

        mAddMissionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                LogUtil.logD(TAG,"[getMission] mission = "+mission);
                if (mMission == null || mMission != mission) {
                    mMission = mission;
                    mOperateDay = mission.getOperateDay();
                    mRepeatStart = mission.getRepeatStart();
                    mRepeatEnd = mission.getRepeatEnd();
                }
            }
        });
    }

    @Override
    protected UserMission getMission() {
        return mMission;
    }

    @Override
    protected void updateItemOperateUI(long time) {
        mFragmentAddMissionBinding.missionAttributeView.getItemOperate().updateUI(time);
    }

    @Override
    protected void setRangeCalenderId() {
        MissionManager.getInstance().setRangeCalenderId("-1");
    }

    @Override
    protected void updateEditMissionRepeatStart(long time) {
        mAddMissionViewModel.updateRepeatStart(time);
    }

    @Override
    protected void updateEditMissionRepeatEnd(long time) {
        mAddMissionViewModel.updateRepeatEnd(time);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mAddMissionViewModel = getFragmentScopeViewModel(AddMissionViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_add_mission, BR.addMissionViewModel, mAddMissionViewModel)
                .addBindingParam(BR.addMissionClickProxy, new ClickProxy());
    }

    public class ClickProxy{
        public void onAddMissionButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick] mission title = " +
                    mFragmentAddMissionBinding.missionAttributeView.getMissionTitle().getText().toString());

            if (mFragmentAddMissionBinding.missionAttributeView.getMissionTitle().getText().toString().isEmpty()) {
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
