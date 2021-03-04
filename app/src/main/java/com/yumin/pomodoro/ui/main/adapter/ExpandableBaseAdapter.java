package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public abstract class ExpandableBaseAdapter<B extends ViewDataBinding, M extends ViewDataBinding > extends BaseExpandableListAdapter {
    private static final String TAG = "[ExpandableBaseAdapter]";
    protected List<Category> mDataList;
    private Context context;

    public ExpandableBaseAdapter(List<Category> list, Context context) {
        this.mDataList = list;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return mDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDataList.get(groupPosition).getMissionList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDataList.get(groupPosition).getMissionList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public abstract int getGroupLayout();
    public abstract void onBindGroupLayout(B binding,Category category);

    public abstract int getChildLayout();
    public abstract void onBindChildLayout(M binding, UserMission userMission, int groupPosition, int childPosition, View view);

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(this.context),
                getGroupLayout(), parent, false);
        if (convertView == null) {
            convertView = binding.getRoot();
            convertView.setPadding(10, 10, 10, 10);
            convertView.setTag(binding);
        } else {
            binding = (B) convertView.getTag();
        }

        LogUtil.logD(TAG,"[getGroupView] category name =  "
                +mDataList.get(groupPosition).getCategoryName());
        onBindGroupLayout(binding,mDataList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        M binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(this.context),getChildLayout(),parent,false);
            convertView = binding.getRoot();
            convertView.setPadding(0, 0, 0, 20);
            convertView.setTag(binding);
        } else {
            binding = (M) convertView.getTag();
        }
        onBindChildLayout(binding,mDataList.get(groupPosition).getMissionList().get(childPosition),groupPosition,childPosition,convertView);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
