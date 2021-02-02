package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.LoginViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ApiService apiService;
    private Application application;

    public ViewModelFactory(Application application, ApiService apiService){
        this.apiService = apiService;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AddMissionViewModel.class) {
            return (T) new AddMissionViewModel(application,new RoomRepository(apiService));
        } else if (modelClass == HomeViewModel.class) {
            return (T) new HomeViewModel(new RoomRepository(apiService));
        } else if (modelClass == EditMissionViewModel.class) {
            return (T) new EditMissionViewModel(application,new RoomRepository(apiService));
        } else if (modelClass == TimerViewModel.class) {
            return (T) new TimerViewModel(application,new RoomRepository(apiService));
        } else if (modelClass == RangeCalenderViewModel.class) {
            return (T) new RangeCalenderViewModel(application,new RoomRepository(apiService));
        } else if (modelClass == LoginViewModel.class) {
            return (T) new LoginViewModel(application);
        }
        return null;
    }
}
