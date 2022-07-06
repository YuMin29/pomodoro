package com.yumin.pomodoro.ui.blog;

import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.Blog;

public class BlogItemViewModel {
    public Blog mBlogItem;
    public BlogItemViewModelListener mListener;
    public MutableLiveData<String> title = new MutableLiveData<>();
    public MutableLiveData<String> description = new MutableLiveData<>();
    public MutableLiveData<String> from = new MutableLiveData<>();
    public MutableLiveData<String> date = new MutableLiveData<>();
    public MutableLiveData<String> imageUrl = new MutableLiveData<>();

    public BlogItemViewModel(Blog item, BlogItemViewModelListener listener) {
        mBlogItem = item;
        mListener = listener;
        title.setValue(mBlogItem.getTitle());
        description.setValue(mBlogItem.getDescription());
        from.setValue(mBlogItem.getFrom());
        date.setValue(mBlogItem.getPublished_at());
        imageUrl.setValue(mBlogItem.getImg_url());
    }

    public void onItemClick(){
        mListener.onItemClick(mBlogItem.getBlog_url());
    }

    public interface BlogItemViewModelListener {
        void onItemClick(String blogUrl);
    }
}
