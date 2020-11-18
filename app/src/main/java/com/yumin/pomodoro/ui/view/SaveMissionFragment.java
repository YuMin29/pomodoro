package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.viewmodel.SaveMissionViewModel;

public class SaveMissionFragment extends Fragment {

    private SaveMissionViewModel saveMissionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saveMissionViewModel =
                ViewModelProviders.of(this).get(SaveMissionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_save_mission, container, false);
        final TextView textView = root.findViewById(R.id.text_save_mission);
        saveMissionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}