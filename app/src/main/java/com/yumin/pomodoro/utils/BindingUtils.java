package com.yumin.pomodoro.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.ui.blog.BlogRecyclerViewAdapter;

import java.util.List;

public final class BindingUtils {
    @BindingAdapter({"adapter"})
    public static void addBlogItems(RecyclerView recyclerView, List<Blog> blogs) {
        BlogRecyclerViewAdapter adapter = (BlogRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearItems();
            adapter.addItems(blogs);
        }
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).centerCrop().into(imageView);
    }
}
