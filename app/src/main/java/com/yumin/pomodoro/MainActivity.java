
package com.yumin.pomodoro;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.MissionManager;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener{
    private static final String TAG = "[MainActivity]";
    private AppBarConfiguration mAppBarConfiguration;
    static TextView mToolbarTitle = null;
    private static NavController mNavController;
    FloatingActionButton mFab;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    FirebaseAuth.AuthStateListener authStateListener;
    DrawerLayout mDrawerLayout;
    FirebaseUser mCurrentFirebaseUser = null;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // set status bar color as tool bar color
        setStatusBarGradient(this);
        setStatusBar(getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_main);
        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTitle = findViewById(R.id.tool_bar_title);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start a quick mission
                MissionManager.getInstance().setOperateId(-1);
                MainActivity.getNavController().navigate(R.id.fragment_timer);
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        View navigationHeaderView = navigationView.getHeaderView(0);
        navigationHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click for login , switch to login fragment
                // current user exist , switch to logout fragment
                if (mCurrentFirebaseUser == null) {
                    MainActivity.getNavController().navigate(R.id.fragment_login);
                } else {
                    // Showing a dialog to confirm logout or not
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("登出帳號？")
                            .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth.signOut();
                                }
                            }).setPositiveButton(getString(R.string.cancel), null);
                    builder.show();
                }
                closeDrawerLayout();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
                R.id.nav_backup, R.id.nav_restore, R.id.nav_total,
                R.id.nav_calender, R.id.nav_save_mission, R.id.nav_settings)
                .setDrawerLayout(mDrawerLayout)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mNavController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);
        isStoragePermissionGranted();

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Check if user is signed in (non-null) and update UI accordingly.
                if (user != null) {
                    updateNavHeader(user);
                    mCurrentFirebaseUser = user;
                }
            }
        };
    }

    private void closeDrawerLayout(){
        mDrawerLayout.closeDrawers();
    }

    private void updateNavHeader(FirebaseUser user){
        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView userName = navigationHeaderView.findViewById(R.id.nav_header_user);
        TextView userMail = navigationHeaderView.findViewById(R.id.nav_header_user_mail);
        userName.setText(user.getDisplayName());
        userMail.setText(user.getEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public static NavController getNavController(){
        return mNavController;
    }

    public static void commitWhenLifecycleStarted(Lifecycle lifecycle, int destination, Bundle bundle) {
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_START) {
                    LogUtil.logD(TAG,"[commitWhenLifeCycleStarted]");
                    lifecycle.removeObserver(this);
                    mNavController.navigate(destination,bundle);
                }
            }
        });
    }

    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.background);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
        }
    }

    protected void setStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        mToolbarTitle.setText(navDestination.getLabel().toString());
        LogUtil.logD(TAG,"[onDestinationChanged] label = "+navDestination.getLabel().toString());
        String navHomeLabel = getResources().getString(R.string.menu_home);
        if (navHomeLabel.equals(navDestination.getLabel().toString()))
            mFab.setVisibility(View.VISIBLE);
        else
            mFab.setVisibility(View.GONE);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                LogUtil.logV(TAG, "Permission is granted");
                return true;
            } else {
                LogUtil.logV(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            LogUtil.logV(TAG, "Permission is granted");
            return true;
        }
    }
}
