package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.databinding.FragmentMainBinding;
import com.yumin.pomodoro.ui.calender.CalenderFragment;
import com.yumin.pomodoro.ui.main.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.ui.mission.expired.ExpiredMissionFragment;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutFragment extends DataBindingFragment implements MainActivity.RefreshHomeFragment{
    private static final String TAG = "[MainFragment]";
    private String[] mTabTitles = null ;
    TabLayoutViewModel mMainViewModel = null;
    FragmentMainBinding mFragmentMainBinding = null;
    List<Fragment> mTabFragmentList = null;

    @Override
    public void onRefresh() {}

    @Override
    protected void initViewModel() {
        mMainViewModel = getFragmentScopeViewModel(TabLayoutViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_main, BR.homeViewModel, mMainViewModel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.logD(TAG,"[onViewCreated] fullScreenMode");
        ((MainActivity)getActivity()).fullScreenMode(false,false);
        mFragmentMainBinding = (FragmentMainBinding) getBinding();
        // init tab layout items
        mTabFragmentList = new ArrayList<>();

        mTabTitles = getResources().getStringArray(R.array.tab_titles);

        for (int i = 0; i < mTabTitles.length; i++){
            mFragmentMainBinding.tabLayout.addTab(mFragmentMainBinding.tabLayout.newTab().setText(mTabTitles[i]),i);
        }

        mTabFragmentList.add(HomeFragment.newInstance());
        mTabFragmentList.add(CalenderFragment.newInstance());
        mTabFragmentList.add(ExpiredMissionFragment.newInstance());

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
