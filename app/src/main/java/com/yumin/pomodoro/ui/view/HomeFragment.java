package com.yumin.pomodoro.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.main.adapter.CategoryAdapter;
import com.yumin.pomodoro.ui.main.adapter.ExpandableViewAdapter;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SharedViewModel;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.base.DataBindingConfig;
import com.yumin.pomodoro.utils.base.DataBindingFragment;
import com.yumin.pomodoro.utils.base.MissionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeFragment extends DataBindingFragment {
    private static final String TAG = "[HomeFragment]";
    private HomeViewModel mHomeViewModel;
    private SharedViewModel mSharedViewModel;
    private CategoryAdapter mCategoryAdapter;
    private ExpandableViewAdapter expandableViewAdapter;
    private List<Mission> mMissions = new ArrayList<>();
    private List<Category> mCategory = new ArrayList<>();
    FragmentHomeBinding fragmentHomeBinding;
    Category today = null;
    Category coming = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        expandableViewAdapter = new ExpandableViewAdapter(mCategory,getContext());
        expandableViewAdapter.setOnClickListenerEditOrDelete(new ExpandableViewAdapter.OnClickListenerEditOrDelete() {
            @Override
            public void OnClickListenerDelete(Mission mission,int groupPosition, int childPosition) {
                LogUtil.logD(TAG,"[item delete] groupPosition = "+groupPosition+" ,childPosition = "+childPosition);
                mHomeViewModel.deleteMission(mission);
                fragmentHomeBinding.homeListView.turnToNormal();
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
                if (!userMission.isFinished()) {
//                    MissionManager.getInstance().setOperateId(userMission.getId());
                    MissionManager.getInstance().setOperateId(userMission.getStrId());
                    navigate(R.id.fragment_timer);
                } else {
                    // TODO: 2020/12/29 重新開始任務？ 清除完成紀錄？

                }
                return true;
            }
        });

        fragmentHomeBinding.homeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ExpandableListView listView = (ExpandableListView) parent;
                long pos = listView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                int childPos = ExpandableListView.getPackedPositionChild(pos);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    UserMission mission = (UserMission) expandableViewAdapter.getChild(groupPos,childPos);
                    LogUtil.logD(TAG,"[onItemLongClick] item = "+mission.getName()+
                            " ,groupPosition = "+groupPos+" ,childPosition = "+childPos);
                    if (!mission.isFinished()) {
//                        MissionManager.getInstance().setEditId(mission.getId());
                        MissionManager.getInstance().setStrEditId(mission.getStrId());
                        navigate(R.id.edit_mission_fragment);
                        return true;
                    } else {
                        // TODO: 1/18/21 重新編輯任務？
                    }
                }
                return false;
            }
        });
    }

    private void observeViewModel() {
        mHomeViewModel.getMissions().observe(getViewLifecycleOwner(), missions -> {
            LogUtil.logD(TAG,"[observeViewModel] mission list size = "+missions.size());
            if (missions != null)
                mHomeViewModel.getLoading().postValue(false);
        });

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
                expandCategoryList();
                LogUtil.logE(TAG,"[observeViewModel][todayObserver] today size = "
                        +today.getMissionList().size());
            }
        });

        MediatorLiveData<Result> comingMissions = getComingMediatorLiveData();
        Observer<List<UserMission>> comingObserver = new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logE(TAG,"[observeViewModel][comingObserver] onChanged");
                coming = new Category(getString(R.string.category_coming), Category.Index.COMING);
                for (UserMission mission : userMissions) {
                    coming.addMission(mission);
                }
                expandCategoryList();
            }
        };
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

        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            fragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });

        mHomeViewModel.getFinishedMissions().observe(getViewLifecycleOwner(), missions -> {
            // update finished mission number
            if (missions == null) {
                fragmentHomeBinding.finishedMission.setText("0");
            } else {
                fragmentHomeBinding.finishedMission.setText(String.valueOf(missions.size()));

                int usedTime = 0;
                for (Mission mission : missions) {
                    usedTime += mission.getTime();
                }
                float num = (float)usedTime / 60;
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                fragmentHomeBinding.totalFinishedTime.setText(decimalFormat.format(num)+"h");
            }
        });

        mHomeViewModel.getUnfinishedMissions().observe(getViewLifecycleOwner(), missions -> {
            // update unfinished mission number
            if (missions == null)
                fragmentHomeBinding.unfinishedMission.setText("0");
            else
                fragmentHomeBinding.unfinishedMission.setText(String.valueOf(missions.size()));
        });

    }

    private MediatorLiveData<Result> getComingMediatorLiveData(){
        // observe coming missions
        MediatorLiveData<Result> comingMissions = new MediatorLiveData<>();
        final Result current = new Result();
        comingMissions.addSource(mHomeViewModel.getComingMissionsByOperateDay(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                current.setMissionsByOperateDay(userMissions);
                comingMissions.setValue(current);
            }
        });
        comingMissions.addSource(mHomeViewModel.getComingMissionsByRepeatType(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                current.setMissionsByRepeatType(userMissions);
                comingMissions.setValue(current);
            }
        });
        comingMissions.addSource(mHomeViewModel.getComingMissionsByRepeatRange(), new Observer<List<UserMission>>() {
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
        todayMissions.addSource(mHomeViewModel.getTodayMissionsByOperateDay(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByOperateDay [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByOperateDay(userMissions);
                todayMissions.setValue(current);
            }
        });
        todayMissions.addSource(mHomeViewModel.getTodayMissionsByRepeatType(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatType [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByRepeatType(userMissions);;
                todayMissions.setValue(current);
            }
        });
        todayMissions.addSource(mHomeViewModel.getTodayMissionsByRepeatRange(), new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissions) {
                LogUtil.logD(TAG,"getTodayMissionsByRepeatRange [onChanged] SIZE = "+userMissions.size());
                current.setMissionsByRepeatRange(userMissions);
                todayMissions.setValue(current);
            }
        });
        return todayMissions;
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
