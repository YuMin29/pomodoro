package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.ui.main.viewholder.RecyclerViewHolder;

import java.util.ArrayList;

public abstract class RecyclerViewBaseAdapter<M> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private static final String TAG = "[BaseBindingAdapter]";
    protected Context context;
    protected ArrayList<M> items;

    public RecyclerViewBaseAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public ArrayList<M> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    protected void onChanged(ArrayList<M> newItems) {
        resetItems(newItems);
        notifyDataSetChanged();
    }

    protected void resetItems(ArrayList<M> newItems) {
        this.items = newItems;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( this.getLayoutResId(viewType), parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        this.onBindItemClickListener(holder, position);
        this.setContent(holder,items,position);
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);
    public abstract void setContent(RecyclerViewHolder holder, ArrayList<M> items, int position);
    protected abstract void onBindItemClickListener(RecyclerViewHolder holder, int position);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
