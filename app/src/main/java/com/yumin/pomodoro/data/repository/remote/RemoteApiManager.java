package com.yumin.pomodoro.data.repository.remote;

import com.yumin.pomodoro.data.Blog;
import com.yumin.pomodoro.data.api.RemoteApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteApiManager {
    private static Retrofit mRetrofit;
    private static RemoteApiManager retrofitManager;

    //提供共有的方法供外界訪問,singleton
    public static RemoteApiManager newInstance() {
        if (retrofitManager == null) {
            synchronized (RemoteApiManager.class) {
                retrofitManager = new RemoteApiManager();
            }
        }
        return retrofitManager;
    }

    //構造方法私有化
    private RemoteApiManager() {
        mRetrofit = getRetrofit();
    }

    //構建Ok請求
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
    }

    //通過動態代理生成相應的Http請求
    public <T> T creat(Class<T> t) {
        return mRetrofit.create(t);
    }

    //構建Retrofit
    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RemoteApiService.BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
