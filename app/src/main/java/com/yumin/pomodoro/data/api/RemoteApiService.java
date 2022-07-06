package com.yumin.pomodoro.data.api;


import com.yumin.pomodoro.data.Blog;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface RemoteApiService {
    static final String BASE_URL = "https://run.mocky.io/v3/";
    @GET("4931461d-4b75-4a02-abf3-e30a9b817f15")
    public Single<List<Blog>> getBlogs();
}
