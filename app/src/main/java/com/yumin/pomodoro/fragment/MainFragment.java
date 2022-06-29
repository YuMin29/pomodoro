package com.yumin.pomodoro.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.databinding.FragmentMainBinding;
import com.yumin.pomodoro.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends DataBindingFragment implements MainActivity.RefreshHomeFragment{
    private static final String TAG = "[MainFragment]";
    private String[] mTabTitles = null ;
    MainViewModel mMainViewModel = null;
    FragmentMainBinding mFragmentMainBinding = null;
    List<Fragment> mTabFragmentList = null;

    @Override
    public void onRefresh() {}

    @Override
    protected void initViewModel() {
        mMainViewModel = getFragmentScopeViewModel(MainViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_main, BR.homeViewModel, mMainViewModel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).fullScreenMode(false);
        mFragmentMainBinding = (FragmentMainBinding) getBinding();
        // init tab layout items
        mTabFragmentList = new ArrayList<>();

        mTabTitles = getResources().getStringArray(R.array.tab_titles);

        for (int i = 0; i < mTabTitles.length; i++){
            mFragmentMainBinding.tabLayout.addTab(mFragmentMainBinding.tabLayout.newTab().setText(mTabTitles[i]),i);
        }

        mTabFragmentList.add(new HomeFragment());
        mTabFragmentList.add(new CalenderFragment());
        mTabFragmentList.add(new ExpiredMissionFragment());

        mFragmentMainBinding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mTabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mTabTitles.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                Log.d(TAG,"[getPageTitle] position = "+position);
                return mTabTitles[position];
            }

        });

        mFragmentMainBinding.tabLayout.setupWithViewPager(mFragmentMainBinding.viewPager,false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"[onDestroyView]");
        mFragmentMainBinding.tabLayout.removeAllTabs();
        mTabFragmentList.clear();
        mFragmentMainBinding.viewPager.setAdapter(null);
    }
}
