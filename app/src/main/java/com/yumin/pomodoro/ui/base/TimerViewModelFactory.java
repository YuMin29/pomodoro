package com.yumin.pomodoro.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class TimerViewModelFactory implements ViewModelProvider.Factory {
    private ApiHelper apiHelper;
    private Application application;
    private int itemId;

    public TimerViewModelFactory(Application application, ApiHelper apiHelper, int itemId) {
        this.apiHelper = apiHelper;
        this.application = application;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TimerViewModel(application,new MainRepository(apiHelper),itemId);
    }
}
