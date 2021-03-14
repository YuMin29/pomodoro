package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.main.adapter.CategoryAdapter;
import com.yumin.pomodoro.ui.main.adapter.ExpandableViewAdapter;
import com.yumin.pomodoro.ui.main.adapter.GroupIndex;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeMilli;
import com.yumin.pomodoro.utils.TimeSort;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends DataBindingFragment implements MainActivity.OnRefreshHomeFragment {
    private static final String TAG = "[HomeFragment]";
    private HomeViewModel mHomeViewModel;
    private SharedViewModel mSharedViewModel;
    private CategoryAdapter mCategoryAdapter;
    private ExpandableViewAdapter expandableViewAdapter;
    private List<UserMission> mMissions = new ArrayList<>();
    private List<Category> mCategory = new ArrayList<>();
    private List<UserMission> mFinishedMissions = new ArrayList<>();
    FragmentHomeBinding fragmentHomeBinding;
    Category today = null;
    Category coming = null;
    private int mTodayMissions = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).setOnRefreshHomeFragment(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentHomeBinding = (FragmentHomeBinding) getBinding();
        initUI();
        observeViewModel();
    }

    @Override
    protected void initViewModel() {
        LogUtil.logD(TAG,"[initViewModel]");
        mHomeViewModel = getFragmentScopeViewModel(HomeViewModel.class);
        mSharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_home, BR.viewModel, mHomeViewModel)
                .addBindingParam(BR.click, new ClickProxy());
    }

    private void navigate(int id){
        NavHostFragment.findNavController(this).navigate(id);
    }

    private void initUI() {
        mCategoryAdapter = new CategoryAdapter(getContext(),mCategory);
        expandableViewAdapter = new ExpandableViewAdapter(getContext(),mCategory,mFinishedMissions);
        expandableViewAdapter.setOnExpandableItemClickListener(new ExpandableViewAdapter.OnExpandableItemClickListener() {
            @Override
            public void onDelete(UserMission userMission, int groupPosition, int childPosition) {
                LogUtil.logD(TAG,"[item onDelete] groupPosition = "+groupPosition+" ,childPosition = "+childPosition);
                mHomeViewModel.deleteMission(userMission);
                fragmentHomeBinding.homeListView.turnToNormal();
            }

            @Override
            public void onEdit(UserMission userMission, int groupPosition, int childPosition) {
                LogUtil.logD(TAG,"[item onEdit] groupPosition = "+groupPosition+" ,childPosition = "+childPosition);
                  MissionManager.getInstance().setStrEditId(userMission);
                  navigate(R.id.edit_mission_fragment);
            }
        });
        fragmentHomeBinding.homeListView.setAdapter(expandableViewAdapter);
        fragmentHomeBinding.homeListView.setGroupIndicator(null);
        fragmentHomeBinding.homeListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                UserMission userMission = (UserMission) expandableViewAdapter.getChild(groupPosition,childPosition);
                LogUtil.logD(TAG,"[onChildClick] item = "+userMission.getName()+
                        " ,groupPosition = "+groupPosition+" ,childPosition = "+childPosition);

                boolean isFinished = false;
                for (UserMission item : mFinishedMissions) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (userMission.getFirebaseMissionId().equals(item.getFirebaseMissionId()))
                            isFinished = true;
                    } else {
                        if (userMission.getId() == item.getId())
                            isFinished = true;
                    }
                }

                if ((groupPosition == GroupIndex.GROUP_TODAY_POSITION) && (!isFinished)) {
                    MissionManager.getInstance().setOperateId(userMission);
                    navigate(R.id.fragment_timer);
                } else {
                    // TODO: 2020/12/29 重新開始任務？ 清除完成紀錄？

                }
                return true;
            }
        });
    }

    private void observeViewModel() {
        LogUtil.logE(TAG,"[observeViewModel]");
        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.logE(TAG,"[observeViewModel][getLoading] onChanged");
                fragmentHomeBinding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
            }});

        mHomeViewModel.getAllMissions().observe(getViewLifecycleOwner(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> missions) {
                LogUtil.logD(TAG, "[observeViewModel][getAllMissions] size = " + missions.size());
                if (missions != null) {
                    updateTodayList();
                    updateComingList();
                    updateFinishedUI();
                }
                mHomeViewModel.getLoading().postValue(false);
            }});
    }

    private void updateComingList() {
        MediatorLiveData<Result> comingMissions = getComingMediatorLiveData();
        comingMissions.observe(getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null || !result.isComplete())
                    return;

                coming = new Category(getString(R.string.category_coming), Category.Index.COMING);
                coming.addAllMission(result.missionsByOperateDay);
                coming.addAllMission(result.missionsByRepeatType);
                coming.addAllMission(result.missionsByRepeatRange);
                expandCategoryList();
                LogUtil.logE(TAG,"[observeViewModel][comingObserver] coming size = "
                        +coming.getMissionList().size());
            }
        });
    }

    private void updateTodayList() {
        MediatorLiveData<Result> todayMission = getTodayMediatorLiveData();
        todayMission.observe(getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null || !result.isComplete()) {
                    // Ignore, this means only one of the queries has fininshed
                    return;
                }
                today = new Category(getString(R.string.category_today), Category.Index.TODAY);
                today.addAllMission(result.missionsByOperateDay);
                today.addAllMission(result.missionsByRepeatType);
                today.addAllMission(result.missionsByRepeatRange);
                Collections.sort(today.getMissionList(), new TimeSort());
                expandCategoryList();
                LogUtil.logE(TAG,"[observeViewModel][todayObserver] today size = "
                        +today.getMissionList().size());
                // for update unfinished mission count
                mTodayMissions = today.getMissionList().size();
                fragmentHomeBinding.unfinishedMission.setText(String.valueOf(mTodayMissions));
            }
        });
    }

    private void updateFinishedUI() {
        mHomeViewModel.getFinishedMissions().observe(getViewLifecycleOwner(), missions -> {

            for (UserMission mission : missions) {
                LogUtil.logE(TAG,mission.toString());
            }

            mFinishedMissions = missions;
            expandableViewAdapter.flashFinishedMission(mFinishedMissions);
            // update finished mission number
            if (missions == null) {
                LogUtil.logE(TAG,"[getFinishedMissions] 111");
                fragmentHomeBinding.finishedMission.setText("0");
            } else {
                LogUtil.logE(TAG,"[getFinishedMissions] 222 , mission list size = "+missions.size());
                fragmentHomeBinding.finishedMission.setText(String.valueOf(missions.size()));
                int usedTime = 0;
                for (UserMission mission : missions) {
                    usedTime += (mission.getTime()*mission.getGoal());
                }
                float num = (float)usedTime / 60;
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                fragmentHomeBinding.totalFinishedTime.setText(decimalFormat.format(num)+"h");
                // unfinished mission count - finished mission count
                fragmentHomeBinding.unfinishedMission.setText(String.valueOf(mTodayMissions-missions.size()));
            }
        });
    }

    private MediatorLiveData<Result> getComingMediatorLiveData(){
        // observe coming missions
        MediatorLiveData<Result> comingMissions = new MediatorLiveData<>();
        final Result current = new Result();
        comingMissions.addSource(mHomeViewModel.getComingNoneRepeatMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                current.setMissionsByOperateDay(userMissions);
                comingMissions.setValue(current);
            }
        });
        comingMissions.addSource(mHomeViewModel.getComingRepeatEverydayMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                current.setMissionsByRepeatType(userMissions);
                comingMissions.setValue(current);
            }
        });
        comingMissions.addSource(mHomeViewModel.getComingRepeatDefineMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                current.setMissionsByRepeatRange(userMissions);
                comingMissions.setValue(current);
            }
        });
        return comingMissions;
    }

    private MediatorLiveData<Result> getTodayMediatorLiveData() {
        // observe today missions
        MediatorLiveData<Result> todayMissions = new MediatorLiveData<Result>();
        final Result current = new Result();
        todayMissions.addSource(mHomeViewModel.getTodayNoneRepeatMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByOperateDay [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByOperateDay(userMissions);
                todayMissions.setValue(current);
            }
        });
        todayMissions.addSource(mHomeViewModel.getTodayRepeatEverydayMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatType [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByRepeatType(userMissions);
                todayMissions.setValue(current);
            }
        });
        todayMissions.addSource(mHomeViewModel.getTodayRepeatDefineMissions(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatRange [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByRepeatRange(userMissions);
                todayMissions.setValue(current);
            }
        });
        return todayMissions;
    }

    @Override
    public void onRefresh() {
        mHomeViewModel.refreshDataWhenLogout();
        observeViewModel();
    }

    private class Result {
        public List<UserMission> missionsByOperateDay = new ArrayList<>();
        public List<UserMission> missionsByRepeatType = new ArrayList<>();
        public List<UserMission> missionsByRepeatRange = new ArrayList<>();

        public Result() {}

        public void setMissionsByOperateDay(List<UserMission> missions){
            this.missionsByOperateDay = missions;
        }

        public void setMissionsByRepeatType(List<UserMission> missions){
            this.missionsByRepeatType = missions;
        }

        public void setMissionsByRepeatRange(List<UserMission> missions){
            this.missionsByRepeatRange = missions;
        }

        boolean isComplete() {
            return (missionsByOperateDay != null && missionsByRepeatType != null && missionsByRepeatRange != null);
        }
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return null == list || list.isEmpty();
    }

    private void expandCategoryList(){
        mCategory.clear();

        if (today != null && !IsNullOrEmpty(today.getMissionList()))
            mCategory.add(today);

        if (coming != null && !IsNullOrEmpty(coming.getMissionList()))
            mCategory.add(coming);

        expandableViewAdapter.flashCategory(mCategory);

        int groupCount = expandableViewAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            LogUtil.logD(TAG, "[updateCategoryList] group count = " + groupCount + " , i =" + i);
            fragmentHomeBinding.homeListView.expandGroup(i);
        }
    }

    public class ClickProxy{
        public void addMission(){
            navigate(R.id.add_mission_fragment);
        }
    }
}
