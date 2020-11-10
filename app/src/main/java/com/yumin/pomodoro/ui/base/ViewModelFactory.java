package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ApiHelper apiHelper;
    private Application application;

    public ViewModelFactory(Application application, ApiHelper apiHelper){
        this.apiHelper = apiHelper;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AddMissionViewModel.class) {
            return (T) new AddMissionViewModel(application,new MainRepository(apiHelper));
        }
        return null;
    }
}
