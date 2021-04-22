package com.yumin.pomodoro.ui.main.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.CalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.LoginViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.SettingsViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public ViewModelFactory(Application application){
        this.application = application;
    }

    // TODO: 2021/3/24 create a repository injector for create view model

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AddMissionViewModel.class) {
            return (T) new AddMissionViewModel(application);
        } else if (modelClass == HomeViewModel.class) {
            return (T) new HomeViewModel(application);
        } else if (modelClass == EditMissionViewModel.class) {
            return (T) new EditMissionViewModel(application);
        } else if (modelClass == TimerViewModel.class) {
            return (T) new TimerViewModel(application);
        } else if (modelClass == RangeCalenderViewModel.class) {
            return (T) new RangeCalenderViewModel(application);
        } else if (modelClass == LoginViewModel.class) {
            return (T) new LoginViewModel(application);
        } else if (modelClass == CalenderViewModel.class) {
            return (T) new CalenderViewModel(application);
        } else if (modelClass == SettingsViewModel.class) {
            return (T) new SettingsViewModel(application);
        } else if (modelClass == BackupViewModel.class) {
            return (T) new BackupViewModel(application);
        } else if (modelClass == RestoreViewModel.class) {
            return (T) new RestoreViewModel(application);
        } else if (modelClass == ExpiredMissionViewModel.class) {
            return (T) new ExpiredMissionViewModel(application);
        }
        return null;
    }
}