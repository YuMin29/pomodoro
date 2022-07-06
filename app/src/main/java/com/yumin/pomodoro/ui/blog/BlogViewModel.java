package com.yumin.pomodoro.ui.blog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.data.repository.remote.RemoteApiManager;
import com.yumin.pomodoro.data.repository.remote.RemoteRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BlogViewModel extends AndroidViewModel {
    private RemoteRepository remoteApiManager;
    private MutableLiveData<List<Blog>> blogContent = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public BlogViewModel(@NonNull Application application) {
        super(application);
        remoteApiManager = new RemoteRepository();
        fetchBlog();
    }

    public void fetchBlog(){
        isLoading.postValue(true);
        remoteApiManager
                .getBlogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.logD("[BlogViewModel]","[onSubscribe]");
                    }

                    @Override
                    public void onSuccess(Object o) {
                        LogUtil.logD("[BlogViewModel]","[onSuccess]");
                        blogContent.postValue((List<Blog>) o);
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.logD("[BlogViewModel]","[onError] error = "+e.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public LiveData<List<Blog>> getBlogList() {
        return blogContent;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }
}
