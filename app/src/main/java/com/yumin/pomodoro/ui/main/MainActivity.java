package com.yumin.pomodoro.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingActivity;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.MissionManager;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.ActivityMainBinding;
import com.yumin.pomodoro.ui.blog.BlogActivity;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.PrefUtils;

public class MainActivity extends DataBindingActivity implements NavController.OnDestinationChangedListener, MainActivityNavigator {
    public static final String NAV_ITEM_SHARED_PREFERENCE = "nav_item";
    public static final String KEY_BACKUP_TIME = "_backup";
    public static final String KEY_RESTORE_TIME = "_restore";
    private static final String TAG = MainActivity.class.getSimpleName();
    private NavController mNavController;
    RefreshHomeFragment mRefreshHomeFragment;
    private MainActivityViewModel mMainActivityViewModel;
    ActivityMainBinding mActivityMainBinding;

    @Override
    protected void initViewModel() {
        mMainActivityViewModel = getActivityScopeViewModel(MainActivityViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.activity_main, BR.viewModel, mMainActivityViewModel)
                .addBindingParam(BR.clickListener, new ClickListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = (ActivityMainBinding) getBinding();

        setStatusBarTransParent();
        this.getSupportActionBar().hide();
        mActivityMainBinding.topView.setPadding(0, getStatusBarHeight(), 0, 0);
        mActivityMainBinding.bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        mActivityMainBinding.bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // empty block
            }
        });

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        mNavController.addOnDestinationChangedListener(this);
        NavigationUI.setupWithNavController(mActivityMainBinding.bottomNavigationView, mNavController);

        // init
        PrefUtils.clearTimerServiceStatus(this);

        mMainActivityViewModel.setUp();
        mMainActivityViewModel.setNavigator(this);
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
                mActivityMainBinding.fabExplode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mActivityMainBinding.fabExplode.setVisibility(View.VISIBLE);
        mActivityMainBinding.fabExplode.startAnimation(animation);
        mActivityMainBinding.fab.setVisibility(View.INVISIBLE);
    }

    public void setFabExplodeBackgroundColor(int color){
         mActivityMainBinding.fabExplode.getBackground().setTint(color);
    }

    private void setStatusBarTransParent() {
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

    public void setRefreshHomeFragment(RefreshHomeFragment onRefreshHomeFragment) {
        mRefreshHomeFragment = onRefreshHomeFragment;
    }

    public boolean isNetworkConnected() {
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

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToLogin() {
        mNavController.navigate(R.id.action_global_loginFragment);
    }

    @Override
    public void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainActivityViewModel.signOut();
                    }
                }).setPositiveButton(getString(R.string.cancel), null);
        builder.show();
    }

    @Override
    public void refresh() {
        mRefreshHomeFragment.onRefresh();
    }

    public void fullScreenMode(boolean enable) {
        LogUtil.logD(TAG,"[fullScreenMode] enable = "+enable);
        mActivityMainBinding.topView.setVisibility(enable ? View.GONE : View.VISIBLE);
        mActivityMainBinding.userSignIn.setVisibility(enable ? View.GONE : View.VISIBLE);
        mActivityMainBinding.bottomNavigationView.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        mActivityMainBinding.bottomAppBar.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        mActivityMainBinding.articles.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);

        View navHostFragment = findViewById(R.id.nav_host_fragment_activity_main);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navHostFragment.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, enable ? 0 : getActionBarHeight());
        navHostFragment.setLayoutParams(layoutParams);

        mActivityMainBinding.fab.setVisibility(enable? View.INVISIBLE : View.VISIBLE);
        // add fab animation
        if (!enable) {
            scaleViewFromCenter(mActivityMainBinding.fab);
        }
    }

    public void scaleViewFromCenter(View view) {
        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    public void setFabVisibility(int visibility){
        mActivityMainBinding.fab.setVisibility(visibility);
    }

    public int getActionBarHeight() {
        final TypedArray ta = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = (int) ta.getDimension(0, 0);
        return actionBarHeight;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainActivityViewModel.release();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition( R.anim.slide_in_top, R.anim.slide_from_top );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mNavController.getCurrentDestination().getId() == R.id.nav_main)
            finish();
        else
            super.onBackPressed();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        if (navDestination.getId() == R.id.add_mission_fragment ||
            navDestination.getId() == R.id.edit_mission_fragment) {
            mActivityMainBinding.userSignIn.setVisibility(View.INVISIBLE);
        }
    }

    public interface RefreshHomeFragment {
        void onRefresh();
    }

    public class ClickListener{
        public void signIn(){
            mMainActivityViewModel.signIn();
        }

        public void onFabClick(){
            setFabExplodeBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            startFabExplodeAnimation(null,null);
        }

        public void article(){
            startActivity(BlogActivity.newIntent(MainActivity.this));

        }
    }
}
