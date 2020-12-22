package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import com.yumin.pomodoro.utils.LogUtil;

public class EditMissionFragment extends Fragment implements ItemListView.OnCalenderListener{
    private static final String TAG = "[EditMissionFragment]";
    EditMissionViewModel editMissionViewModel;
    RangeCalenderViewModel rangeCalenderViewModel;
    FragmentEditMissionBinding fragmentEditMissionBinding;
    int editId = 0;
    public EditMissionFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logD(TAG, "[onCreate]");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        fragmentEditMissionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_mission,container,false);
        fragmentEditMissionBinding.setLifecycleOwner(this);
        fragmentEditMissionBinding.itemRepeat.setOnCalenderListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            editId = bundle.getInt("editId");
        }
        LogUtil.logD(TAG,"[onCreateView] editId = "+editId);
        initViewModel(editId);
        initObserver();
        fragmentEditMissionBinding.setClickProxy(new EditMissionFragment.ClickProxy());
        fragmentEditMissionBinding.setViewmodel(editMissionViewModel);
        return fragmentEditMissionBinding.getRoot();
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

        rangeCalenderViewModel.getRepeatStart().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                LogUtil.logD(TAG,"[initObserve] getRepeatStart");
            }
        });

        rangeCalenderViewModel.getClickCommit().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    LogUtil.logD(TAG,"[initObserve] getClickCommit = "+aBoolean);
                    editMissionViewModel.updateRepeatStart(rangeCalenderViewModel.getRepeatStart().getValue());
                    editMissionViewModel.updateRepeatEnd(rangeCalenderViewModel.getRepeatEnd().getValue());
                }
            }
        });
    }

    private void initViewModel(int editId) {
        LogUtil.logD(TAG,"[initViewModel] edit id = "+editId);
        editMissionViewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(getActivity().getApplication()),getContext()),editId)).get(EditMissionViewModel.class);
        rangeCalenderViewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(getActivity().getApplication()),getContext()),editId)).get(RangeCalenderViewModel.class);
    }

    @Override
    public void onOpened() {
        Bundle bundle = new Bundle();
        bundle.putInt("missionId",editId);
        MainActivity.getNavController().navigate(R.id.fragment_range_calender,bundle);
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
