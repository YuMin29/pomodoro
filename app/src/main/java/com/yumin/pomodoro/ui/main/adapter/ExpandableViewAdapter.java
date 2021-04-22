package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpandableViewAdapter extends ExpandableBaseAdapter<CategoryItemLayoutBinding, MissionItemLayoutBinding> implements GroupIndex {
    private static final String TAG = "[ExpandableViewAdapter]";
    private OnExpandableItemClickListener onExpandableItemClickListener;
    private OnBindView onBindView;
    private Context mContext;

    public interface OnExpandableItemClickListener {
        void onDelete(UserMission mission, int groupPosition, int childPosition);

        void onEdit(UserMission mission, int groupPosition, int childPosition);
    }

    public interface OnBindView {
        void onBindItem(MissionItemLayoutBinding binding, View view, int groupPosition, boolean isFinished, UserMission userMission);
    }

    public void setOnExpandableItemClickListener(OnExpandableItemClickListener onExpandableItemClickListener) {
        this.onExpandableItemClickListener = onExpandableItemClickListener;
    }

    public void setOnBindView(OnBindView onBindView) {
        this.onBindView = onBindView;
    }

    public ExpandableViewAdapter(Context context, List<Category> dataList, List<UserMission> finishedMissions) {
        super(context, dataList, finishedMissions);
        mContext = context;
    }

    public void flashCategory(List<Category> list) {
        if (list.isEmpty())
            LogUtil.logD(TAG, "[flashCategory] list empty");
        else
            LogUtil.logD(TAG, "[flashCategory] list size = " + list.size());
        this.mDataList = list;
        this.notifyDataSetChanged();
    }

    public void flashFinishedMission(List<UserMission> missions) {
        this.mFinishedMissions = missions;
        this.notifyDataSetChanged();
    }

    public void flashMissionState(List<MissionState> missionStates){
        this.mMissionStates = missionStates;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupLayout() {
        return R.layout.category_item_layout;
    }

    @Override
    public void onBindGroupLayout(CategoryItemLayoutBinding binding, Category category, boolean isExpanded) {
        LogUtil.logD(TAG, "[onBindGroupLayout] CATEGORY NAME = " + category.getCategoryName());
        binding.setCategory(category);
        binding.categoryArrow.setImageResource(isExpanded ? R.drawable.ic_baseline_keyboard_arrow_down_24 :
                R.drawable.ic_baseline_keyboard_arrow_up_24);
    }

    private Animation arrowAnimation(int fromDegrees, int toDegrees) {
        Animation animation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(500);
        return animation;
    }

    @Override
    public int getChildLayout() {
        return R.layout.mission_item_layout;
    }

    @Override
    public void onBindChildLayout(MissionItemLayoutBinding binding, UserMission userMission, int groupPosition, int childPosition, View view, boolean isFinished) {
        LogUtil.logE(TAG, "[onBindChildLayout] groupPosition = " + groupPosition + " , childPosition = " + childPosition + " ,isFinished = " + isFinished);
        binding.setMission(userMission);
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onDelete(userMission, groupPosition, childPosition);
                }
            }
        });

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onEdit(userMission, groupPosition, childPosition);
                }
            }
        });

        binding.itemTime.setText(mContext.getString(R.string.mission_time) + Integer.toString(userMission.getTime()) + mContext.getString(R.string.minute));

        binding.itemGoal.setText(mContext.getString(R.string.mission_goal) + Integer.toString(userMission.getGoal()));

        if (onBindView != null) {
            onBindView.onBindItem(binding, view, groupPosition, isFinished, userMission);
        } else {
            // gray out item if it finished
            if (isFinished && groupPosition == GROUP_TODAY_POSITION) {
                view.setAlpha(0.5f); // set opacity
                binding.colorView.setVisibility(View.INVISIBLE);
                binding.finishedIcon.setVisibility(View.VISIBLE);
            } else {
                view.setAlpha(1f); // set opacity
                binding.colorView.setVisibility(View.VISIBLE);
                binding.finishedIcon.setVisibility(View.INVISIBLE);
            }

            if (groupPosition == GROUP_COMING_POSITION) {
                binding.itemOperateDay.setVisibility(View.VISIBLE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
                Date date = new Date(userMission.getOperateDay());
                String dateStr = simpleDateFormat.format(date);
                binding.itemOperateDay.setText(mContext.getString(R.string.mission_operate_day) + dateStr);
            } else {
                binding.itemOperateDay.setVisibility(View.GONE);
            }
        }
    }
}
