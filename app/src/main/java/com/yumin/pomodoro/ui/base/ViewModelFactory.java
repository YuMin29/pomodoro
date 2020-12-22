package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.HomeViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.RangeCalenderViewModel;
import com.yumin.pomodoro.ui.main.viewmodel.TimerViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ApiHelper apiHelper;
    private Application application;
    private int missionId;

    public ViewModelFactory(Application application, ApiHelper apiHelper, int missionId){
        this.apiHelper = apiHelper;
        this.application = application;
        this.missionId = missionId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AddMissionViewModel.class) {
            return (T) new AddMissionViewModel(application,new MainRepository(apiHelper));
        } else if (modelClass == HomeViewModel.class) {
            return (T) new HomeViewModel(application,new MainRepository(apiHelper));
        } else if (modelClass == EditMissionViewModel.class) {
            return (T) new EditMissionViewModel(application,new MainRepository(apiHelper), missionId);
        } else if (modelClass == TimerViewModel.class) {
            return (T) new TimerViewModel(application,new MainRepository(apiHelper), missionId);
        } else if (modelClass == RangeCalenderViewModel.class) {
            return (T) new RangeCalenderViewModel(application,new MainRepository(apiHelper), missionId);
        }
        return null;
    }
}
