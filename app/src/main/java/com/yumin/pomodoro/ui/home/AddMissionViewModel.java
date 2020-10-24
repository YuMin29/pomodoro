package com.yumin.pomodoro.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
import com.yumin.pomodoro.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends ViewModelProvider.AndroidViewModelFactory {
    public static final String TAG = "[AddMissionViewModel]";

//    public MutableLiveData<String> missionTitle = new MutableLiveData<>();
//    public MutableLiveData<Integer> missionTime = new MutableLiveData<>();
//    public MutableLiveData<String> missionBreak = new MutableLiveData<>();
//    public MutableLiveData<Mission> mission = new MutableLiveData<>();
    private Application mApplication;
    private FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    public MutableLiveData<Mission> missionMutableLiveData = new MutableLiveData<>();
    Mission mMission;

    public AddMissionViewModel(@NonNull Application application, FragmentAddMissionBindingImpl fragmentAddMissionBinding) {
        super(application);
        this.mApplication = application;
        this.mFragmentAddMissionBinding = fragmentAddMissionBinding;
        mMission = new Mission();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Application.class,FragmentAddMissionBindingImpl.class)
                    .newInstance(mApplication,mFragmentAddMissionBinding);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return super.create(modelClass);
    }

    public void setMissionTitle(String title){
        mMission.setName(title);
        missionMutableLiveData.postValue(mMission);
    }

    public void setMissionTime(int time){
        mMission.setTime(time);
    }
}
