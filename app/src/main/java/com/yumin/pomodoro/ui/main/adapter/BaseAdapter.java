package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseAdapter<M> extends RecyclerView.Adapter {
    private static final String TAG = "[BaseBindingAdapter]";
    protected Context context;
    protected ArrayList<M> items;

    public BaseAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public ArrayList<M> getItems() {
        return items;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate( this.getLayoutResId(viewType), parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.onBindItemClickListener(holder, position);
        this.onBindItem(this.items.get(position));
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    protected abstract void onBindItem(M item);

    protected abstract void onBindItemClickListener(RecyclerView.ViewHolder holder, int position);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
