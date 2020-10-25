package com.yumin.pomodoro.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.CountViewBindingImpl;
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
import com.yumin.pomodoro.utils.BaseBindingAdapter;
import com.yumin.pomodoro.utils.CountView;
import com.yumin.pomodoro.utils.CountViewItem;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
    ViewGroup viewGroup;
    RecyclerViewAdapter mRecyclerViewAdapter;
    List<CountViewItem> mCountViewItems = new ArrayList<>();

    public AddMissionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        mFragmentAddMissionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_mission, container, false);
        mAddMissionViewModel = new AddMissionViewModel(getActivity().getApplication(), mFragmentAddMissionBinding);
        mAddMissionEventHandler = new AddMissionEventHandler(mAddMissionViewModel);
        viewGroup = container;
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mAddMissionEventHandler.getCountViewListener(),
                mAddMissionEventHandler.getOnItemClickListener());
        initCountViewList();
        return mFragmentAddMissionBinding.getRoot();
    }

    private void initCountViewList() {
        mCountViewItems = new ArrayList<>();
        mCountViewItems.add(new CountViewItem(getContext(), "0", R.string.mission_time, 0, 0));
        mCountViewItems.add(new CountViewItem(getContext(), "0", R.string.mission_break, 0, 0));
        mCountViewItems.add(new CountViewItem(getContext(), "0", R.string.mission_goal, 0, 0));
        mCountViewItems.add(new CountViewItem(getContext(), "0", R.string.mission_repeat, 0, 0));
        mRecyclerViewAdapter.getItems().addAll(mCountViewItems);
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
        mFragmentAddMissionBinding.missionTitle.getText().clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.logD(TAG, "[onViewCreated]");
        // set title
        MainActivity.setToolbarTitle("add mission");
        // set click event
        mFragmentAddMissionBinding.setEventHandler(mAddMissionEventHandler);
        mFragmentAddMissionBinding.setLifecycleOwner(this);
        mFragmentAddMissionBinding.recyclerView.setAdapter(mRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mFragmentAddMissionBinding.recyclerView.setLayoutManager(linearLayoutManager);
    }

    public class RecyclerViewAdapter extends BaseBindingAdapter<CountViewItem, CountViewBindingImpl> {
        CountView.CountViewListener mCountViewListener;
        OnItemClickListener mOnItemClickListener;

        public RecyclerViewAdapter(Context context, CountView.CountViewListener countViewListener,
                                   OnItemClickListener onItemClickListener) {
            super(context);
            this.mCountViewListener = countViewListener;
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        protected int getLayoutResId(int viewType) {
            return R.layout.count_view;
        }

        @Override
        protected void onBindItem(CountViewBindingImpl binding, CountViewItem item) {
            binding.setCountViewItem(item);

            binding.addNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getItems().indexOf(item);
                    mCountViewListener.onAddButtonClick(v, position);
                }
            });

            binding.minusNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getItems().indexOf(item);
                    mCountViewListener.onMinusButtonClock(v, position);
                }
            });
        }

        @Override
        protected void onBindItemClickListener(RecyclerView.ViewHolder holder, int position) {
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
    }
}
