package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.databinding.FragmentEditMissionBinding;
import com.yumin.pomodoro.ui.base.EditViewModelFactory;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

public class EditMissionFragment extends DataBindingFragment implements ItemListView.OnCalenderListener{
    private static final String TAG = "[EditMissionFragment]";
    EditMissionViewModel editMissionViewModel;
    SharedViewModel sharedViewModel;
    FragmentEditMissionBinding fragmentEditMissionBinding;
//    RangeCalenderViewModel rangeCalenderViewModel;
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

//        rangeCalenderViewModel.getRepeatStart().observe(getViewLifecycleOwner(), new Observer<Long>() {
//            @Override
//            public void onChanged(Long aLong) {
//                LogUtil.logD(TAG,"[initObserve] getRepeatStart");
//            }
//        });
//
//        rangeCalenderViewModel.getClickCommit().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if (aBoolean) {
//                    LogUtil.logD(TAG,"[initObserve] getClickCommit = "+aBoolean);
//                    editMissionViewModel.updateRepeatStart(rangeCalenderViewModel.getRepeatStart().getValue());
//                    editMissionViewModel.updateRepeatEnd(rangeCalenderViewModel.getRepeatEnd().getValue());
//                }
//            }
//        });
    }


    @Override
    public void onOpened() {
        MissionManager.getInstance().setRangeCalenderId(MissionManager.getInstance().getEditId());
        MainActivity.getNavController().navigate(R.id.fragment_range_calender);
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
