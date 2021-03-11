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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.User;
import com.yumin.pomodoro.databinding.FragmentLoginBinding;
import com.yumin.pomodoro.ui.main.viewmodel.LoginViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoginFragment extends DataBindingFragment {
    private static final String TAG = "[LoginFragment]";
    private static final int RC_SIGN_IN = 1001;
    LoginViewModel mLoginViewModel;
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    FragmentLoginBinding mFragmentLoginBinding;
    GoogleSignInClient mGoogleSignInClient;

    // TODO: 2021/3/11 It's need to refactor especially move some function to view model
    @Override
    protected void initViewModel() {
        mLoginViewModel = getFragmentScopeViewModel(LoginViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logD(TAG, "[onCreate]");
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getContext());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.logD(TAG, "[onViewCreated]");
        mFragmentLoginBinding = (FragmentLoginBinding) getBinding();
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        // FB login action
        LoginButton loginButton = mFragmentLoginBinding.fbLoginButton;
        loginButton.setFragment(this);
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

        // Google login action
        SignInButton googleSignInButton = mFragmentLoginBinding.googleSignInButton;
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handelGoogleSignIn();
            }
        });
        initObserver();
    }

    private void initObserver() {
        mLoginViewModel.getRoomMissions().observe(getViewLifecycleOwner(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logE(TAG, "[initObserver][getRoomMissions] isEmpty = " +
                        userMissions.isEmpty());
                mLoginViewModel.setIsRoomMissionsExist(!userMissions.isEmpty());
            }
        });
    }

    private void handelGoogleSignIn() {
        LogUtil.logD(TAG, "[handelGoogleSignIn]");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        LogUtil.logD(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener());
    }

    private void navigate(int id) {
        NavHostFragment.findNavController(this).navigate(id);
    }

    public void loginAccount() {
        // check email and password
        String email = mFragmentLoginBinding.loginEmail.getText().toString();
        String password = mFragmentLoginBinding.loginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
            return;
        }

        mFragmentLoginBinding.login.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getOnCompleteListener());
    }

    @NotNull
    private OnCompleteListener<AuthResult> getOnCompleteListener() {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
//                        MainActivity.getNavController().navigate(R.id.nav_home);
                        navigate(R.id.nav_home);
                        mFragmentLoginBinding.login.setEnabled(true);
                        addUserToFirebase(user);
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
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

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            LogUtil.logD(TAG, "handleGoogleSignInResult:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener());
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
                LogUtil.logE(TAG, "[addUserToFirebase]  222 ");
            }
        });
    }

    private void syncLocalRoomToFirebase() {
        // 1. check where if local Room has data
        // 2. if have, sync data
        // 3. then clear local room
    }


    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_login, -1, null)
                .addBindingParam(BR.clickProxy, new LoginFragment.ClickProxy());
    }

    public class ClickProxy {
        public void login() {
            loginAccount();
        }

        ;

        public void register() {
            // switch to register fragment
            goToRegisterAccount();
        }

        ;

        public void forgetPassword() {
            goToResetPassword();
        }
    }
}
