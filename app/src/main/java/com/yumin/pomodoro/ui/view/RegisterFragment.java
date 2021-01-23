package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentRegisterBinding;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

public class RegisterFragment extends DataBindingFragment {
    private static final String TAG = "[RegisterFragment]";
    FirebaseAuth mAuth;
    FragmentRegisterBinding mFragmentRegisterBinding;

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
        mFragmentRegisterBinding = (FragmentRegisterBinding) getBinding();
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_register,-1,null)
                .addBindingParam(BR.clickProxy, new RegisterFragment.ClickProxy());
    }

    private void createAccount(){
        mFragmentRegisterBinding.progressBar.setVisibility(View.VISIBLE);

        String email = mFragmentRegisterBinding.registerEmail.getText().toString();
        String password = mFragmentRegisterBinding.registerPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(),getString(R.string.create_account_error),Toast.LENGTH_SHORT).show();
            return;
        }
        mFragmentRegisterBinding.createAccount.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                mFragmentRegisterBinding.progressBar.setVisibility(View.GONE);
                                mFragmentRegisterBinding.createAccount.setEnabled(true);
                                MainActivity.getNavController().navigate(R.id.nav_home);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mFragmentRegisterBinding.progressBar.setVisibility(View.GONE);
                            mFragmentRegisterBinding.createAccount.setEnabled(true);
                        }
                    }
                });
    }

    private void verifiedAccount(){

    }

    public class ClickProxy{
        public void createAccount(){
            RegisterFragment.this.createAccount();
        };
    }
}
