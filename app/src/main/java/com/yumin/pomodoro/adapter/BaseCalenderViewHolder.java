package com.yumin.pomodoro.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseCalenderViewHolder extends RecyclerView.ViewHolder {
    public BaseCalenderViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBind(int position);
}
