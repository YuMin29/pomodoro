package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.CategoryItemLayoutBinding;
import com.yumin.pomodoro.databinding.MissionItemLayoutBinding;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpandableBaseAdapter extends BaseExpandableListAdapter implements GroupIndex{
    private static final String TAG = "[ExpandableBaseAdapter]";
    protected List<Category> mDataList;
    protected List<UserMission> mCompletedMissions;
    protected List<MissionState> mMissionStates;
    private Context mContext;
    private OnExpandableItemClickListener onExpandableItemClickListener;
    private CustomizeItemBehavior mCustomizeItemBehavior;

    public ExpandableBaseAdapter(Context context, List<Category> list, List<UserMission> completedMissions) {
        mDataList = list;
        mContext = context;
        mCompletedMissions = completedMissions;
    }

    public interface OnExpandableItemClickListener {
        void onDelete(UserMission mission, int groupPosition, int childPosition);
        void onEdit(UserMission mission, int groupPosition, int childPosition);
    }

    public interface CustomizeItemBehavior {
        void onBindItemBehavior(MissionItemLayoutBinding binding, View view, int groupPosition, boolean isCompleted, UserMission userMission);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LogUtil.logE(TAG,"[getGroupView] convertView == null");
            groupViewHolder = new GroupViewHolder(DataBindingUtil.inflate(LayoutInflater.from(this.mContext), R.layout.category_item_layout, parent, false));
            convertView = groupViewHolder.getViewDataBinding().getRoot();
            convertView.setPadding(10, 10, 10, 10);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        LogUtil.logD(TAG,"[getGroupView] category name =  "
                +mDataList.get(groupPosition).getCategoryName() +" ,isExpanded"+isExpanded);

        CategoryItemLayoutBinding categoryItemLayoutBinding = (CategoryItemLayoutBinding) groupViewHolder.getViewDataBinding();
        categoryItemLayoutBinding.setCategory(mDataList.get(groupPosition));
        categoryItemLayoutBinding.categoryArrow.setImageResource(isExpanded ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_up_24);
        categoryItemLayoutBinding.executePendingBindings();
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder;
        if (convertView == null) {
            itemViewHolder = new ItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(this.mContext),R.layout.mission_item_layout,parent,false));
            convertView = itemViewHolder.getViewDataBinding().getRoot();
            convertView.setPadding(0, 0, 0, 20);
            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        UserMission userMission = mDataList.get(groupPosition).getMissionList().get(childPosition);
        LogUtil.logE(TAG,"[getChildView] mCompletedMissions size = "+ mCompletedMissions.size());
        boolean isCompleted = false;
        for (UserMission item : mCompletedMissions) {
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                LogUtil.logE(TAG,"[getChildView] ITEM getFirebaseMissionId = "+item.getFirebaseMissionId());
//                LogUtil.logE(TAG,"[getChildView] USER MISSION getFirebaseMissionId = "+userMission.getFirebaseMissionId());
//                if (userMission.getFirebaseMissionId().equals(item.getFirebaseMissionId()))
//                    isFinished = true;
//            } else {
            LogUtil.logE(TAG,"[getChildView] item id = "+item.getId() + " ,userMission id = "+userMission.getId());
            if (userMission.getId() == item.getId())
                isCompleted = true;
//            }
        }

        if (mMissionStates != null) {
            for (MissionState missionState : mMissionStates) {
                LogUtil.logE(TAG,"[getChildView] mMissionStates id = "+missionState.getMissionId() + " ,userMission id = "+userMission.getId());
                if (missionState.getMissionId().equals(String.valueOf(userMission.getId()))) {
                    LogUtil.logE(TAG,"[getChildView] missionState record day = "+TimeToMillisecondUtil.getDateString(missionState.getRecordDay()));
                    LogUtil.logE(TAG,"[getChildView] group day = "+mDataList.get(groupPosition).getCategoryName());
                    if (TimeToMillisecondUtil.getDateString(missionState.getRecordDay()).equals(mDataList.get(groupPosition).getCategoryName()))
                        isCompleted = missionState.getCompleted();
                }
            }
        }
        LogUtil.logE(TAG,"[getChildView] isCompleted = "+isCompleted);
        MissionItemLayoutBinding missionItemLayoutBinding = (MissionItemLayoutBinding) itemViewHolder.getViewDataBinding();
        missionItemLayoutBinding.setMission(userMission);
        missionItemLayoutBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onDelete(userMission, groupPosition, childPosition);
                }
            }
        });

        missionItemLayoutBinding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExpandableItemClickListener != null) {
                    onExpandableItemClickListener.onEdit(userMission, groupPosition, childPosition);
                }
            }
        });

        missionItemLayoutBinding.itemTime.setText(mContext.getString(R.string.mission_time) + Integer.toString(userMission.getTime()) + mContext.getString(R.string.minute));

        missionItemLayoutBinding.itemGoal.setText(mContext.getString(R.string.mission_goal) + Integer.toString(userMission.getGoal()));

        if (mCustomizeItemBehavior != null) {
            mCustomizeItemBehavior.onBindItemBehavior(missionItemLayoutBinding, convertView, groupPosition, isCompleted, userMission);
        } else {
            // gray out item when completed
            if (isCompleted && groupPosition == GROUP_TODAY_POSITION) {
                convertView.setAlpha(0.5f);
                missionItemLayoutBinding.colorView.setVisibility(View.INVISIBLE);
                missionItemLayoutBinding.finishedIcon.setVisibility(View.VISIBLE);
            } else {
                convertView.setAlpha(1f);
                missionItemLayoutBinding.colorView.setVisibility(View.VISIBLE);
                missionItemLayoutBinding.finishedIcon.setVisibility(View.INVISIBLE);
            }

            if (groupPosition == GROUP_COMING_POSITION) {
                missionItemLayoutBinding.itemOperateDay.setVisibility(View.VISIBLE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
                Date date = new Date(userMission.getOperateDay());
                String dateStr = simpleDateFormat.format(date);
                missionItemLayoutBinding.itemOperateDay.setText(mContext.getString(R.string.mission_operate_day) + dateStr);
            } else {
                missionItemLayoutBinding.itemOperateDay.setVisibility(View.GONE);
            }
        }
        missionItemLayoutBinding.executePendingBindings();
        return convertView;
    }

    public void setOnExpandableItemClickListener(OnExpandableItemClickListener onExpandableItemClickListener) {
        this.onExpandableItemClickListener = onExpandableItemClickListener;
    }

    public void updateCategory(List<Category> list) {
        this.mDataList = list;
        this.notifyDataSetChanged();
    }

    public void updateCompletedMission(List<UserMission> missions) {
        this.mCompletedMissions = missions;
        this.notifyDataSetChanged();
    }

    public void flashMissionState(List<MissionState> missionStates){
        this.mMissionStates = missionStates;
        this.notifyDataSetChanged();
    }

    public void setCustomizeItemBehavior(CustomizeItemBehavior customizeItemBehavior) {
        this.mCustomizeItemBehavior = customizeItemBehavior;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class GroupViewHolder{
        private ViewDataBinding mViewDataBinding;
        GroupViewHolder(ViewDataBinding viewDataBinding) {
            this.mViewDataBinding =viewDataBinding;
        }

        public ViewDataBinding getViewDataBinding() {
            return mViewDataBinding;
        }
    }

    class ItemViewHolder{
        private ViewDataBinding mViewDataBinding;
        ItemViewHolder(ViewDataBinding viewDataBinding) {
            this.mViewDataBinding =viewDataBinding;
        }
        ViewDataBinding getViewDataBinding() {
            return mViewDataBinding;
        }
    }
}
