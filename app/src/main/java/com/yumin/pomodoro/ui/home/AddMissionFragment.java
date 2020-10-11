package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.text.Editable;
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
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
import com.yumin.pomodoro.utils.Event;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
    ViewGroup viewGroup;

    public AddMissionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAddMissionViewModel = new ViewModelProvider(requireActivity()).get(AddMissionViewModel.class);
        mAddMissionEventHandler = new AddMissionEventHandler();
        mFragmentAddMissionBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_mission,container,false);
        viewGroup = container;
        return mFragmentAddMissionBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentAddMissionBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set title
        MainActivity.setToolbarTitle("add mission");
        // set click event
        mFragmentAddMissionBinding.setViewModel(mAddMissionViewModel);
        mFragmentAddMissionBinding.setEventHandler(mAddMissionEventHandler);
        mFragmentAddMissionBinding.setLifecycleOwner(this);
    }
}
