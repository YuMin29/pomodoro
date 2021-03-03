package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.View;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.repository.firebase.UserMission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpandableViewAdapter extends ExpandableBaseAdapter<CategoryItemLayoutBinding, MissionItemLayoutBinding> implements GroupIndex{
    private static final String TAG = "[TestExpandableAdapter]";
    private OnExpandableItemClickListener onExpandableItemClickListener;
    private Context mContext;

    public interface OnExpandableItemClickListener {
        void onDelete(UserMission mission, int groupPosition, int childPosition);
        void onEdit(UserMission mission, int groupPosition, int childPosition);
    }

    public void setOnExpandableItemClickListener(OnExpandableItemClickListener onExpandableItemClickListener){
        this.onExpandableItemClickListener = onExpandableItemClickListener;
    }

    public ExpandableViewAdapter(List<Category> dataList, Context context){
        super(dataList,context);
        mContext = context;
    }

    public void flashCategory(List<Category> list) {
        if (list.isEmpty())
            LogUtil.logD(TAG,"[flashCategory] list empty");
        else
            LogUtil.logD(TAG,"[flashCategory] list size = "+list.size());
        this.mDataList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupLayout() {
        return R.layout.category_item_layout;
    }

    @Override
    public void onBindGroupLayout(CategoryItemLayoutBinding binding, Category category) {
        LogUtil.logD(TAG,"[onBindGroupLayout] CATEGORY NAME = "+category.getCategoryName());
        binding.setCategory(category);
    }

    @Override
    public int getChildLayout() {
        return R.layout.mission_item_layout;
    }

    @Override
    public void onBindChildLayout(MissionItemLayoutBinding binding, UserMission userMission, int groupPosition, int childPosition, View view) {
        LogUtil.logE(TAG,"[onBindChildLayout] groupPosition = "+groupPosition+" , childPosition = "+childPosition);
        binding.setMission(userMission);
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onDelete(userMission,groupPosition,childPosition);
                }
            }
        });

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onEdit(userMission,groupPosition,childPosition);
                }
            }
        });

        binding.itemTime.setText(mContext.getString(R.string.mission_time) +
                Integer.toString(userMission.getTime()) + mContext.getString(R.string.minute));

        binding.itemGoal.setText(mContext.getString(R.string.mission_goal) +
                Integer.toString(userMission.getGoal()));

        // TODO: 1/18/21  gray out this item and show check icon when finished
        // set delete line if finished
        if (groupPosition == GROUP_TODAY_POSITION && userMission.isFinished()) {
//            binding.itemName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
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
            binding.itemOperateDay.setText(mContext.getString(R.string.mission_operate_day)+dateStr);
        }  else {
            binding.itemOperateDay.setVisibility(View.GONE);
        }
    }
}
