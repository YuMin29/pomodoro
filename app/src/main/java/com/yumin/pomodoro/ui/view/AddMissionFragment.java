package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.api.ApiServiceImpl;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBinding;
import com.yumin.pomodoro.ui.base.ViewModelFactory;
import com.yumin.pomodoro.ui.main.viewholder.RecyclerViewHolder;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionEventHandler;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.adapter.RecyclerViewBaseAdapter;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
    RecyclerViewAdapter mRecyclerViewAdapter;
    FragmentAddMissionBinding fragmentAddMissionBinding;
    List<AdjustMissionItem> adjustMissionItems = new ArrayList<>();

    public AddMissionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        fragmentAddMissionBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_mission,container,false);
        setupViewModel();
        return fragmentAddMissionBinding.getRoot();
    }

    private void setupObserver() {
        mAddMissionViewModel.getInitMission().observe(getViewLifecycleOwner(), new Observer<Mission>() {
            @Override
            public void onChanged(Mission mission) {
                LogUtil.logD(TAG,"MISSION [onChanged]");
                // create adjust items base on mission value
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getTime()), R.string.mission_time, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.TIME));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getShortBreakTime()), R.string.mission_break, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.SHORT_BREAK));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getLongBreakTime()), R.string.mission_long_break, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.LONG_BREAK));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getGoal()), R.string.mission_goal, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.GOAL));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getRepeat()), R.string.mission_repeat, View.VISIBLE, View.VISIBLE, AdjustMissionItem.AdjustItem.REPEAT));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getOperateDay()),R.string.mission_operate_day, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.OPERATE_DAY));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getColor()),R.string.mission_theme, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.COLOR));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getEnableNotification()),R.string.mission_notification, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.NOTIFICATION));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getEnableSound()),R.string.mission_sound, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.SOUND));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getVolume()),R.string.mission_sound_level, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.VOLUME));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getEnableVibrate()),R.string.mission_vibrate, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.VIBRATE));
                adjustMissionItems.add(new AdjustMissionItem(getContext(), String.valueOf(mission.getKeepScreenOn()),R.string.mission_keep_awake, View.GONE, View.GONE, AdjustMissionItem.AdjustItem.SCREEN_ON));
//                renderList(adjustMissionItems);
                mAddMissionEventHandler.setAdjustMissionItems(adjustMissionItems);
            }
        });
    }

    private void setupUI() {
        // set title
        MainActivity.setToolbarTitle("Add mission");
//        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mAddMissionEventHandler.getMissionItemListener(),
//                mAddMissionEventHandler.getOnItemClickListener(), mAddMissionViewModel);
//        fragmentAddMissionBinding.recyclerView.setAdapter(mRecyclerViewAdapter);
        ((SimpleItemAnimator) fragmentAddMissionBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),6);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position >= 8 && position < 11)
                    return 2;
                else if ((position > 0 && position < 3) || (position >= 3 && position < 7))
                    return 3;
                else
                    return 6;
            }
        });
        fragmentAddMissionBinding.recyclerView.setLayoutManager(gridLayoutManager);
        fragmentAddMissionBinding.missionTitle.addTextChangedListener(mAddMissionEventHandler.getTextWatcher());
    }

    private void setupViewModel() {
        mAddMissionViewModel =  new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication(),
                new ApiHelper(new ApiServiceImpl(),getContext()))).get(AddMissionViewModel.class);
        mAddMissionEventHandler = new AddMissionEventHandler(mAddMissionViewModel,getContext(),adjustMissionItems);
    }

    private void renderList(List<AdjustMissionItem> adjustMissionItems){
        mRecyclerViewAdapter.addData(adjustMissionItems);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.logD(TAG, "[onPause]");
    }

    @Override
    public void onStop() {
        LogUtil.logD(TAG, "[onStop]");
        super.onStop();
//        mFragmentAddMissionBinding.missionTitle.getText().clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.logD(TAG, "[onViewCreated]");
        setupUI();
        setupObserver();
        fragmentAddMissionBinding.setClick(mAddMissionEventHandler);
        fragmentAddMissionBinding.setAdapter(mRecyclerViewAdapter);
    }

    public class RecyclerViewAdapter extends RecyclerViewBaseAdapter<AdjustMissionItem> {
        MissionItemView.MissionItemListener mMissionItemListener;
        OnItemClickListener mOnItemClickListener;
        AddMissionViewModel addMissionViewModel;

        public RecyclerViewAdapter(Context context, MissionItemView.MissionItemListener missionItemListener,
                                   OnItemClickListener onItemClickListener, AddMissionViewModel addMissionViewModel) {
            super(context);
            this.mMissionItemListener = missionItemListener;
            this.mOnItemClickListener = onItemClickListener;
            this.addMissionViewModel = addMissionViewModel;
        }

        @Override
        protected int getLayoutResId(int viewType) {
            return R.layout.mission_item_view;
        }

        @Override
        public void setContent(RecyclerViewHolder holder, ArrayList<AdjustMissionItem> items, int position) {
            holder.setText(R.id.num_textview,items.get(position).getContent());
            holder.setText(R.id.description_textview,items.get(position).getDesc());
            holder.setClickListener(R.id.add_num, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMissionItemListener.onAddButtonClick(v, position);
                }
            });

            holder.setClickListener(R.id.minus_num, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMissionItemListener.onMinusButtonClock(v, position);
                }
            });

            holder.setVisibility(R.id.add_num,items.get(position).getAddButtonVisibility());
            holder.setVisibility(R.id.minus_num,items.get(position).getMinusButtonVisibility());
        }

        @Override
        protected void onBindItemClickListener(RecyclerViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return false;
                }
            });
        }

        public void addData(List<AdjustMissionItem> adjustMissionItems){
            this.items.clear();
            this.items.addAll(adjustMissionItems);
        }
    }

    public class RecyclerViewBindingAdapter{
        private final static String TAG = "[RecyclerViewBindingAdapter]";
        @BindingAdapter(value = {"adapter","submitList"})
        public void bindList(RecyclerView recyclerView, List list, ListAdapter listAdapter){
            LogUtil.logD(TAG,"[bindList]");
            if (recyclerView != null && list != null) {
                LogUtil.logD(TAG,"[bindList] 1");
                if (recyclerView.getAdapter() == null) {
                    LogUtil.logD(TAG,"[bindList] 2");
                    recyclerView.setAdapter(listAdapter);
                }
                listAdapter.submitList(list);
            }
        }
    }
}
