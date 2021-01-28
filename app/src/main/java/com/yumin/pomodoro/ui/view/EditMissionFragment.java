package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

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
    private long latestRepeatStart = -1L;
    private long latestRepeatEnd = -1L;
    private long operateDay = -1L;
    private long repeatStart = -1L;
    private long repeatEnd = -1L;

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

    private void navigateUp(){
        NavHostFragment.findNavController(this).navigateUp();
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
//                    MainActivity.getNavController().navigateUp();
                    navigateUp();
                }
            }
        });

        editMissionViewModel.getCancelButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean click) {
                LogUtil.logD(TAG,"[Observe][getCancelButtonClick] click = "+click);
                if (click) {
//                    MainActivity.getNavController().navigateUp();
                    navigateUp();
                }
            }
        });

        sharedViewModel.getRepeatStart().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatStart] time = "+time);
                editMissionViewModel.updateRepeatStart(time);
                latestRepeatStart = time;
            }
        });

        sharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                LogUtil.logD(TAG,"[Observe][getRepeatEnd] time = "+time);
                editMissionViewModel.updateRepeatEnd(time);
                latestRepeatEnd = time;
            }
        });

        editMissionViewModel.getEditMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                if (mission != null) {
                    editMission = mission;
                    operateDay = editMission.getOperateDay();
                    repeatStart = editMission.getRepeatStart();
                    repeatEnd = editMission.getRepeatEnd();
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
        Bundle bundle = new Bundle();
        bundle.putLong("repeat_start", (latestRepeatStart != -1L) ? latestRepeatStart : repeatStart);
        bundle.putLong("repeat_end", (latestRepeatEnd != -1L) ? latestRepeatEnd : repeatEnd);
        bundle.putLong("mission_operate_day",operateDay);
//        MainActivity.getNavController().navigate(R.id.fragment_range_calender,bundle);
        NavHostFragment.findNavController(this).navigate(R.id.fragment_range_calender,bundle);
    }

    @Override
    public void onOperateChanged(long time) {
        LogUtil.logD(TAG,"[onOperateChanged] = "+getTransferDate(time));
        if (editMission!=null && editMission.getRepeat() == REPEAT_DEFINE &&
                editMission.getRepeatStart() != -1L &&
                editMission.getRepeatEnd() != -1L) {
            if (time > editMissionViewModel.getEditMission().getValue().getRepeatStart() ||
                    time > editMissionViewModel.getEditMission().getValue().getRepeatEnd()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("執行日小於重複區間")
                        .setMessage("將會清除已設置的重複區間")
                        .setPositiveButton(R.string.ok +"??", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                latestRepeatStart = -1L;
                                latestRepeatEnd = -1L;
                                repeatStart = -1L;
                                repeatEnd = -1L;

                                fragmentEditMissionBinding.itemOperate.updateUI(time);
                                operateDay = time;
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
            operateDay = time;
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
