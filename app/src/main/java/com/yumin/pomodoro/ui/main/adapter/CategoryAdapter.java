package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.ui.main.viewmodel.MissionViewHolder;
import com.yumin.pomodoro.ui.main.viewholder.CategoryViewHolder;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class CategoryAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "[CategoryAdapter]";
    private Context context;
    private LayoutInflater mInflater;
    private List<Category> mCategoryList;

    public CategoryAdapter(Context context, List<Category> datas) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mCategoryList = datas;
    }

    public void flashCategory(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mCategoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCategoryList.get(groupPosition).getMissionList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategoryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCategoryList.get(groupPosition).getMissionList().get(childPosition);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CategoryViewHolder categoryViewHolder;
        if (convertView == null) {
            categoryViewHolder = new CategoryViewHolder();
            convertView = mInflater.inflate(R.layout.category_item_layout, parent, false);
            convertView.setPadding(10, 10, 10, 10);
            categoryViewHolder.categoryName = (TextView) convertView.findViewById(R.id.category_name_text_view);
            convertView.setTag(categoryViewHolder);
        } else {
            categoryViewHolder = (CategoryViewHolder) convertView.getTag();
        }

        Log.d(TAG,"[getGroupView] category name =  "
                +mCategoryList.get(groupPosition).getCategoryName());

        categoryViewHolder.categoryName.setText(mCategoryList.get(groupPosition).getCategoryName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MissionViewHolder missionViewHolder;
        if (convertView == null) {
            missionViewHolder = new MissionViewHolder();
            convertView = mInflater.inflate(R.layout.mission_item_layout, parent, false);
            convertView.setPadding(0, 0, 0, 20);
//            missionViewHolder.itemName = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(missionViewHolder);
        } else {
            missionViewHolder = (MissionViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logD(TAG,"convert view click! childPosition = "+childPosition );
            }
        });

//        if (childPosition != 0)
//            missionViewHolder.itemName.setText(mCategoryList.get(groupPosition).getMissionList().get(childPosition).getName());

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
