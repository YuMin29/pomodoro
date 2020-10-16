package com.yumin.pomodoro.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.MainActivity;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
import com.yumin.pomodoro.utils.CountView;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AddMissionFragment extends Fragment {
    private static final String TAG = "[AddMissionFragment]";
    FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    AddMissionViewModel mAddMissionViewModel;
    AddMissionEventHandler mAddMissionEventHandler;
    ViewGroup viewGroup;
    private CountView mTimeCountView = new CountView(getContext(),0,0,"0",R.string.mission_time);
    private CountView mBreakCountView = new CountView(getContext(),0,0,"0",R.string.mission_break);
    private CountView mRepeatCountView = new CountView(getContext(),0,0,"0",R.string.mission_repeat);
    private CountView mGoalCountView = new CountView(getContext(),0,0,"0",R.string.mission_goal);

    public AddMissionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logD(TAG, "[onCreateView]");
        mAddMissionViewModel = new ViewModelProvider(requireActivity()).get(AddMissionViewModel.class);
        mAddMissionViewModel.init();
        mAddMissionEventHandler = new AddMissionEventHandler(mAddMissionViewModel);
        mFragmentAddMissionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_mission, container, false);
        viewGroup = container;
        initCountViewList();
        return mFragmentAddMissionBinding.getRoot();
    }

    private void initCountViewList(){
        List<CountView> countViewList = new ArrayList<>();
        countViewList.add(mTimeCountView);
        countViewList.add(mBreakCountView);
        countViewList.add(mRepeatCountView);
        countViewList.add(mGoalCountView);
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
        // set title
        MainActivity.setToolbarTitle("add mission");
        // set click event
        mFragmentAddMissionBinding.setViewModel(mAddMissionViewModel);
        mFragmentAddMissionBinding.setEventHandler(mAddMissionEventHandler);
        mFragmentAddMissionBinding.setLifecycleOwner(this);
//        mFragmentAddMissionBinding.missionTime.setListener(mAddMissionEventHandler);
        mAddMissionViewModel.init();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private Context mContext;

        public RecyclerViewAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.count_view, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.addButton = view.findViewById(R.id.add_num);
            viewHolder.minusButton = view.findViewById(R.id.minus_num);
            viewHolder.numTextView = view.findViewById(R.id.num_textview);
            viewHolder.descTextView = view.findViewById(R.id.description_textview);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            Button addButton;
            TextView numTextView;
            TextView descTextView;
            Button minusButton;
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
