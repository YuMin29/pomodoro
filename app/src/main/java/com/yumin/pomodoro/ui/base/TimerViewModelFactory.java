package com.yumin.pomodoro.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class TimerViewModelFactory implements ViewModelProvider.Factory {
    private ApiService apiService;
    private Application application;
    private int itemId;

    public TimerViewModelFactory(Application application, ApiService apiService, int itemId) {
        this.apiService = apiService;
        this.application = application;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TimerViewModel(application,new RoomRepository(apiService));
    }
}
