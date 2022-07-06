package com.yumin.pomodoro.data.repository.remote;

import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.data.api.RemoteApiService;

import java.util.List;

import io.reactivex.Single;

public class RemoteRepository {
    private RemoteApiService remoteApiService;

    public RemoteRepository(){
        remoteApiService = RemoteApiManager.newInstance().creat(RemoteApiService.class);
    }

    public Single<List<Blog>> getBlogs(){
        return remoteApiService.getBlogs();
    }
}
