package com.yumin.pomodoro.ui.timer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.yumin.pomodoro.data.MissionSettings;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LiveSharedPreference;
import com.yumin.pomodoro.utils.TimeToMillisecondUtil;

public class TimerViewModel extends AndroidViewModel {
    private static final String TAG = TimerViewModel.class.getSimpleName();
    private static final String QUICK_MISSION = "quick_mission";
    private RoomRepository mRoomRepository;
    private LiveData<UserMission> mMission;
    private LiveData<Integer> mMissionNumberOfCompletion;
    private LiveData<MissionState> mMissionState;
    private MissionSettings mMissionSettings;
    private LiveData<Boolean> mAutoStartNextMission;
    private MutableLiveData<String> missionStringId = new MutableLiveData<>();
    private MediatorLiveData<Result> mFetchDataResult;
    private final String preferenceName = "missionSettings";
    private SharedPreferences mSharedPreferences;
    private static final String KEY_TIMER_SERVICE_STATUS = "timer_service_status";

    public TimerViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mMissionSettings = new MissionSettings(application);
        fetchMission();
        mSharedPreferences = application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    private void fetchMission(){
        mMission = Transformations.switchMap(missionStringId, new Function<String, LiveData<UserMission>>() {
            @Override
            public LiveData<UserMission> apply(String id) {
                if (id.equals(QUICK_MISSION)) {
                    MutableLiveData<UserMission> result = new MutableLiveData<>();
                    result.postValue(mRoomRepository.getQuickMission());
                    return result;
                } else {
                    return mRoomRepository.getMissionById(id);
                }
            }
        });

        mMissionNumberOfCompletion = Transformations.switchMap(missionStringId, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String id) {
                MutableLiveData<Integer> result = new MutableLiveData<>();
                if (id.equals(QUICK_MISSION)){
                    result.postValue(-1);
                    return result;
                } else {
                    return mRoomRepository.getNumberOfCompletionById(id, TimeToMillisecondUtil.getTodayStartTime());
                }
            }
        });

        mMissionState = Transformations.switchMap(missionStringId, new Function<String, LiveData<MissionState>>() {
            @Override
            public LiveData<MissionState> apply(String id) {
                if (id.equals(QUICK_MISSION)) {
                    MutableLiveData<MissionState> result = new MutableLiveData<>();
                    result.postValue(null);
                    return result;
                } else {
                    return mRoomRepository.getMissionStateByToday(id, TimeToMillisecondUtil.getTodayStartTime());
                }
            }
        });


        mFetchDataResult = new MediatorLiveData<>();
        Result result = new Result();
        mFetchDataResult.addSource(mMission, new Observer<UserMission>() {
            @Override
            public void onChanged(UserMission mission) {
                result.mission = mission;
                result.missionInit = true;
                mFetchDataResult.setValue(result);
            }
        });
        mFetchDataResult.addSource(mMissionNumberOfCompletion, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                result.missionNumberOfCompletion = (integer == null ? 0 : integer);
                result.completionInit = true;
                mFetchDataResult.setValue(result);
            }
        });
        mFetchDataResult.addSource(mMissionState, new Observer<MissionState>() {
            @Override
            public void onChanged(MissionState missionState) {
                result.missionState = missionState;
                result.missionStateInit = true;
                mFetchDataResult.setValue(result);
            }
        });

        mAutoStartNextMission = mMissionSettings.getAutoStartNextMission();
    }

    public void setMissionStringId(String id){
        missionStringId.postValue(id);
    }

    public LiveData<Result> gerFetchDataResult(){
        return mFetchDataResult;
    }

    public LiveSharedPreference<Integer> getTimerServiceStatus(){
        return new LiveSharedPreference<Integer>(mSharedPreferences, KEY_TIMER_SERVICE_STATUS, 0);
    }

    public LiveData<UserMission> getMission(){
        return mMission;
    }

    public void updateMissionState(boolean finished, int completeOfNumber){
        mRoomRepository.updateMissionFinishedState(missionStringId.getValue(),finished,completeOfNumber);
    }

    public LiveData<Integer> getMissionNumberOfCompletion(){
        return mMissionNumberOfCompletion;
    }

    public LiveData<MissionState> getMissionState(){
        return mMissionState;
    }

    public void initMissionState(){
        if (!missionStringId.getValue().equals(QUICK_MISSION))
            mRoomRepository.initMissionState(missionStringId.getValue());
    }

    public LiveData<Boolean> getAutoStartNextMission(){
        return mAutoStartNextMission;
    }

    public class Result{
        public UserMission mission;
        public int missionNumberOfCompletion;
        public MissionState missionState;
        private boolean completionInit = false;
        private boolean missionInit = false;
        private boolean missionStateInit = false;

        public boolean isInit(){
            return missionInit && completionInit && missionStateInit;
        }
    }
}
