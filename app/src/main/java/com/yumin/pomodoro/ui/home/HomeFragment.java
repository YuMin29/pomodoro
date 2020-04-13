package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel mHomeViewModel;
    ArrayAdapter<String> mAdapter;
    MissionAdapter missionAdapter;
    List<String> mMissionNames = new ArrayList<>();
    List<Mission> mMissions = new ArrayList<>();
    boolean mIsLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater,container,false);
        View root = fragmentHomeBinding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentHomeBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
//        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mMissionNames);
        missionAdapter = new MissionAdapter(mMissions,getContext());
        fragmentHomeBinding.homeListView.setAdapter(missionAdapter);
        observeViewModel();
    }

    private void observeViewModel(){
//        mHomeViewModel.getStringList().observe(getViewLifecycleOwner(), missionNames -> {
            // Load data & update the ui
//            mMissionNames.addAll(missionNames);
//            mAdapter.notifyDataSetChanged();
//        });
        mHomeViewModel.getMissionList().observe(getViewLifecycleOwner(), missionList ->{
            mMissions.addAll(missionList);
            missionAdapter.updateData(mMissions);
            missionAdapter.notifyDataSetChanged();
        });

        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            mIsLoading = isLoading;
            fragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });
    }
}