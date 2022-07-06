package com.yumin.pomodoro.ui.blog;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.databinding.ItemBlogEmptyViewBinding;
import com.yumin.pomodoro.databinding.ItemBlogViewBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class BlogRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "[BlogRecyclerViewAdapter]";
    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    private List<Blog> mBlogList = new ArrayList<>();
    private BlogAdapterListener mListener;


    public BlogRecyclerViewAdapter(List<Blog> blogList){
        this.mBlogList = blogList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemBlogViewBinding itemBlogViewBinding = ItemBlogViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,false);
                return new ItemViewHolder(itemBlogViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemBlogEmptyViewBinding itemBlogEmptyViewBinding = ItemBlogEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,false);
                return new EmptyItemViewHolder(itemBlogEmptyViewBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mBlogList != null && !mBlogList.isEmpty())
            return VIEW_TYPE_NORMAL;
        else
            return VIEW_TYPE_EMPTY;
    }

    @Override
    public int getItemCount() {
        if (mBlogList != null && mBlogList.size() > 0)
            return mBlogList.size();
        else
            return 1;
    }

    public void addItems(List<Blog> blogList) {
        if (blogList == null)
            return;
        mBlogList.addAll(blogList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mBlogList.clear();
    }

    public void setListener(BlogAdapterListener listener) {
        this.mListener = listener;
    }

    public interface BlogAdapterListener {
        void onRetryClick();
    }

    public class ItemViewHolder extends BaseViewHolder implements BlogItemViewModel.BlogItemViewModelListener{
        private ItemBlogViewBinding mViewBinding;
        private BlogItemViewModel mBlogItemViewModel;

        public ItemViewHolder(@NonNull ItemBlogViewBinding binding) {
            super(binding.getRoot());
            this.mViewBinding = binding;
        }


        @Override
        public void onBind(int position) {
            final Blog blogItem = mBlogList.get(position);
            mBlogItemViewModel = new BlogItemViewModel(blogItem,this);
            mViewBinding.setViewModel(mBlogItemViewModel);

            mViewBinding.executePendingBindings();
        }

        @Override
        public void onItemClick(String blogUrl) {
            if (blogUrl != null) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(blogUrl));
                    itemView.getContext().startActivity(intent);
                } catch (Exception e) {
                    LogUtil.logD(TAG,"url error : "+e.getMessage());
                }
            }
        }
    }

    public class EmptyItemViewHolder extends BaseViewHolder implements BlogEmptyViewModel.BlogEmptyViewModelListener{
        private ItemBlogEmptyViewBinding mBinding;

        public EmptyItemViewHolder(@NonNull ItemBlogEmptyViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            BlogEmptyViewModel blogEmptyViewModel = new BlogEmptyViewModel(this);
            mBinding.setViewModel(blogEmptyViewModel);
        }

        @Override
        public void onRetryClick() {
            mListener.onRetryClick();
        }
    }
}
