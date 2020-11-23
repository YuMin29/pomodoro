package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class TestExpandableAdapter extends ExpandableBaseAdapter<CategoryItemLayoutBinding, MissionItemLayoutBinding>{
    private static final String TAG = "[TestExpandableAdapter]";

    public TestExpandableAdapter(List<Category> dataList,Context context){
        super(dataList,context);
    }

    public void flashCategory(List<Category> list) {
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
    public void onBindChildLayout(MissionItemLayoutBinding binding, Mission mission) {
        binding.setMission(mission);
    }
}
