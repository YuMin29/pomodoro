package com.yumin.pomodoro.ui.blog;

public class BlogEmptyViewModel {
    private BlogEmptyViewModelListener mListener;

    public BlogEmptyViewModel(BlogEmptyViewModelListener mListener) {
        this.mListener = mListener;
    }

    public void onRetryClick(){
        mListener.onRetryClick();
    }

    public interface BlogEmptyViewModelListener {
        public void onRetryClick();
    }
}
