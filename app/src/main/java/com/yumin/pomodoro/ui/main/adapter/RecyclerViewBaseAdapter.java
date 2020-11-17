package com.yumin.pomodoro.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.ui.main.viewholder.RecyclerViewHolder;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewBaseAdapter<M,B extends ViewDataBinding> extends ListAdapter<M,RecyclerView.ViewHolder> {
    private static final String TAG = "[BaseBindingAdapter]";
    protected Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    protected RecyclerViewBaseAdapter(@NonNull DiffUtil.ItemCallback<M> diffCallback,Context context) {
        super(diffCallback);
        this.context = context;
    }

    @Override
    public void submitList(@Nullable List<M> list) {
        LogUtil.logD(TAG,"[submitList] list SIZE = "+list.size());
        super.submitList(list, () -> {
            super.submitList(list == null ? new ArrayList<>() : new ArrayList<>(list));
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), this.getLayoutResId(viewType), parent, false);
        BaseBindingViewHolder holder = new BaseBindingViewHolder(binding.getRoot());
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                int position = holder.getAdapterPosition();
                mOnItemClickListener.onItemClick(binding,getItem(position), position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                int position = holder.getAdapterPosition();
                mOnItemLongClickListener.onItemLongClick(binding,getItem(position), position);
                return true;
            }
            return false;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        B binding = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(binding, getItem(position), holder);
        if (binding != null) {
            binding.executePendingBindings();
        }
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);
    protected abstract void onBindItem(B binding, M item, RecyclerView.ViewHolder holder);

    public static class BaseBindingViewHolder extends RecyclerView.ViewHolder {
        BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<M,B> {
        void onItemClick(B binding,M item, int position);
    }

    public interface OnItemLongClickListener<M,B> {
        void onItemLongClick(B binding,M item, int position);
    }
}
