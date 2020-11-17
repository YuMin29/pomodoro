package com.yumin.pomodoro.ui.main.bindingadapter;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class RecyclerViewBindingAdapter {
    private final static String TAG = "[RecyclerViewBindingAdapter]";

    @BindingAdapter(value = {"adapter","submitList"} , requireAll = false)
    public static void bindList(RecyclerView recyclerView, ListAdapter listAdapter, List list){
        LogUtil.logD(TAG,"[bindList]");
        if (recyclerView != null && list != null) {
            LogUtil.logD(TAG,"[bindList] 1, list size = "+list.size());
            if (recyclerView.getAdapter() == null) {
                LogUtil.logD(TAG,"[bindList] 2");

                recyclerView.setAdapter(listAdapter);
            }
            listAdapter.submitList(list);
        }
    }
}
