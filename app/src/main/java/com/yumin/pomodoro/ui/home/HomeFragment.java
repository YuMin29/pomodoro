package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.Mission;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel mHomeViewModel;
    ArrayAdapter<String> mAdapter;
    List<String> missionNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        final ListView listView = view.findViewById(R.id.home_list_view);
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, missionNames);
        listView.setAdapter(mAdapter);
        observeViewModel();
    }

    private void observeViewModel(){
        mHomeViewModel.getStringList().observe(getViewLifecycleOwner(), missionItems -> {
            // Load data & update the ui
            missionNames.addAll(missionItems);
            mAdapter.notifyDataSetChanged();
        });
    }
}