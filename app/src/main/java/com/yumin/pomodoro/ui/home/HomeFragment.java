package com.yumin.pomodoro.ui.home;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.adapter.ExpandableBaseAdapter;
import com.yumin.pomodoro.adapter.GroupIndex;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.base.MissionManager;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.main.MainActivity;
import com.yumin.pomodoro.ui.timer.TimerFragment;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.SortTimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends DataBindingFragment implements MainActivity.RefreshHomeFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    FragmentHomeBinding mFragmentHomeBinding;
    Category mToday = null;
    Category mComing = null;
    private HomeViewModel mHomeViewModel;
    private ExpandableBaseAdapter mExpandableViewAdapter;
    private List<Category> mCategory = new ArrayList<>();
    private List<UserMission> mCompletedMissions = new ArrayList<>();
    private int mTodayMissionSize = -1;

    // singleton
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static <T> boolean listIsNullOrEmpty(Collection<T> list) {
        return null == list || list.isEmpty();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setRefreshHomeFragment(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentHomeBinding = (FragmentHomeBinding) getBinding();
        initLayout();
        observeData();
    }

    @Override
    protected void initViewModel() {
        LogUtil.logD(TAG, "[initViewModel]");
        mHomeViewModel = getFragmentScopeViewModel(HomeViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_home, BR.homeViewModel, mHomeViewModel)
                .addBindingParam(BR.homeClickProxy, new ClickProxy());
    }

    private void navigate(int id) {
        NavHostFragment.findNavController(this).navigate(id);
    }

    private void initLayout() {
        mExpandableViewAdapter = new ExpandableBaseAdapter(getContext(), mCategory, mCompletedMissions);
        mExpandableViewAdapter.setOnExpandableItemClickListener(new ExpandableBaseAdapter.ExpandableItemClickListener() {
            @Override
            public void onDelete(UserMission userMission, int groupPosition, int childPosition) {
                LogUtil.logD(TAG, "[onDelete] groupPosition = " + groupPosition + " ,childPosition = " + childPosition);
                mHomeViewModel.deleteMission(userMission);
                mFragmentHomeBinding.homeListView.turnToNormal();
            }

            @Override
            public void onEdit(UserMission userMission, int groupPosition, int childPosition) {
                LogUtil.logD(TAG, "[onEdit] groupPosition = " + groupPosition + " ,childPosition = " + childPosition);
                MissionManager.getInstance().setStrEditId(userMission);
                navigate(R.id.action_global_editMissionFragment);
            }
        });
        mFragmentHomeBinding.homeListView.setAdapter(mExpandableViewAdapter);
        mFragmentHomeBinding.homeListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            UserMission userMission = (UserMission) mExpandableViewAdapter.getChild(groupPosition, childPosition);
            LogUtil.logD(TAG, "[onChildClick] item = " + userMission.getName() + " ,groupPosition = " + groupPosition + " ,childPosition = " + childPosition);
            boolean isFinished = false;
            for (UserMission item : mCompletedMissions) {
                if (userMission.getId() == item.getId())
                    isFinished = true;
            }

            if ((groupPosition == GroupIndex.GROUP_TODAY_POSITION) && (!isFinished)) {
                Bundle bundle = new Bundle();
                bundle.putInt(TimerFragment.BUNDLE_FLOAT_VIEW_BACKGROUND_COLOR, userMission.getColor());
                ((MainActivity) getActivity()).setFabExplodeBackgroundColor(userMission.getColor());
                ((MainActivity) getActivity()).startFabExplodeAnimation(bundle, userMission);
            }
            return true;
        });

        mFragmentHomeBinding.homeListView.setOnGroupClickListener((parent, view, groupPosition, id) -> {
            LogUtil.logD(TAG, "[onGroupClick] groupPosition = " + groupPosition);
            ExpandableBaseAdapter.GroupViewHolder groupViewHolder = (ExpandableBaseAdapter.GroupViewHolder) view.getTag();
            CategoryItemLayoutBinding categoryItemLayoutBinding = (CategoryItemLayoutBinding) groupViewHolder.getViewDataBinding();
            categoryItemLayoutBinding.categoryArrow.startAnimation(arrowAnimation(180, 0));
            return false;
        });
    }

    private Animation arrowAnimation(int fromDegrees, int toDegrees) {
        Animation animation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(500);
        return animation;
    }

    private void observeData() {
        LogUtil.logE(TAG, "[observeData]");
        mHomeViewModel.getTodayMissions().observe(getViewLifecycleOwner(), result -> {
            if (result == null || !result.isCompleted())
                return;
            mToday = new Category(getString(R.string.category_today), Category.Index.TODAY);
            mToday.addAllMission(result.mNoneRepeatMissions);
            mToday.addAllMission(result.mRepeatEverydayMissions);
            mToday.addAllMission(result.mRepeatCustomizeMissions);
            Collections.sort(mToday.getMissionList(), new SortTimeUtil());
            expandCategoryList();
            LogUtil.logE(TAG, "[observeData][getTodayMissions] size = " + mToday.getMissionList().size());
            mTodayMissionSize = mToday.getMissionList().size();
            updateUI(mCompletedMissions);
        });

        mHomeViewModel.getComingMissions().observe(getViewLifecycleOwner(), result -> {
            if (result == null || !result.isCompleted())
                return;
            mComing = new Category(getString(R.string.category_coming), Category.Index.COMING);
            mComing.addAllMission(result.mNoneRepeatMissions);
            mComing.addAllMission(result.mRepeatEverydayMissions);
            mComing.addAllMission(result.mRepeatCustomizeMissions);
            expandCategoryList();
            LogUtil.logE(TAG, "[observeData][getComingMissions] size = " + mComing.getMissionList().size());
        });

        mHomeViewModel.getCompletedMissions().observe(getViewLifecycleOwner(), missions -> {
            mCompletedMissions = missions;
            updateUI(mCompletedMissions);
        });
    }

    private void updateUI(List<UserMission> missions) {
        mExpandableViewAdapter.updateCompletedMission(missions);
        mHomeViewModel.updateUI(missions, mTodayMissionSize);
    }

    @Override
    public void onRefresh() {
        mHomeViewModel.refreshDataWhenLogout();
    }

    private void expandCategoryList() {
        mCategory.clear();

        if (mToday != null && !listIsNullOrEmpty(mToday.getMissionList()))
            mCategory.add(mToday);

        if (mComing != null && !listIsNullOrEmpty(mComing.getMissionList()))
            mCategory.add(mComing);

        mExpandableViewAdapter.updateCategory(mCategory);

        int groupCount = mExpandableViewAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            LogUtil.logD(TAG, "[updateCategoryList] group count = " + groupCount + " , i =" + i);
            mFragmentHomeBinding.homeListView.expandGroup(i);
        }
        mFragmentHomeBinding.progressBar.setVisibility(View.INVISIBLE);
    }

    public class ClickProxy {
        public void addMission() {
            navigate(R.id.action_global_addMissionFragment);
        }
    }
}
