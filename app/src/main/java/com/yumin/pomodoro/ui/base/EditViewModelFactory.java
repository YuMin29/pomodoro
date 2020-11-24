package com.yumin.pomodoro.ui.base;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.api.ApiHelper;
import com.yumin.pomodoro.data.repository.MainRepository;
import com.yumin.pomodoro.ui.main.viewmodel.EditMissionViewModel;

public class EditViewModelFactory implements ViewModelProvider.Factory {
    private ApiHelper apiHelper;
    private Application application;
    private int editId;

    public EditViewModelFactory(Application application, ApiHelper apiHelper, int editId) {
        this.apiHelper = apiHelper;
        this.application = application;
        this.editId = editId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditMissionViewModel(application,new MainRepository(apiHelper),editId);
    }
}
