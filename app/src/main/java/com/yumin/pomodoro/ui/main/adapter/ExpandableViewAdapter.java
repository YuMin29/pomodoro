package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class ExpandableViewAdapter extends ExpandableBaseAdapter<CategoryItemLayoutBinding, MissionItemLayoutBinding> {
    private static final String TAG = "[TestExpandableAdapter]";
    private OnClickListenerEditOrDelete onClickListenerEditOrDelete;

    public interface OnClickListenerEditOrDelete{
        void OnClickListenerDelete(Mission mission,int groupPosition,int childPosition);
    }

    public void setOnClickListenerEditOrDelete(OnClickListenerEditOrDelete onClickListenerEditOrDelete){
        this.onClickListenerEditOrDelete = onClickListenerEditOrDelete;
    }

    public ExpandableViewAdapter(List<Category> dataList, Context context){
        super(dataList,context);
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
        LogUtil.logD(TAG,"[onBindGroupLayout] CATREGORY NAME = "+category.getCategoryName());
        binding.setCategory(category);
    }

    @Override
    public int getChildLayout() {
        return R.layout.mission_item_layout;
    }

    @Override
    public void onBindChildLayout(MissionItemLayoutBinding binding, Mission mission,int groupPosition, int childPosition) {
        binding.setMission(mission);
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerEditOrDelete != null) {
                    onClickListenerEditOrDelete.OnClickListenerDelete(mission,groupPosition,childPosition);
                }
            }
        });

        // set delete line if finished
        if (mission.isFinished()) {
            binding.itemName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }
    }
}
