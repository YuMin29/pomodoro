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

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentHomeBinding;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.adapter.CategoryAdapter;
import com.yumin.pomodoro.ui.main.clickhandler.AddMissionClickHandler;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.base.IFragmentListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "[HomeFragment]";
    private HomeViewModel mHomeViewModel;
    private CategoryAdapter mCategoryAdapter;
    private List<Mission> mMissions = new ArrayList<>();
    private List<Category> mCategory = new ArrayList<>();
    private IFragmentListener mIFragmentListener;
    FragmentHomeBinding fragmentHomeBinding;

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
        fragmentHomeBinding.homeListView.setAdapter(mCategoryAdapter);
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
            LogUtil.logD(TAG,"[Observe] mission list size = "+missions.size());
            if (missions != null)
                mHomeViewModel.getLoading().postValue(false);
        });

        mHomeViewModel.getMissionsToday().observe(getViewLifecycleOwner(), missions -> {
//            LogUtil.logD(TAG,"[Observe] today mission list size = "+missions.size());
        });

        mHomeViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if (!mCategory.containsAll(categories)) {
                mCategory.clear();
                mCategory.addAll(categories);
                mCategoryAdapter.flashCategory(mCategory);
                mCategoryAdapter.notifyDataSetChanged();
                int groupCount = fragmentHomeBinding.homeListView.getCount();
                for (int i=0; i<groupCount; i++)
                    fragmentHomeBinding.homeListView.expandGroup(i);
            }
        });

        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            fragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });
    }
}
