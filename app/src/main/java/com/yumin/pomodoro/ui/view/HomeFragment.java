package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.ui.main.adapter.CategoryAdapter;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.base.IFragmentListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel mHomeViewModel;
    private CategoryAdapter mCategoryAdapter;
    private List<Mission> mMissions = new ArrayList<>();
    private List<Category> mCategory = new ArrayList<>();
    private IFragmentListener mIFragmentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIFragmentListener = (IFragmentListener) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mCategoryAdapter = new CategoryAdapter(getContext(),mCategory);
//        mFragmentHomeBinding.setViewModel(mHomeViewModel);
//        mFragmentHomeBinding.setLifecycleOwner(this);
//        mFragmentHomeBinding.homeListView.setAdapter(mCategoryAdapter);
//        mFragmentHomeBinding.homeListView.setGroupIndicator(null);
//        mFragmentHomeBinding.homeListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                LogUtil.logD("[Stella]","[onGroupClick] groupPosition = "+groupPosition);
//                return true;
//            }
//        });
//        mFragmentHomeBinding.homeListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                LogUtil.logD("[Stella]","[onChildClick] childPosition = "+childPosition);
//                return false;
//            }
//        });
//
//        mFragmentHomeBinding.addItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // launch fragment to add new item
//				mIFragmentListener.switchFragment("AddMissionFragment");
//            }
//        });
        observeViewModel();
    }


    private void observeViewModel(){
//        mHomeViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
//            if (!mCategory.containsAll(categories)) {
//                mCategory.clear();;
//                mCategory.addAll(categories);
//                mCategoryAdapter.flashCategory(mCategory);
//                mCategoryAdapter.notifyDataSetChanged();
//                int groupCount = mFragmentHomeBinding.homeListView.getCount();
//                for (int i=0; i<groupCount; i++)
//                    mFragmentHomeBinding.homeListView.expandGroup(i);
//            }
//        });
//
//        mHomeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
//            mFragmentHomeBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
//        });
    }
}
