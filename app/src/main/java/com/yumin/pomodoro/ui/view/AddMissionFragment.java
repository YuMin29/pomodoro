package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.clickhandler.AddMissionClickHandler;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    AddMissionClickHandler mAddMissionClickHandler;
    FragmentAddMissionBinding fragmentAddMissionBinding;
    List<AdjustMissionItem> adjustMissionItems = new ArrayList<>();

    public AddMissionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        fragmentAddMissionBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_mission,container,false);
        initViewModel();
        initClickHandler();
        initUI();
        initObserver();
        fragmentAddMissionBinding.setClick(mAddMissionClickHandler);
        fragmentAddMissionBinding.setViewmodel(mAddMissionViewModel);
        return fragmentAddMissionBinding.getRoot();
    }

    private void initObserver() {
    }

    private void initUI() {
        // set title
        MainActivity.setToolbarTitle("Add mission");
//        fragmentAddMissionBinding.missionTitle.addTextChangedListener(mAddMissionClickHandler.getTextWatcher());
    }

    private void initViewModel() {
        mAddMissionViewModel =  new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(),getContext()))).get(AddMissionViewModel.class);

    }

    private void initClickHandler(){
        mAddMissionClickHandler = new AddMissionClickHandler(getContext(),mAddMissionViewModel);
    }
}
