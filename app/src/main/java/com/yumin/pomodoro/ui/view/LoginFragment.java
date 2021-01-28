package com.yumin.pomodoro.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentLoginBinding;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.LoginViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends DataBindingFragment {
    private static final String TAG = "[LoginFragment]";
    private static final int RC_SIGN_IN = 123;
    LoginViewModel mLoginViewModel;
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    FragmentLoginBinding mFragmentLoginBinding;

    @Override
    protected void initViewModel() {
//        mLoginViewModel = getFragmentScopeViewModel(LoginViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLoginBinding = (FragmentLoginBinding) getBinding();
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        // FB login action
        LoginButton loginButton = mFragmentLoginBinding.fbLoginButton;
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigate(int id){
        NavHostFragment.findNavController(this).navigate(id);
    }

    public void loginAccount(){
        // check email and password
        String email = mFragmentLoginBinding.loginEmail.getText().toString();
        String password = mFragmentLoginBinding.loginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getContext(),getString(R.string.create_account_error),Toast.LENGTH_SHORT).show();
            return;
        }

        mFragmentLoginBinding.login.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
//                        MainActivity.getNavController().navigate(R.id.nav_home);
                        navigate(R.id.nav_home);
                        mFragmentLoginBinding.login.setEnabled(true);
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToResetPassword(){
        navigate(R.id.fragment_reset_password);
    }

    private void goToRegisterAccount(){
        navigate(R.id.fragment_register);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_login, -1, null)
                .addBindingParam(BR.clickProxy, new LoginFragment.ClickProxy());
    }

    public class ClickProxy{
        public void login(){
            loginAccount();
        };

        public void register(){
            // switch to register fragment
            goToRegisterAccount();
        };

        public void forgetPassword(){
            goToResetPassword();
        }
    }
}
