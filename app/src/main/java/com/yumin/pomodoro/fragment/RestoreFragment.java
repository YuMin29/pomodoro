package com.yumin.pomodoro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.viewmodel.RestoreViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RestoreFragment extends DataBindingFragment {
    private RestoreViewModel mRestoreViewModel;

    @Override
    protected void initViewModel() {
        mRestoreViewModel = getFragmentScopeViewModel(RestoreViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void observeViewModel(){
        mRestoreViewModel.getProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean progress) {
                // set time stamp
                String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.NAV_ITEM_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(FirebaseAuth.getInstance().getCurrentUser().getUid() + MainActivity.KEY_RESTORE_TIME,"上次還原時間:" + nowDate).commit();

                // false -> navigate up
                if (!progress) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("restoreFinished",true);
                    navigateUp(bundle);
                }


//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.setRestoreTime(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });


        mRestoreViewModel.getResultMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<RestoreViewModel.Result>() {
            @Override
            public void onChanged(RestoreViewModel.Result restoreProgressResult) {
                if (restoreProgressResult.isComplete()) {
                    mRestoreViewModel.operateRestore();
                }
            }
        });
    }

    private void navigateUp(Bundle bundle){
        NavHostFragment.findNavController(this).navigate(R.id.nav_settings,bundle);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_restore, BR.restore_view_model,mRestoreViewModel);
    }
}