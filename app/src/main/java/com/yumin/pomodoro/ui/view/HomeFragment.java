package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.activity.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.main.adapter.ExpandableBaseAdapter;
import com.yumin.pomodoro.ui.main.adapter.GroupIndex;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.SortTimeUtil;
import com.yumin.pomodoro.ui.base.DataBindingConfig;
import com.yumin.pomodoro.ui.base.DataBindingFragment;
import com.yumin.pomodoro.ui.base.MissionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends DataBindingFragment implements MainActivity.OnRefreshHomeFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private HomeViewModel mHomeViewModel;
    private ExpandableBaseAdapter mExpandableViewAdapter;
    private List<Category> mCategory = new ArrayList<>();
    private List<UserMission> mCompletedMissions = new ArrayList<>();
    FragmentHomeBinding mFragmentHomeBinding;
    Category mToday = null;
    Category mComing = null;
    private int mTodayMissionSize = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).setOnRefreshHomeFragment(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentHomeBinding = (FragmentHomeBinding) getBinding();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        undoStatusBarColor();
        initUI();
        observeViewModel();
    }

    private void undoStatusBarColor() {
        ((MainActivity) getContext()).getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void initViewModel() {
        LogUtil.logD(TAG,"[initViewModel]");
        mHomeViewModel = getFragmentScopeViewModel(HomeViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_home, BR.homeViewModel, mHomeViewModel)
                .addBindingParam(BR.homeClickProxy, new ClickProxy());
    }

    private void navigate(int id){
        NavHostFragment.findNavController(this).navigate(id);
    }

    private void initUI() {
        mExpandableViewAdapter = new ExpandableBaseAdapter(getContext(), mCategory, mCompletedMissions);
        mExpandableViewAdapter.setOnExpandableItemClickListener(new ExpandableBaseAdapter.OnExpandableItemClickListener() {
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
                navigate(R.id.edit_mission_fragment);
            }
        });
        mFragmentHomeBinding.homeListView.setAdapter(mExpandableViewAdapter);
        mFragmentHomeBinding.homeListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                UserMission userMission = (UserMission) mExpandableViewAdapter.getChild(groupPosition, childPosition);
                LogUtil.logD(TAG, "[onChildClick] item = " + userMission.getName() + " ,groupPosition = " + groupPosition + " ,childPosition = " + childPosition);
                boolean isFinished = false;
                for (UserMission item : mCompletedMissions) {
                    if (userMission.getId() == item.getId())
                        isFinished = true;
                }

                if ((groupPosition == GroupIndex.GROUP_TODAY_POSITION) && (!isFinished)) {
                    MissionManager.getInstance().setOperateId(userMission);
                    navigate(R.id.fragment_timer);
                } else {
                    // TODO: 2021/06/05 重新開始任務？ 清除完成紀錄？
                }
                return true;
            }
        });

        mFragmentHomeBinding.homeListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogUtil.logD(TAG,"[onGroupClick] groupPosition = "+groupPosition);
                ExpandableBaseAdapter.GroupViewHolder groupViewHolder = (ExpandableBaseAdapter.GroupViewHolder) v.getTag();
                CategoryItemLayoutBinding categoryItemLayoutBinding = (CategoryItemLayoutBinding) groupViewHolder.getViewDataBinding();
                categoryItemLayoutBinding.categoryArrow.startAnimation(arrowAnimation(180,0));
                return false;
            }
        });
    }

    private Animation arrowAnimation(int fromDegrees , int toDegrees){
        Animation animation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(100);
        return animation;
    }

    private void observeViewModel() {
        LogUtil.logE(TAG,"[observeViewModel]");
        mHomeViewModel.getTodayMissions().observe(getViewLifecycleOwner(), new Observer<HomeViewModel.Result>() {
            @Override
            public void onChanged(HomeViewModel.Result result) {
                if (result == null || !result.isCompleted())
                    return;
                mToday = new Category(getString(R.string.category_today), Category.Index.TODAY);
                mToday.addAllMission(result.mNoneRepeatMissions);
                mToday.addAllMission(result.mRepeatEverydayMissions);
                mToday.addAllMission(result.mRepeatCustomizeMissions);
                Collections.sort(mToday.getMissionList(), new SortTimeUtil());
                expandCategoryList();
                LogUtil.logE(TAG,"[observeViewModel][getTodayMissions] size = " + mToday.getMissionList().size());
                mTodayMissionSize = mToday.getMissionList().size();
                updateMissionsUI();
            }
        });

        mHomeViewModel.getComingMissions().observe(getViewLifecycleOwner(), new Observer<HomeViewModel.Result>() {
            @Override
            public void onChanged(HomeViewModel.Result result) {
                if (result == null || !result.isCompleted())
                    return;
                mComing = new Category(getString(R.string.category_coming), Category.Index.COMING);
                mComing.addAllMission(result.mNoneRepeatMissions);
                mComing.addAllMission(result.mRepeatEverydayMissions);
                mComing.addAllMission(result.mRepeatCustomizeMissions);
                expandCategoryList();
                LogUtil.logE(TAG,"[observeViewModel][getComingMissions] size = " + mComing.getMissionList().size());
            }
        });

        mHomeViewModel.getCompletedMissions().observe(getViewLifecycleOwner(), missions -> {
            mCompletedMissions = missions;
            updateMissionsUI();
        });
    }

    private void updateMissionsUI(){
        mExpandableViewAdapter.updateCompletedMission(mCompletedMissions);

        if (mCompletedMissions == null) {
            mFragmentHomeBinding.finishedMission.setText("0");
        } else {
            mFragmentHomeBinding.finishedMission.setText(String.valueOf(mCompletedMissions.size()));
            int usedTime = 0;
            for (UserMission mission : mCompletedMissions) {
                usedTime += (mission.getTime()*mission.getGoal());
            }
            float num = (float)usedTime / 60;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            mFragmentHomeBinding.totalFinishedTime.setText(decimalFormat.format(num)+"h");
            // unfinished mission count - finished mission count

            if (mTodayMissionSize > 0)
                mFragmentHomeBinding.unfinishedMission.setText(String.valueOf(mTodayMissionSize - mCompletedMissions.size()));
        }
    }

    @Override
    public void onRefresh() {
        mHomeViewModel.refreshDataWhenLogout();
        observeViewModel();
    }

    public static <T> boolean listIsNullOrEmpty(Collection<T> list) {
        return null == list || list.isEmpty();
    }

    private void expandCategoryList(){
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

    public class ClickProxy{
        public void addMission(){
            navigate(R.id.add_mission_fragment);
        }
    }
}
