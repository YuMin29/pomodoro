
package com.yumin.pomodoro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
<<<<<<< HEAD
import androidx.fragment.app.FragmentManager;
=======
>>>>>>> d7e87be... tmp

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
<<<<<<< HEAD
import com.yumin.pomodoro.ui.home.AddMissionFragment;
=======
>>>>>>> d7e87be... tmp
import com.yumin.pomodoro.utils.IFragmentListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener, IFragmentListener {

    private AppBarConfiguration mAppBarConfiguration;
    static TextView mToolbarTitle = null;
<<<<<<< HEAD
=======
    private NavController mNavController;
>>>>>>> d7e87be... tmp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set status bar color as tool bar color
        setStatusBarGradient(this);
        setContentView(R.layout.activity_main);
        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTitle = findViewById(R.id.tool_bar_title);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jump to add mission fragment
                mNavController.navigate(R.id.add_mission_fragment);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
                R.id.nav_backup, R.id.nav_restore, R.id.nav_total,
                R.id.nav_calender, R.id.nav_save_mission, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
<<<<<<< HEAD
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
=======
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mNavController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);
>>>>>>> d7e87be... tmp
    }

    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.background);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
    
    @Override
    public void switchFragment(String fragmentName){
<<<<<<< HEAD
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if ("AddMissionFragment".contentEquals(fragmentName)) {
			AddMissionFragment fragment = new AddMissionFragment();
            transaction.replace(R.id.fragment_container, fragment, "AppManageFragment")
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
=======
        mNavController.navigate(R.id.add_mission_fragment);
>>>>>>> d7e87be... tmp
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    }

    public static void setToolbarTitle(String tile) {
        mToolbarTitle.setText(tile);
    }
}
