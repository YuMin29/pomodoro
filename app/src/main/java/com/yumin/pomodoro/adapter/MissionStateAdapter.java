package com.yumin.pomodoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.customize.ColorView;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class MissionStateAdapter extends RecyclerView.Adapter<BaseCalenderViewHolder> {
    private static final String TAG = MissionStateAdapter.class.getSimpleName();
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_EMPTY = 1;
    private List<UserMission> mUserMissionList;
    private List<MissionState> mMissionStateList;
    private Context mContext;

    public MissionStateAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(List<UserMission> userMissionList, List<MissionState> missionStateList) {
        mUserMissionList = userMissionList;
        mMissionStateList = missionStateList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseCalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL:
                LogUtil.logD(TAG,"[onCreateViewHolder] TYPE_NORMAL");
                return new MissionStateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calender_mission_item,
                        parent, false));
            case TYPE_EMPTY:
            default:
                LogUtil.logD(TAG,"[onCreateViewHolder] TYPE_EMPTY");
                return new MissionEmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calender_empty_view,
                        parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseCalenderViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mUserMissionList != null && !mUserMissionList.isEmpty())
            return TYPE_NORMAL;
        else
            return TYPE_EMPTY;
    }

    @Override
    public int getItemCount() {
        if (null == mUserMissionList || mUserMissionList.size() == 0) {
            LogUtil.logD(TAG,"[getItemCount] 1");
            return 1;
        } else {
            LogUtil.logD(TAG,"[getItemCount] mUserMissionList.size() = "+mUserMissionList.size());
            return mUserMissionList.size();
        }
    }

    class MissionStateViewHolder extends BaseCalenderViewHolder {
        TextView title;
        TextView content;
        ColorView colorView;
        LinearLayout iconLinearLayout;

        public MissionStateViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            iconLinearLayout = itemView.findViewById(R.id.linearLayout_tomato);
        }

        @Override
        public void onBind(int position) {
            UserMission userMission = mUserMissionList.get(position);
            MissionState missionState = mMissionStateList.get(position);
            missionState.getNumberOfCompletion();
            title.setText(userMission.getName());
            content.setText(String.valueOf(userMission.getTime()));
            colorView.setColorValue(userMission.getColor());
            iconLinearLayout.removeAllViews();

            int completeOfNumber = missionState.getNumberOfCompletion();
            int goal = userMission.getGoal();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 18, 0);

            for (int index = 0; index < completeOfNumber; index++) {
                ImageView tomatoIcon = new ImageView(mContext);
                tomatoIcon.setBackgroundResource(R.drawable.ic_tomato_colorful);
                tomatoIcon.setLayoutParams(params);
                iconLinearLayout.addView(tomatoIcon);
            }

            for (int index = 0; index < goal - completeOfNumber; index++) {
                ImageView tomatoIcon = new ImageView(mContext);
                tomatoIcon.setBackgroundResource(R.drawable.ic_tomato_grayscale);
                tomatoIcon.setLayoutParams(params);
                iconLinearLayout.addView(tomatoIcon);
            }
        }
    }

    class MissionEmptyViewHolder extends BaseCalenderViewHolder{

        public MissionEmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(int position) {

        }
    }
}

