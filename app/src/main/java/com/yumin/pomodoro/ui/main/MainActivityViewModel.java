package com.yumin.pomodoro.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.MainActivityNavigator;
import com.yumin.pomodoro.utils.LogUtil;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "[MainActivityViewModel]";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private MainActivityNavigator mMainActivityNavigator;
    private MutableLiveData<String> userName = new MutableLiveData<>();

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setUp(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                userName.setValue(getApplication().getString(R.string.nav_header_title_no_user));
            } else {
                userName.setValue("Hi," + firebaseUser.getDisplayName());
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void setNavigator(MainActivityNavigator mainActivityNavigator){
        this.mMainActivityNavigator = mainActivityNavigator;
    }

    public void release(){
        if (mAuthStateListener != null)
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void signOut(){
        AuthUI.getInstance().signOut(getApplication()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                LogUtil.logE(TAG, "sign out [onComplete]");
                mMainActivityNavigator.refresh();
                userName.setValue(getApplication().getString(R.string.nav_header_title_no_user));
            }
        });
    }

    public void signIn(){
        if (mFirebaseAuth.getCurrentUser() == null) {
            // check network state first
            if (!mMainActivityNavigator.isNetworkConnected()) {
                // request user need to connect network
                mMainActivityNavigator.showToast(getApplication().getString(R.string.network_warning));
                return;
            }
            // use animation
            mMainActivityNavigator.navigateToLogin();
        } else {
            mMainActivityNavigator.showLogoutDialog();
        }
    }
}
