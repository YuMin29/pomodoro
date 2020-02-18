package com.yumin.pomodoro.ui.backuo;

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

public class BackupFragment extends Fragment {

    private BackupViewModel backupViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        backupViewModel =
                ViewModelProviders.of(this).get(BackupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_backup, container, false);
        final TextView textView = root.findViewById(R.id.text_backup);
        backupViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}