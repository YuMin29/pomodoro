package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.yumin.pomodoro.R;

import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel mHomeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);
        mHomeViewModel = new HomeViewModel();
        final ListView listView = root.findViewById(R.id.home_list_view);

        mHomeViewModel.getList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {

            }
        });
        return root;
    }
}