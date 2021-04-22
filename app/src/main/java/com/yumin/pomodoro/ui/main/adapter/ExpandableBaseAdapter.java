package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.model.Category;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.User;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

import java.util.List;

public abstract class ExpandableBaseAdapter<B extends ViewDataBinding, M extends ViewDataBinding > extends BaseExpandableListAdapter {
    private static final String TAG = "[ExpandableBaseAdapter]";
    protected List<Category> mDataList;
    protected List<UserMission> mFinishedMissions;
    protected List<MissionState> mMissionStates;
    private Context context;

    public ExpandableBaseAdapter(Context context, List<Category> list, List<UserMission> finishedMissions) {
        this.mDataList = list;
        this.context = context;
        this.mFinishedMissions = finishedMissions;
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
    public abstract void onBindGroupLayout(B binding,Category category,boolean isExpanded);

    public abstract int getChildLayout();
    public abstract void onBindChildLayout(M binding, UserMission userMission, int groupPosition, int childPosition, View view, boolean isFinished);

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        B binding;
        if (convertView == null) {
            LogUtil.logE(TAG,"[getGroupView] 000");
            binding = DataBindingUtil.inflate(LayoutInflater.from(this.context),getGroupLayout(), parent, false);
            convertView = binding.getRoot();
            convertView.setPadding(10, 10, 10, 10);
            convertView.setTag(binding);
        } else {
            LogUtil.logE(TAG,"[getGroupView] 111");
            binding = (B) convertView.getTag();
        }

        LogUtil.logD(TAG,"[getGroupView] category name =  "
                +mDataList.get(groupPosition).getCategoryName() +" ,isExpanded"+isExpanded);
        onBindGroupLayout(binding,mDataList.get(groupPosition),isExpanded);
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
        UserMission userMission = mDataList.get(groupPosition).getMissionList().get(childPosition);
        LogUtil.logE(TAG,"[getChildView] mFinishedMissions size = "+mFinishedMissions.size());
        boolean isFinished = false;
        for (UserMission item : mFinishedMissions) {
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                LogUtil.logE(TAG,"[getChildView] ITEM getFirebaseMissionId = "+item.getFirebaseMissionId());
//                LogUtil.logE(TAG,"[getChildView] USER MISSION getFirebaseMissionId = "+userMission.getFirebaseMissionId());
//                if (userMission.getFirebaseMissionId().equals(item.getFirebaseMissionId()))
//                    isFinished = true;
//            } else {
            LogUtil.logE(TAG,"[getChildView] ITEM ID = "+item.getId());
            LogUtil.logE(TAG,"[getChildView] USER MISSION ID = "+userMission.getId());
            if (userMission.getId() == item.getId())
                isFinished = true;
//            }
        }

        if (null != mMissionStates) {
            for (MissionState missionState : mMissionStates) {
                LogUtil.logE(TAG,"[getChildView] missionState ID = "+missionState.getMissionId());
                LogUtil.logE(TAG,"[getChildView] userMission ID = "+userMission.getId());
                if (missionState.getMissionId().equals(String.valueOf(userMission.getId()))) {
                    LogUtil.logE(TAG,"[getChildView] missionState record day = "+TimeToMillisecondUtil.getDateString(missionState.getRecordDay()));
                    LogUtil.logE(TAG,"[getChildView] group day = "+mDataList.get(groupPosition).getCategoryName());
                    if (TimeToMillisecondUtil.getDateString(missionState.getRecordDay()).equals(mDataList.get(groupPosition).getCategoryName()))
                        isFinished = missionState.getFinished();
                }
            }
        }

        LogUtil.logE(TAG,"[getChildView] mFinishedMissions isFinished = "+isFinished);
        onBindChildLayout(binding,userMission,groupPosition,childPosition,convertView,isFinished);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
