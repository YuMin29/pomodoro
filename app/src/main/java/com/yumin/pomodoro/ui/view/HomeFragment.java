package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.adapter.CategoryAdapter;
import com.yumin.pomodoro.ui.main.adapter.TestExpandableAdapter;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.base.IFragmentListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "[HomeFragment]";
    private HomeViewModel mHomeViewModel;
    private CategoryAdapter mCategoryAdapter;
    private TestExpandableAdapter testExpandableAdapter;
    private List<Mission> mMissions = new ArrayList<>();
    private List<Category> mCategory = new ArrayList<>();
    private IFragmentListener mIFragmentListener;
    FragmentHomeBinding fragmentHomeBinding;
    Category today;
    Category coming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        initViewModel();
//        initClickHandler();
        initUI();
        fragmentHomeBinding.setViewModel(mHomeViewModel);
        return fragmentHomeBinding.getRoot();
    }

    private void initViewModel() {
        LogUtil.logD(TAG,"[initViewModel]");
        mHomeViewModel =  new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(getActivity().getApplication()),getContext()))).get(HomeViewModel.class);
    }

    private void initUI() {
        mCategoryAdapter = new CategoryAdapter(getContext(),mCategory);
        testExpandableAdapter = new TestExpandableAdapter(mCategory,getContext());
        fragmentHomeBinding.homeListView.setAdapter(testExpandableAdapter);
        fragmentHomeBinding.homeListView.setGroupIndicator(null);
        fragmentHomeBinding.homeListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogUtil.logD("[Stella]","[onGroupClick] groupPosition = "+groupPosition);
                return true;
            }
        });
        fragmentHomeBinding.homeListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LogUtil.logD("[Stella]","[onChildClick] childPosition = "+childPosition);
                return false;
            }
        });

        fragmentHomeBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch fragment to add new item
                mIFragmentListener.switchFragment("AddMissionFragment");
            }
        });
        observeViewModel();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIFragmentListener = (IFragmentListener) context;
    }

    private void observeViewModel(){
        mHomeViewModel.getMissions().observe(getViewLifecycleOwner(), missions -> {
            LogUtil.logD(TAG,"[observeViewModel] mission list size = "+missions.size());
            if (missions != null)
                mHomeViewModel.getLoading().postValue(false);
        });

        mHomeViewModel.getTodayMissions().observe(getViewLifecycleOwner(), missions -> {
            LogUtil.logD(TAG,"[observeViewModel] today mission list size = "+missions.size());
            if (missions.size() > 0) {
                today = new Category("今天");
               for (Mission mission : missions) {
                   today.addMission(mission);
               }
               updateCategoryList();
            }
        });

        mHomeViewModel.getComingMissions().observe(getViewLifecycleOwner(),missions -> {
            LogUtil.logD(TAG,"[observeViewModel] coming mission list size = "+missions.size());
            if (missions.size() > 0) {
                coming = new Category("即將到來");
                for (Mission mission : missions) {
                    coming.addMission(mission);
                }
                updateCategoryList();
            }
        });

        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            fragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });
    }

    private void updateCategoryList(){
        mCategory.clear();
        if (today != null)
            mCategory.add(today);
        if (coming != null )
            mCategory.add(coming);
        testExpandableAdapter.flashCategory(mCategory);

        int groupCount = testExpandableAdapter.getGroupCount();
        for (int i=0; i<groupCount; i++) {
            LogUtil.logD(TAG, "[updateCategoryList] group count = " + groupCount + " , i =" + i);
            fragmentHomeBinding.homeListView.expandGroup(i);
        }
    }
}
