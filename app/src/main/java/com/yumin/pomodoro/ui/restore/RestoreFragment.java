package com.yumin.pomodoro.ui.restore;

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

public class RestoreFragment extends Fragment {

    private RestoreViewModel restoreViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restoreViewModel =
                ViewModelProviders.of(this).get(RestoreViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restore, container, false);
        final TextView textView = root.findViewById(R.id.text_restore);
        restoreViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}