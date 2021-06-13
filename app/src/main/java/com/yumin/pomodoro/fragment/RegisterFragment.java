package com.yumin.pomodoro.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentRegisterBinding;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.utils.LogUtil;

public class RegisterFragment extends DataBindingFragment {
    private static final String TAG = RegisterFragment.class.getSimpleName();
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
        return new DataBindingConfig(R.layout.fragment_register, -1, null)
                .addBindingParam(BR.registerClickProxy, new RegisterFragment.ClickProxy());
    }

    private void navigate(int id) {
        NavHostFragment.findNavController(this).navigate(id);
    }

    private void createAccount() {
        String email = mFragmentRegisterBinding.registerEmail.getText().toString();
        String password = mFragmentRegisterBinding.registerPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password length smaller than 6", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog alertDialog = createProgressBarDialog();
        alertDialog.show();
        mFragmentRegisterBinding.createAccount.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            LogUtil.logD(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                navigate(R.id.nav_home);
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                LogUtil.logD(TAG, "[createUserWithEmail] exception" + e.getStackTrace());
                                if (e instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(getContext(), R.string.email_malformed_exception, Toast.LENGTH_SHORT).show();
                                if (e instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(getContext(), R.string.email_exist_exception, Toast.LENGTH_SHORT).show();
                            }
                        }
                        mFragmentRegisterBinding.createAccount.setEnabled(true);
                    }
                });
    }

    private AlertDialog createProgressBarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progressbar, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    private void back() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    public class ClickProxy {
        public void createAccount() {
            RegisterFragment.this.createAccount();
        }

        public void registerBack() {
            back();
        }
    }
}
