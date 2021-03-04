package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.LoginViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
//    private ApiService apiService;
    private Application application;

    public ViewModelFactory(Application application){
        this.application = application;
//        this.apiService = apiService;
    }

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
        }
        return null;
    }
}
