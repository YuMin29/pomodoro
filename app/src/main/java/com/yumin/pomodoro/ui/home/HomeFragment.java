package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mFragmentHomeBinding;
    private HomeViewModel mHomeViewModel;
    private MissionAdapter mMissionAdapter;
    private List<Mission> mMissions = new ArrayList<>();
    boolean mIsLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater,container,false);
        return mFragmentHomeBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentHomeBinding = null;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mMissionAdapter = new MissionAdapter(mMissions,getContext());
        mFragmentHomeBinding.homeListView.setAdapter(mMissionAdapter);
        observeViewModel();
    }


    private void observeViewModel(){
        mHomeViewModel.getMissionList().observe(getViewLifecycleOwner(), missionList ->{
            if (!mMissions.containsAll(missionList)) {
                mMissions.clear();
                mMissions.addAll(missionList);
                mMissionAdapter.updateData(mMissions);
                mMissionAdapter.notifyDataSetChanged();
            }
        });

        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            mIsLoading = isLoading;
            mFragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });
    }
}