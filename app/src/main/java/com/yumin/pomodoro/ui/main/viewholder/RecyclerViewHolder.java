package com.yumin.pomodoro.ui.main.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    View mConvertView;
    SparseArray<View> mViews;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    public <T extends View> T getView(int viewID) {
        T view = (T) mViews.get(viewID);
        if (view == null) {
            view = mConvertView.findViewById(viewID);
            mViews.put(viewID,view);
        }
        return view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public void setClickListener(int viewID, View.OnClickListener clickListener){
        View childView = getView(viewID);
        childView.setOnClickListener(clickListener);
    }

    public void setText(int viewID, String text) {
        View childView = getView(viewID);
        if (childView instanceof TextView) {
            ((TextView) childView).setText(text);
        }
    }

    public void setImage(int viewID, Drawable drawable) {
        View childView = getView(viewID);
        if (childView instanceof ImageView) {
            ((ImageView) childView).setImageDrawable(drawable);
        }
    }

    public void setImageResource(int viewID, int resourceID) {
        View childView = getView(viewID);
        if (childView instanceof ImageView) {
            ((ImageView) childView).setImageResource(resourceID);
        }
    }

    public void setColorFilter(int viewID, int color) {
        View childView = getView(viewID);
        if (childView instanceof ImageView) {
            ((ImageView) childView).setColorFilter(color);
        }
    }

    public void clearColorFilter(int viewID) {
        View childView = getView(viewID);
        if (childView instanceof ImageView) {
            ((ImageView) childView).clearColorFilter();
        }
    }

    public void setEnable(int viewID, boolean enabled) {
        View childView = getView(viewID);
        childView.setEnabled(enabled);
    }

    public void setCheck(int viewID, boolean check) {
        View childView = getView(viewID);
        if (childView instanceof CheckBox) {
            ((CheckBox) childView).setChecked(check);
        }
    }

    public void setChildViewTag(int viewID, Object tag) {
        View childView = getView(viewID);
        childView.setTag(tag);
    }

    public void setVisibility(int viewID, int visibility){
        View childView  = getView(viewID);
        childView.setVisibility(visibility);
    }
}
