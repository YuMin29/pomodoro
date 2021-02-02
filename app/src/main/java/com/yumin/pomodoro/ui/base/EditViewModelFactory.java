package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiService;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;

public class EditViewModelFactory implements ViewModelProvider.Factory {
    private ApiService apiService;
    private Application application;
    private int editId;

    public EditViewModelFactory(Application application, ApiService apiService, int editId) {
        this.apiService = apiService;
        this.application = application;
        this.editId = editId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditMissionViewModel(application,new RoomRepository(apiService));
    }
}
