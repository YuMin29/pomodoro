package com.yumin.pomodoro.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentResetPasswordBinding;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

public class ResetPasswordFragment extends DataBindingFragment {
    FirebaseAuth mAuth;
    FragmentResetPasswordBinding mFragmentResetPasswordBinding;

    @Override
    protected void initViewModel() {}

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
                .addBindingParam(BR.resetPasswordClickProxy, new ClickProxy());
    }

    private void sendResetPasswordLink() {
        String email = mFragmentResetPasswordBinding.resetEmail.getText().toString();

        if (TextUtils.isEmpty(email))
            return;

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFragmentResetPasswordBinding.resetPassword
                            .setBackgroundColor(Color.parseColor("#00FF00")); // color green
                    mFragmentResetPasswordBinding.resetPassword.setText(R.string.reset_email_success);
                    mFragmentResetPasswordBinding.resetPassword.setClickable(false);
                }
            }
        });
    }

    private void back(){
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
