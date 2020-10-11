package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
<<<<<<< HEAD
=======
import androidx.databinding.DataBindingUtil;
>>>>>>> d7e87be... tmp
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
<<<<<<< HEAD
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
=======
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
>>>>>>> d7e87be... tmp
import com.yumin.pomodoro.utils.Event;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
<<<<<<< HEAD
    FragmentAddMissionBinding mFragmentAddMissionBinding;
    AddMissionViewModel mAddMissionViewModel;
=======
    FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
>>>>>>> d7e87be... tmp
    ViewGroup viewGroup;

    public AddMissionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAddMissionViewModel = new ViewModelProvider(requireActivity()).get(AddMissionViewModel.class);
<<<<<<< HEAD
        mFragmentAddMissionBinding = FragmentAddMissionBinding.inflate(inflater);
=======
        mAddMissionEventHandler = new AddMissionEventHandler();
        mFragmentAddMissionBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_mission,container,false);
>>>>>>> d7e87be... tmp
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
<<<<<<< HEAD
        mFragmentAddMissionBinding.missionTitle.addTextChangedListener(mAddMissionViewModel.onTextListener);
        mFragmentAddMissionBinding.missionTitle.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionTime.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionBreak.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionDay.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionGoal.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionNotification.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionRepeat.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionSound.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionSoundLevel.setOnClickListener(mAddMissionViewModel.onViewClick);
        mFragmentAddMissionBinding.missionTheme.setOnClickListener(mAddMissionViewModel.onViewClick);

        mAddMissionViewModel.getClickEvent().observe(getViewLifecycleOwner(),
                new Observer<Event<Integer>>() {
                    @Override
                    public void onChanged(Event<Integer> integerEvent) {
                        if (integerEvent.getContentIfNotHandled() != null){
                            switch (integerEvent.peekContent().intValue()) {
                                case R.id.mission_title:
                                    break;
                                case R.id.mission_time:
                                    break;
                                case R.id.mission_break:
                                    break;
                                case R.id.mission_day:
                                    break;
                                case R.id.mission_goal:
                                    break;
                                case R.id.mission_notification:
                                    break;
                                case R.id.mission_sound:
                                    break;
                                case R.id.mission_sound_level:
                                    break;
                                case R.id.mission_theme:
                                    break;
                            }
                        }
                    }
                });
=======
        mFragmentAddMissionBinding.setViewModel(mAddMissionViewModel);
        mFragmentAddMissionBinding.setEventHandler(mAddMissionEventHandler);
        mFragmentAddMissionBinding.setLifecycleOwner(this);
>>>>>>> d7e87be... tmp
    }
}
