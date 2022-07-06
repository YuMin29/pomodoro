package com.yumin.pomodoro.ui.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingActivity;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.databinding.ActivityBlogBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends DataBindingActivity implements BlogRecyclerViewAdapter.BlogAdapterListener{
    private static final String TAG = "[BlogActivity]";
    private BlogViewModel mBlogViewModel;
    private LinearLayoutManager mLayoutManager;
    private BlogRecyclerViewAdapter mBlogAdapter;
    private ActivityBlogBinding mActivityBlogBinding;

    public static Intent newIntent(Context context) {
        return new Intent(context, BlogActivity.class);
    }

    @Override
    protected void initViewModel() {
        mBlogViewModel = getActivityScopeViewModel(BlogViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.activity_blog, BR.blogViewModel, mBlogViewModel);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(this);
        mBlogAdapter = new BlogRecyclerViewAdapter(new ArrayList<>());
        mBlogAdapter.setListener(this);
        mActivityBlogBinding = (ActivityBlogBinding) getBinding();
        setUp();
        mBlogViewModel.getBlogList().observe(this, new Observer<List<Blog>>() {
            @Override
            public void onChanged(List<Blog> blogs) {
                for (Blog blog : blogs) {
                    LogUtil.logD(TAG,"[onChanged] blog = "+blog.getPublished_at());
                }
            }
        });

        setSupportActionBar(mActivityBlogBinding.toolbar);
        getSupportActionBar().setTitle("Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUp() {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mActivityBlogBinding.blogRecyclerView.setLayoutManager(mLayoutManager);
        mActivityBlogBinding.blogRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivityBlogBinding.blogRecyclerView.setAdapter(mBlogAdapter);
    }

    @Override
    public void onRetryClick() {
        mBlogViewModel.fetchBlog();
    }
}
