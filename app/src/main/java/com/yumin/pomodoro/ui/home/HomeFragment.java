package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.Mission;

import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel mHomeViewModel;
    ArrayAdapter<Mission> mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        final ListView listView = root.findViewById(R.id.home_list_view);

        mHomeViewModel.getList().observe(getViewLifecycleOwner(), missions -> {
            // Update the ui
            mAdapter = new ArrayAdapter<Mission>(getContext(), android.R.layout.simple_list_item_1);
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        });
        return root;
    }
}