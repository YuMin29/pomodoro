package com.yumin.pomodoro.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.Mission;
import com.yumin.pomodoro.databinding.FragmentAddMissionBindingImpl;
import com.yumin.pomodoro.utils.MissionItem;
import com.yumin.pomodoro.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

//TODO: ViewModel class shouldn't import any android.* or view.* class

public class AddMissionViewModel extends ViewModelProvider.AndroidViewModelFactory {
    public static final String TAG = "[AddMissionViewModel]";

    private enum Visibility{
        VISIBLE(0),
        INVISIBLE(1),
        GONE(8);

        private int value;

        Visibility(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }


    private Application mApplication;
    private FragmentAddMissionBindingImpl mFragmentAddMissionBinding;
    public MutableLiveData<Mission> missionMutableLiveData = new MutableLiveData<>();
    Mission mMission;
    List<MissionItem> missionItems = new ArrayList<>();
    public MutableLiveData<List<MissionItem>> countViewItemList = new MutableLiveData<>();

    public AddMissionViewModel(@NonNull Application application, FragmentAddMissionBindingImpl fragmentAddMissionBinding) {
        super(application);
        this.mApplication = application;
        this.mFragmentAddMissionBinding = fragmentAddMissionBinding;
        mMission = new Mission();
        init(application);
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
        missionMutableLiveData.postValue(mMission);
    }

    private void init(Application application){
        missionItems = new ArrayList<>();
        missionItems.add(new MissionItem(application.getApplicationContext(), "25", R.string.mission_time, Visibility.VISIBLE.getValue(), Visibility.VISIBLE.getValue()));
        missionItems.add(new MissionItem(application.getApplicationContext(), "5", R.string.mission_break, Visibility.VISIBLE.getValue(), Visibility.VISIBLE.getValue()));
        missionItems.add(new MissionItem(application.getApplicationContext(), "15", R.string.mission_long_break, Visibility.VISIBLE.getValue(), Visibility.VISIBLE.getValue()));
        missionItems.add(new MissionItem(application.getApplicationContext(), "0", R.string.mission_goal, Visibility.VISIBLE.getValue(), Visibility.VISIBLE.getValue()));
        missionItems.add(new MissionItem(application.getApplicationContext(), "0", R.string.mission_repeat, Visibility.VISIBLE.getValue(), Visibility.VISIBLE.getValue()));
        missionItems.add(new MissionItem(application.getApplicationContext(),"每天",R.string.mission_day,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"藍",R.string.mission_theme,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"",R.string.mission_notification,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"",R.string.mission_sound,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"",R.string.mission_sound_level,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"",R.string.mission_vibrate,Visibility.GONE.getValue(),Visibility.GONE.value));
        missionItems.add(new MissionItem(application.getApplicationContext(),"",R.string.mission_keep_awake,Visibility.GONE.getValue(),Visibility.GONE.value));
        countViewItemList.setValue(missionItems);
    }
    public List<MissionItem> getCountViewList(){
        return this.countViewItemList.getValue();
    }

    public void setCountItem(int position, String count){

    }

    public void addCountItem(int position){
        LogUtil.logD(TAG,"[updateCountItem] position = "+position);
        MissionItem item = missionItems.get(position);
        int count = Integer.valueOf(item.getContent());
        count++;
        item.setContent(String.valueOf(count));
        missionItems.set(position,item);
        countViewItemList.setValue(missionItems);
    }

    public void minusCountItem(int position){
        LogUtil.logD(TAG,"[updateCountItem] position = "+position);
        MissionItem item = missionItems.get(position);
        int count = Integer.valueOf(item.getContent());

        if (count > 0)
            count--;
        item.setContent(String.valueOf(count));
        missionItems.set(position,item);
        countViewItemList.setValue(missionItems);
    }
}
