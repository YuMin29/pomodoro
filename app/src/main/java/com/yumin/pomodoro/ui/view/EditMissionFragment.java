package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentEditMissionBinding;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener, ItemDateView.OnOperateDayChanged{
    private static final String TAG = "[EditMissionFragment]";
    EditMissionViewModel editMissionViewModel;
    SharedViewModel sharedViewModel;
    FragmentEditMissionBinding fragmentEditMissionBinding;
//    RangeCalenderViewModel rangeCalenderViewModel;
    Mission editMission;
    private static final int REPEAT_NONE = 0;
    private static final int REPEAT_EVERYDAY = 1;
    private static final int REPEAT_DEFINE = 2;

    public EditMissionFragment() {}

    @Override
    protected void initViewModel() {
        editMissionViewModel = getFragmentScopeViewModel(EditMissionViewModel.class);
        sharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_edit_mission, BR.viewmodel,editMissionViewModel)
                .addBindingParam(BR.clickProxy,new ClickProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentEditMissionBinding = (FragmentEditMissionBinding) getBinding();
        fragmentEditMissionBinding.itemRepeat.setOnCalenderListener(this);
        fragmentEditMissionBinding.itemOperate.setOperateDayListener(this);
        initObserver();
    }

    private void initObserver() {
        editMissionViewModel.getSaveButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getSaveButtonClick] click = "+click);
                if (click) {
                    MainActivity.getNavController().navigateUp();
                }
            }
        });

        editMissionViewModel.getCancelButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getCancelButtonClick] click = "+click);
                if (click) {
                    MainActivity.getNavController().navigateUp();
                }
            }
        });

        sharedViewModel.getRepeatStart().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatStart] time = "+time);
                editMissionViewModel.getEditMission().getValue().setRepeatStart(time);
            }
        });

        sharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                editMissionViewModel.getEditMission().getValue().setRepeatEnd(time);
            }
        });

        editMissionViewModel.getEditMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                if (mission != null) {
                    editMission = mission;
                }
            }
        });
    }

    private String getTransferDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(time));
    }

    @Override
    public void onOpened() {
        MissionManager.getInstance().setRangeCalenderId(MissionManager.getInstance().getEditId());
        MainActivity.getNavController().navigate(R.id.fragment_range_calender);
    }

    @Override
    public void onOperateChanged(long time) {
        // TODO: 12/29/20 check operate day
        LogUtil.logD(TAG,"[onOperateChanged] = "+getTransferDate(time));
        if (editMission!=null && editMission.getRepeat() == REPEAT_DEFINE &&
                editMission.getRepeatStart() != -1L &&
                editMission.getRepeatEnd() != -1L) {
            // TODO: 12/29/20 compare operate day with defined repeat day
            if (time > editMissionViewModel.getEditMission().getValue().getRepeatStart() ||
                    time > editMissionViewModel.getEditMission().getValue().getRepeatEnd()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("執行日小於重複區間")
                        .setMessage("將會清除已設置的重複區間")
                        .setPositiveButton(R.string.dialog_ok+"??", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editMissionViewModel.updateRepeatStart(-1L);
                                editMissionViewModel.updateRepeatEnd(-1L);
                                fragmentEditMissionBinding.itemOperate.updateUI(time);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
                                LogUtil.logD(TAG,"[onOperateChanged] click cancel");
                            }
                        });
                builder.show();
            }
        } else {
            fragmentEditMissionBinding.itemOperate.updateUI(time);
        }
    }

    public class ClickProxy{

        public void onSaveButtonClick(){
            LogUtil.logD(TAG,"[onSaveButtonClick]");
            editMissionViewModel.saveMission();
        }

        public void onCancelButtonClick(){
            LogUtil.logD(TAG,"[onCancelButtonClick]");
            editMissionViewModel.cancel();
        }
    }
}
