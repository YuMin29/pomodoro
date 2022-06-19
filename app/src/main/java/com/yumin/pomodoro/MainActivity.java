package com.yumin.pomodoro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yumin.pomodoro.adapter.DrawerListAdapter;
import com.yumin.pomodoro.adapter.NavItem;
import com.yumin.pomodoro.base.MissionManager;
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
    public ImageView userSignIn;
    public ImageView settings;
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
    View mTopView;
    private FirebaseAuth mAuth;

    public static void commitWhenLifecycleStarted(Lifecycle lifecycle, int destination, Bundle bundle) {
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_START) {
                    LogUtil.logD(TAG, "[commitWhenLifeCycleStarted]");
                    lifecycle.removeObserver(this);
                    mNavController.navigate(destination, bundle);
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
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        // set status bar color
//        setStatusBarGradient(this);
//        setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View settingView = findViewById(R.id.top_view);
        settingView.setPadding(0, getStatusBarHeight(), 0, 0);
        mTopView = findViewById(R.id.top_view);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.getMenu().getItem(1).setEnabled(false);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_calender, R.id.nav_save_mission)
                .build();
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start a quick mission
                MissionManager.getInstance().setOperateId(null);
                mNavController.navigate(R.id.fragment_timer);
            }
        });

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        mNavController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        userSignIn = findViewById(R.id.user_sign_in);
        userSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFirebaseUser == null) {
                    // check network state first
                    if (!isNetworkConnected()) {
                        // request user need to connect network
                        Toast.makeText(getApplicationContext(), R.string.network_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mNavController.navigate(R.id.fragment_login);
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


        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch to settings fragment
                mNavController.navigate(R.id.nav_settings);
            }
        });

//        // set toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        mToolbarTitle = findViewById(R.id.tool_bar_title);

//        mNavigationView = findViewById(R.id.nav_view);
//        mDrawerLayout = findViewById(R.id.drawer_layout);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mNavHeaderMain = mDrawerLayout.findViewById(R.id.nav_header_main);
//        mNavHeaderMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCurrentFirebaseUser == null) {
//                    // check network state first
//                    if (!isNetworkConnected()) {
//                        // request user need to connect network
//                        Toast.makeText(getApplicationContext(),"請設置網路",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    mNavController.navigate(R.id.fragment_login);
//                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
//                            .setTitle(R.string.logout)
//                            .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            LogUtil.logE(TAG,"sign out [onComplete]");
//                                            mRefreshHomeFragment.onRefresh();
//                                            setBackupTime(null);
//                                            setRestoreTime(null);
//                                        }
//                                    });
//                                }
//                            }).setPositiveButton(getString(R.string.cancel), null);
//                    builder.show();
//                }
//                closeDrawerLayout();
//            }
//        });
//
//        mNavItems.add(new NavItem(getString(R.string.menu_calender), R.drawable.ic_baseline_calendar_today_24));
//        mNavItems.add(new NavItem(getString(R.string.menu_settings), R.drawable.ic_baseline_settings_24));
//        mNavItems.add(new NavItem(getString(R.string.menu_backup), R.drawable.ic_baseline_cloud_upload_24));
//        mNavItems.add(new NavItem(getString(R.string.menu_restore), R.drawable.ic_baseline_cloud_download_24));
//        mNavItems.add(new NavItem(getString(R.string.menu_expired_mission),R.drawable.ic_baseline_save_24));
//
//        mDrawerList = (ListView) findViewById(R.id.navList);
//        mDrawerListAdapter = new DrawerListAdapter(this, mNavItems);
//        mDrawerList.setAdapter(mDrawerListAdapter);
//        // Drawer Item click listeners
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                LogUtil.logE(TAG,"[onItemClick] position = "+position);
//                switch (position) {
//                    case POSITION_CALENDER:
//                        mNavController.navigate(R.id.nav_calender);
//                        break;
//                    case POSITION_SETTINGS:
//                        mNavController.navigate(R.id.nav_settings);
//                        break;
//                    case POSITION_BACKUP:
//                        if (isLoginFirebase())
//                            showAlertDialog(R.string.menu_backup,R.string.backup_message,R.id.fragment_backup);
//                        break;
//                    case POSITION_RESTORE:
//                        if (isLoginFirebase())
//                            showAlertDialog(R.string.menu_restore,R.string.restore_message,R.id.fragment_restore);
//                        break;
//                    case POSITION_EXPIRED:
//                        mNavController.navigate(R.id.fragment_expired_mission);
//                        break;
//                }
//                closeDrawerLayout();
//            }
//        });
//
//        NavigationUI.setupActionBarWithNavController(this,mNavController,mDrawerLayout);
//        NavigationUI.setupWithNavController(mNavigationView, mNavController);

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

    private void showAlertDialog(int title, int message, int fragmentId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                mNavController.navigate(fragmentId);
            }

        });
        dialog.show();
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

    private void getApplicationHashKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.yumin.pomodoro", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                LogUtil.logE("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            LogUtil.logE("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            LogUtil.logE("no such an algorithm", e.toString());
        } catch (Exception e) {
            LogUtil.logE("exception", e.toString());
        }
    }

    private void closeDrawerLayout() {
        mDrawerLayout.closeDrawers();
    }

    public void fullScreenMode(boolean enable) {
        mTopView.setVisibility(enable ? View.GONE : View.VISIBLE);
        settings.setVisibility(enable ? View.GONE : View.VISIBLE);
        userSignIn.setVisibility(enable ? View.GONE : View.VISIBLE);
        mBottomNavigationView.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        LogUtil.logD(TAG, "[onDestinationChanged] label = " + navDestination.getLabel().toString());
        String navHomeLabel = getResources().getString(R.string.menu_home);
        if (navHomeLabel.equals(navDestination.getLabel().toString())) {
            mFab.setVisibility(View.VISIBLE);
        } else {
//            mFab.setVisibility(View.GONE);
        }
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
        } else {
            //permission is automatically granted on sdk<23 upon installation
            LogUtil.logV(TAG, "Permission is granted");
            return true;
        }
    }

    public interface RefreshHomeFragment {
        void onRefresh();
    }
}
