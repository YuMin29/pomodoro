package com.yumin.pomodoro.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.Mission;

import java.util.List;

public class MissionAdapter extends BaseAdapter {
    List<Mission> missions = null;
    LayoutInflater layoutInflater = null;

    public MissionAdapter(List<Mission> missions, Context context){
        Log.d("[Stella]","MissionAdapter");
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(context);
        this.missions = missions;
    }

    public void updateData(List<Mission> missions){
        Log.d("[Stella]","updateData");
        this.missions = missions;
    }

    @Override
    public int getCount() {
        return missions.size();
    }

    @Override
    public Object getItem(int position) {
        return missions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.d("[Stella]","getView");
        MissionViewHolder missionViewHolder = null;
        if (view == null) {
            missionViewHolder = new MissionViewHolder();
            view = layoutInflater.inflate(R.layout.mission_item_layout,viewGroup,false);
            missionViewHolder.itemName = view.findViewById(R.id.item_name);
            missionViewHolder.itemDay  = view.findViewById(R.id.item_day);
            missionViewHolder.itemType  = view.findViewById(R.id.item_type);
            view.setTag(missionViewHolder);

        } else {
            missionViewHolder = (MissionViewHolder) view.getTag();
        }

        Mission mission = (Mission) getItem(position);

        if (mission != null) {
            Log.d("[Stella]","mission is not null, name = "+mission.getName());
            missionViewHolder.itemName.setText(mission.getName());
        } else
            Log.d("[Stella]","mission is null");

        return view;
    }
}
