package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionEventHandler;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.adapter.BaseAdapter;
import com.yumin.pomodoro.data.model.MissionItem;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
    ViewGroup viewGroup;
    RecyclerViewAdapter mRecyclerViewAdapter;

    public AddMissionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        View view = inflater.inflate(R.layout.fragment_add_mission,container,true);
        mAddMissionViewModel = new AddMissionViewModel(getActivity().getApplication());
        mAddMissionEventHandler = new AddMissionEventHandler(mAddMissionViewModel,getContext());
        viewGroup = container;
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mAddMissionEventHandler.getMissionItemListener(),
                mAddMissionEventHandler.getOnItemClickListener(), mAddMissionViewModel);
        observeCountViewList();
        return view;
    }

    private void observeCountViewList() {
        mRecyclerViewAdapter.getItems().addAll(mAddMissionViewModel.getCountViewList());

        mAddMissionViewModel.countViewItemList.observe(getViewLifecycleOwner(), new Observer<List<MissionItem>>() {
            @Override
            public void onChanged(List<MissionItem> missionItems) {
                LogUtil.logD(TAG, "[observeCountViewList]");
                mRecyclerViewAdapter.notifyItemRangeChanged(0, missionItems.size());
            }
        });
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
        // set title
        MainActivity.setToolbarTitle("Add mission");
//        mFragmentAddMissionBinding.setEventHandler(mAddMissionEventHandler);
//        mFragmentAddMissionBinding.setLifecycleOwner(this);
//        mFragmentAddMissionBinding.recyclerView.setAdapter(mRecyclerViewAdapter);
//        ((SimpleItemAnimator) mFragmentAddMissionBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),6);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//              if (position >= 8 && position < 11)
//                return 2;
//              else if ((position > 0 && position < 3) || (position >= 3 && position < 7))
//                  return 3;
//              else
//                  return 6;
//            }
//        });
//
//        mFragmentAddMissionBinding.recyclerView.setLayoutManager(gridLayoutManager);
    }

    public class RecyclerViewAdapter extends BaseAdapter<MissionItem> {
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
        protected void onBindItem(MissionItem item) {

        }

//        @Override
//        protected void onBindItem(MissionItemViewBindingImpl binding, MissionItem item) {
//            binding.setMissionItem(item);
//            binding.setViewModel(addMissionViewModel);
//
//            binding.addNum.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getItems().indexOf(item);
//                    mMissionItemListener.onAddButtonClick(v, position);
//                }
//            });
//
//            binding.minusNum.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getItems().indexOf(item);
//                    mMissionItemListener.onMinusButtonClock(v, position);
//                }
//            });
//            binding.setIsContentEmpty(item.isContentEmpty());
//        }

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
