package com.yumin.pomodoro.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.repository.firebase.User;
import com.yumin.pomodoro.databinding.FragmentLoginBinding;
import com.yumin.pomodoro.viewmodel.LoginViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;

import org.jetbrains.annotations.NotNull;


// TODO: [10/24] 登入帳密可以在fragment先檢查是否為空,但後續驗證帳號有效與否 應該交給view model 去call repository api處理
//               再把結果透過view model回傳到fragment
public class LoginFragment extends DataBindingFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    LoginViewModel mLoginViewModel;
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    FragmentLoginBinding mFragmentLoginBinding;
    GoogleSignInClient mGoogleSignInClient;
    AlertDialog mSyncProgressBar;
    private boolean mSyncData = false;

    @Override
    protected void initViewModel() {
        mLoginViewModel = getFragmentScopeViewModel(LoginViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getContext());
        // TODO: [10/24] 應該搬到VIEW MODEL?
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLoginBinding = (FragmentLoginBinding) getBinding();
        mCallbackManager = CallbackManager.Factory.create();

        // TODO: [10/24] 應該搬到VIEW MODEL?
        // FB login action
        LoginButton fbLoginButton = mFragmentLoginBinding.fbLoginButton;
        fbLoginButton.setFragment(this);
        fbLoginButton.setReadPermissions("email", "public_profile");
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.logD(TAG, "[facebook:onSuccess] " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
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

        MaterialButton fbCustom = mFragmentLoginBinding.fbCustom;
        fbCustom.setOnClickListener(v -> {
            fbLoginButton.performClick();
        });

        MaterialButton googleSignInButton = mFragmentLoginBinding.googleSignInButton;
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGoogleSignIn();
            }
        });
        initObserver();
    }

    private void initObserver() {
        mLoginViewModel.getResultMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<LoginViewModel.Result>() {
            @Override
            public void onChanged(LoginViewModel.Result result) {
                if (result.isComplete()) {
                    if (result.getFirebaseMissions().size() > 0 && !mSyncData) {
                        mSyncProgressBar = createProgressBarDialog();
                        mSyncProgressBar.show();
                        mLoginViewModel.syncFirebaseMissionsToRoom();
                        mSyncData = true;
                    } else {
                        navigate(R.id.nav_home);
                    }
                }
            }
        });

        mLoginViewModel.getNavigateToHome().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (mSyncProgressBar != null)
                        mSyncProgressBar.dismiss();
                    navigate(R.id.nav_home);
                }

            }
        });
    }

    private void handleGoogleSignIn() {
        LogUtil.logD(TAG, "[handleGoogleSignIn]");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AlertDialog progressBarDialog = createProgressBarDialog();
        progressBarDialog.show();
        mFragmentLoginBinding.login.setEnabled(false);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener(progressBarDialog));
    }

    private void navigate(int id) {
        NavHostFragment.findNavController(this).navigate(id);
    }

    // TODO: [10/24] this part should move to view model
    public void loginAccount() {
        String email = mFragmentLoginBinding.loginEmail.getText().toString();
        String password = mFragmentLoginBinding.loginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: [10/24] 應該搬到VIEW MODEL?
        AlertDialog progressBarDialog = createProgressBarDialog();
        progressBarDialog.show();
        mFragmentLoginBinding.login.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        progressBarDialog.dismiss();
                        if (task.isSuccessful()) {
                            singInSuccess();
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                LogUtil.logD(TAG, "[signInWithEmailAndPassword] exception : " + e.getStackTrace());
                                if (e instanceof FirebaseAuthInvalidUserException)
                                    Toast.makeText(getActivity(), R.string.sign_in_email_exception, Toast.LENGTH_SHORT).show();
                                if (e instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(getActivity(), R.string.sign_in_password_exception, Toast.LENGTH_SHORT).show();
                            }
                        }
                        mFragmentLoginBinding.login.setEnabled(true);
                    }
                });
    }

    private AlertDialog createProgressBarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progressbar,null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    private void singInSuccess() {
        LogUtil.logD(TAG, "[singInSuccess]");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            addUserToFirebase(user);
            mLoginViewModel.setFirebaseUserExist(true);
        }
    }

    @NotNull
    private OnCompleteListener<AuthResult> getOnCompleteListener(AlertDialog progressBarDialog) {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarDialog.dismiss();

                if (task.isSuccessful()) {
                    singInSuccess();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        LogUtil.logD(TAG, "[signInWithCredential] exception = " + e.getStackTrace());
                        if (e instanceof FirebaseAuthInvalidUserException)
                            Toast.makeText(getActivity(),R.string.invalid_user_exception,Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                            Toast.makeText(getActivity(),R.string.invalid_credential_exception,Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(getActivity(),R.string.user_collision_exception,Toast.LENGTH_SHORT).show();
                    }
                }
                mFragmentLoginBinding.login.setEnabled(true);
            }
        };
    }

    private void goToResetPassword() {
        navigate(R.id.fragment_reset_password);
    }

    private void goToRegisterAccount() {
        navigate(R.id.fragment_register);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            LogUtil.logD(TAG, "[handleGoogleSignInResult] :" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            LogUtil.logD(TAG, "[signInResult:failed] =" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        AlertDialog progressBarDialog = createProgressBarDialog();
        progressBarDialog.show();
        mFragmentLoginBinding.login.setEnabled(false);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener(progressBarDialog));
    }

    private void addUserToFirebase(FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        LogUtil.logE(TAG, "[addUserToFirebase] getUid = " + firebaseUser.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    LogUtil.logE(TAG, "[addUserToFirebase] getUid = " + firebaseUser.getUid() + " doesn't exist!");
                    LogUtil.logE(TAG, "[addUserToFirebase] set value name = " + user.getUserName() + " ,mail = " + user.getUserMail());
                    // The child doesn't exist
                    databaseReference.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_login, -1, null)
                .addBindingParam(BR.loginClickProxy, new LoginFragment.ClickProxy());
    }

    public class ClickProxy {
        public void login() {
            loginAccount();
        }
        public void register() {
            goToRegisterAccount();
        }
        public void forgetPassword() {
            goToResetPassword();
        }
    }
}
