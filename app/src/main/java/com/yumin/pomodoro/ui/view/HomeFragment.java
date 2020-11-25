package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.yumin.pomodoro.ui.main.adapter.ExpandableViewBaseAdapter;
import com.yumin.pomodoro.ui.main.adapter.ExpandableViewViewAdapter;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.base.IFragmentListener;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "[HomeFragment]";
    private HomeViewModel mHomeViewModel;
    private CategoryAdapter mCategoryAdapter;
    private ExpandableViewViewAdapter expandableViewAdapter;
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
        fragmentHomeBinding.setLifecycleOwner(this);
        fragmentHomeBinding.setClick(new ClickProxy());
        initViewModel();
        initUI();
        observeViewModel();
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
        expandableViewAdapter = new ExpandableViewViewAdapter(mCategory,getContext());
        fragmentHomeBinding.homeListView.setAdapter(expandableViewAdapter);
        fragmentHomeBinding.homeListView.setGroupIndicator(null);
        fragmentHomeBinding.homeListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Mission mission = (Mission) expandableViewAdapter.getChild(groupPosition,childPosition);
                LogUtil.logD(TAG,"[onChildClick] item = "+mission.getName()+
                        " ,groupPosition = "+groupPosition+" ,childPosition = "+childPosition);
                Bundle bundle = new Bundle();
                bundle.putInt("itemId",mission.getId());
                MainActivity.getNavController().navigate(R.id.fragment_timer,bundle);
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
                    Mission mission = (Mission) expandableViewAdapter.getChild(groupPos,childPos);
                    LogUtil.logD(TAG,"[onItemLongClick] item = "+mission.getName()+
                            " ,groupPosition = "+groupPos+" ,childPosition = "+childPos);
                    // switch to edit mission fragment
                    Bundle bundle = new Bundle();
                    bundle.putInt("editId",mission.getId());
                    MainActivity.getNavController().navigate(R.id.edit_mission_fragment,bundle);
                    return true;
                }
                return false;
            }
        });
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
                today = new Category(getString(R.string.category_today));
               for (Mission mission : missions) {
                   today.addMission(mission);
               }
               updateCategoryList();
            }
        });

        mHomeViewModel.getComingMissions().observe(getViewLifecycleOwner(),missions -> {
            LogUtil.logD(TAG,"[observeViewModel] coming mission list size = "+missions.size());
            if (missions.size() > 0) {
                coming = new Category(getString(R.string.category_coming));
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
        expandableViewAdapter.flashCategory(mCategory);

        int groupCount = expandableViewAdapter.getGroupCount();
        for (int i=0; i<groupCount; i++) {
            LogUtil.logD(TAG, "[updateCategoryList] group count = " + groupCount + " , i =" + i);
            fragmentHomeBinding.homeListView.expandGroup(i);
        }
    }

    public class ClickProxy{
        public void addMission(){
            MainActivity.getNavController().navigate(R.id.add_mission_fragment);
        }
    }
}
