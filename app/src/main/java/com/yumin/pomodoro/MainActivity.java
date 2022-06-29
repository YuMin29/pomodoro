package com.yumin.pomodoro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.adapter.DrawerListAdapter;
import com.yumin.pomodoro.adapter.NavItem;
import com.yumin.pomodoro.base.MissionManager;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.PrefUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    public static final String NAV_ITEM_SHARED_PREFERENCE = "nav_item";
    public static final String KEY_BACKUP_TIME = "_backup";
    public static final String KEY_RESTORE_TIME = "_restore";
    private static final String TAG = MainActivity.class.getSimpleName();
    static TextView mToolbarTitle = null;
    private static NavController mNavController;
    private final int POSITION_CALENDER = 0;
    private final int POSITION_SETTINGS = 1;
    private final int POSITION_BACKUP = 2;
    private final int POSITION_RESTORE = 3;
    private final int POSITION_EXPIRED = 4;
    public ImageView mUserSignIn;
    public ImageView mSettings;
    FloatingActionButton mFab;
    NavigationView mNavigationView;
    FirebaseAuth.AuthStateListener authStateListener;
    DrawerLayout mDrawerLayout;
    FirebaseUser mCurrentFirebaseUser = null;
    Context mContext;
    RefreshHomeFragment mRefreshHomeFragment;
    LinearLayout mNavHeaderMain = null;
    ListView mDrawerList;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    DrawerListAdapter mDrawerListAdapter;
    BottomNavigationView mBottomNavigationView;
    BottomAppBar mBottomAppBar;
    View mTopView;
    private FirebaseAuth mFirebaseAuth;
    private View mFabExplode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        extendLayoutToStatusBar();
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mTopView = findViewById(R.id.top_view);
        mTopView.setPadding(0, getStatusBarHeight(), 0, 0);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.getMenu().getItem(1).setEnabled(false);

        mBottomAppBar = findViewById(R.id.bottomAppBar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_calender, R.id.nav_save_mission, R.id.fragment_timer)
                .build();

        mFabExplode = findViewById(R.id.fab_explode);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFabExplodeBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                startFabExplodeAnimation(null,null);
            }
        });

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        mNavController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        mUserSignIn = findViewById(R.id.user_sign_in);
        mUserSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFirebaseUser == null) {
                    // check network state first
                    if (!isNetworkConnected()) {
                        // request user need to connect network
                        Toast.makeText(getApplicationContext(), R.string.network_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // use animation
                    if (mNavController.getCurrentDestination().getId() == R.id.nav_main)
                        mNavController.navigate(R.id.main_to_login);
                    else if (mNavController.getCurrentDestination().getId() == R.id.nav_settings)
                        mNavController.navigate(R.id.setting_to_login);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.logout)
                            .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            LogUtil.logE(TAG, "sign out [onComplete]");
                                            mRefreshHomeFragment.onRefresh();
//                                            setBackupTime(null);
//                                            setRestoreTime(null);
                                        }
                                    });
                                }
                            }).setPositiveButton(getString(R.string.cancel), null);
                    builder.show();
                }
            }
        });


        mSettings = findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch to settings fragment
                mNavController.navigate(R.id.main_to_settings);
            }
        });


        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateNavHeader(user);
            if (user != null) {
//                setBackupTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                setRestoreTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
            mCurrentFirebaseUser = user;
        };

        // init
        PrefUtils.clearTimerServiceStatus(this);
    }

    public void startFabExplodeAnimation(Bundle bundle, UserMission userMission) {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.circle_animation);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // navigate to timer with mission id
                MissionManager.getInstance().setOperateId(bundle != null ? userMission : null);
                mNavController.navigate(R.id.fragment_timer, bundle != null ? bundle : null);
                mFabExplode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mFabExplode.setVisibility(View.VISIBLE);
        mFabExplode.startAnimation(animation);
        mFab.setVisibility(View.INVISIBLE);
    }

    public void setFabExplodeBackgroundColor(int color){
        Drawable background = mFabExplode.getBackground();
        background.setTint(color);
    }

    private void extendLayoutToStatusBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isLoginFirebase() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, R.string.require_login, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setRefreshHomeFragment(RefreshHomeFragment onRefreshHomeFragment) {
        mRefreshHomeFragment = onRefreshHomeFragment;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setBackupTime(String uid) {
        updateNaSubtitle(POSITION_BACKUP, uid == null ? null : uid + KEY_BACKUP_TIME);
    }

    public void setRestoreTime(String uid) {
        updateNaSubtitle(POSITION_RESTORE, uid == null ? null : uid + KEY_RESTORE_TIME);
    }

    private void updateNaSubtitle(int navItem, String key) {
        String backupTime = "";
        if (key != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(NAV_ITEM_SHARED_PREFERENCE, MODE_PRIVATE);
            backupTime = sharedPreferences.getString(key, "");
        }
        mNavItems.get(navItem).setSubtitle(backupTime);
        mDrawerListAdapter.notifyDataSetInvalidated();
    }

    private void updateNavHeader(FirebaseUser user) {
        TextView userName = findViewById(R.id.user_name);
//        TextView userMail = mNavHeaderMain.findViewById(R.id.nav_header_user_mail);
        userName.setText(user == null ? getApplicationContext().getString(R.string.nav_header_title_no_user) : "Hi," + user.getDisplayName());
//        userMail.setText(user == null ? "" : user.getEmail());
    }

    public void fullScreenMode(boolean enable) {
        mTopView.setVisibility(enable ? View.GONE : View.VISIBLE);
        mUserSignIn.setVisibility(enable ? View.GONE : View.VISIBLE);
        mBottomNavigationView.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        mBottomAppBar.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);

        View navHostFragment = findViewById(R.id.nav_host_fragment_activity_main);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navHostFragment.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, enable ? 0 : getActionBarHeight());
        navHostFragment.setLayoutParams(layoutParams);

        mFab.setVisibility(enable? View.INVISIBLE : View.VISIBLE);
    }

    public void fabVisible(int visibility){
        mFab.setVisibility(visibility);
    }

    public int getActionBarHeight() {
        final TypedArray ta = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = (int) ta.getDimension(0, 0);
        return actionBarHeight;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(authStateListener);
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
        return NavigationUI.navigateUp(navController, mDrawerLayout)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mNavController.getCurrentDestination().getId() == R.id.nav_home)
            finish();
        else
            super.onBackPressed();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
//        mToolbarTitle.setText(navDestination.getLabel().toString());
//        LogUtil.logD(TAG, "[onDestinationChanged] label = " + navDestination.getLabel().toString());
//        String navHomeLabel = getResources().getString(R.string.menu_main);
//        if (navHomeLabel.equals(navDestination.getLabel().toString())) {
//            mFab.setVisibility(View.VISIBLE);
//        } else {
//            mFab.setVisibility(View.INVISIBLE);
//        }
    }

    public interface RefreshHomeFragment {
        void onRefresh();
    }
}
