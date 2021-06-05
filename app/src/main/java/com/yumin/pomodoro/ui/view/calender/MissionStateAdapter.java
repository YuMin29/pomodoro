package com.yumin.pomodoro.ui.view.calender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.ui.view.ColorView;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class MissionStateAdapter extends RecyclerView.Adapter<MissionStateAdapter.MissionStateViewHolder> {
    private static final String TAG = MissionStateAdapter.class.getSimpleName();
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
    public MissionStateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MissionStateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calender_mission_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MissionStateViewHolder holder, int position) {
        UserMission userMission = mUserMissionList.get(position);
        MissionState missionState = mMissionStateList.get(position);
        missionState.getNumberOfCompletion();
        holder.title.setText(userMission.getName());
        holder.content.setText(String.valueOf(userMission.getTime()));
        holder.colorView.setColorValue(userMission.getColor());
        holder.iconLinearLayout.removeAllViews();

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
            holder.iconLinearLayout.addView(tomatoIcon);
        }

        for (int index = 0; index < goal - completeOfNumber; index++) {
            ImageView tomatoIcon = new ImageView(mContext);
            tomatoIcon.setBackgroundResource(R.drawable.ic_tomato_grayscale);
            tomatoIcon.setLayoutParams(params);
            holder.iconLinearLayout.addView(tomatoIcon);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mUserMissionList || mUserMissionList.size() == 0)
            return 0;
        return mUserMissionList.size();
    }

    class MissionStateViewHolder extends RecyclerView.ViewHolder {
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
    }
}

