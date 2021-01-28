package com.yumin.pomodoro.ui.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentResetPasswordBinding;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

public class ResetPasswordFragment extends DataBindingFragment {
    FirebaseAuth mAuth;
    FragmentResetPasswordBinding mFragmentResetPasswordBinding;

    @Override
    protected void initViewModel() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentResetPasswordBinding = (FragmentResetPasswordBinding) getBinding();
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_reset_password, -1, null)
                .addBindingParam(BR.clickProxy, new ClickProxy());
    }

    private void sendResetPasswordLink() {
        String email = mFragmentResetPasswordBinding.resetEmail.getText().toString();

        if (TextUtils.isEmpty(email))
            return;

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // change reset button color
                    mFragmentResetPasswordBinding.resetPassword
                            .setBackgroundColor(Color.parseColor("#00FF00")); // color green
                }
            }
        });
    }

    private void back(){
        // TODO: 1/28/21 add navigate up here
        NavHostFragment.findNavController(this).navigateUp();
    }

    public class ClickProxy{
        public void resetPassword(){
            sendResetPasswordLink();
        };

        public void resetBack(){
            back();
        };
    }
}
