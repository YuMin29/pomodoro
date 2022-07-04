package com.yumin.pomodoro.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.ui.main.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentLoginBinding;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

public class LoginFragment extends DataBindingFragment implements LoginNavigator{
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private LoginViewModel mLoginViewModel;
    private CallbackManager mFbCallbackManager;
    private FragmentLoginBinding mFragmentLoginBinding;
    private GoogleSignInClient mGoogleSignInClient;
    private AlertDialog mProgressBarDialog;
    private boolean mSyncData = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logD(TAG,"[onCreate]");
        ((MainActivity)getActivity()).fullScreenMode(true);

        FacebookSdk.sdkInitialize(getContext());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
    }

    @Override
    protected void initViewModel() {
        mLoginViewModel = getFragmentScopeViewModel(LoginViewModel.class);
        mLoginViewModel.setLoginNavigator(this);
        mLoginViewModel.init();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLoginBinding = (FragmentLoginBinding) getBinding();
        mFbCallbackManager = CallbackManager.Factory.create();

        // set up FB login action
        mFragmentLoginBinding.fbLoginButton.setFragment(this);
        mFragmentLoginBinding.fbLoginButton.setReadPermissions("email", "public_profile");
        mFragmentLoginBinding.fbLoginButton.registerCallback(mFbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.logD(TAG, "[facebook:onSuccess] " + loginResult);
                mLoginViewModel.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                LogUtil.logD(TAG, "[facebook:onCancel]");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.logD(TAG, "[facebook:onError] " + error);
            }
        });
        observeData();
    }

    private void observeData() {
        mLoginViewModel.getResultMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<LoginViewModel.Result>() {
            @Override
            public void onChanged(LoginViewModel.Result result) {
                if (result.isComplete()) {
                    if (result.getFirebaseMissions().size() > 0 && !mSyncData) {
                        mProgressBarDialog = createProgressBarDialog();
                        mProgressBarDialog.show();
                        mLoginViewModel.syncFirebaseMissionsToRoom();
                        mSyncData = true;
                    } else {
                        navigateTo(R.id.nav_main);
                    }
                }
            }
        });
    }

    private void handleLoginFacebook(){
        mFragmentLoginBinding.fbLoginButton.performClick();
    }

    private void handleGoogleSignIn() {
        LogUtil.logD(TAG, "[handleGoogleSignIn]");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void navigateTo(int id) {
        LogUtil.logD(TAG,"[navigate]");
        NavHostFragment.findNavController(this).navigate(id);
    }

    private void handleLoginWithAccount() {
        String enterEmail = mFragmentLoginBinding.loginEmail.getText().toString();
        String enterPassword = mFragmentLoginBinding.loginPassword.getText().toString();
        if (TextUtils.isEmpty(enterEmail) || TextUtils.isEmpty(enterPassword)) {
            Toast.makeText(getContext(), getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
            return;
        }
        mLoginViewModel.loginWithAccount(enterEmail,enterPassword);
    }

    @Override
    public AlertDialog createProgressBarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progressbar,null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
    }

    private void goToResetPassword() {
        navigateTo(R.id.fragment_reset_password);
    }

    private void goToRegisterAccount() {
        navigateTo(R.id.fragment_register);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            processGoogleSignInResult(task);
        } else {
            mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            LogUtil.logD(TAG, "[processGoogleSignInResult] :" + account.getId());
            mLoginViewModel.handleGoogleSignIn(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            LogUtil.logD(TAG, "[processGoogleSignInResult] exception =" + e.getStatusCode());
        }
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_login, BR.loginViewModel, mLoginViewModel)
                .addBindingParam(BR.loginClickProxy, new LoginClickProxy());
    }

    public void navigateUp(){
        LogUtil.logD(TAG,"[navigateUp]");
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void navigateToHome() {
        if (mProgressBarDialog != null)
            mProgressBarDialog.dismiss();
        navigateTo(R.id.nav_main);
    }

    public class LoginClickProxy {
        public void loginWithAccount() {
            handleLoginWithAccount();
        }

        public void registerAccount() {
            goToRegisterAccount();
        }

        public void forgetAccountPassword() {
            goToResetPassword();
        }

        public void back(){
            navigateUp();
        }

        public void loginWithFacebook(){
            handleLoginFacebook();
        }

        public void loginWithGoogle(){
            handleGoogleSignIn();
        }
    }
}
