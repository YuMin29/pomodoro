package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginViewModel extends AndroidViewModel {
    private final String TAG = LoginViewModel.class.getSimpleName();
    private RoomRepository mRoomRepository;
    private FirebaseRepository mFirebaseRepository;
    private LiveData<List<UserMission>> mFirebaseMissions;
    private LiveData<List<MissionState>> mFirebaseMissionStates;
    private LiveData<List<UserMission>> mRoomMissions;
    private LiveData<List<MissionState>> mRoomMissionState;
    private MediatorLiveData mResultMediatorLiveData = new MediatorLiveData();
    private MutableLiveData<Boolean> mFirebaseUserExist = new MutableLiveData<>();
    private MutableLiveData<Boolean> mNavigateToHome = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        mRoomMissions = mRoomRepository.getMissions();
        mRoomMissionState = mRoomRepository.getMissionStateList();

        mFirebaseMissions = Transformations.switchMap(mFirebaseUserExist, new Function<Boolean, LiveData<List<UserMission>>>() {
            @Override
            public LiveData<List<UserMission>> apply(Boolean input) {
                return mFirebaseRepository.getMissions();
            }
        });
        mFirebaseMissionStates = Transformations.switchMap(mFirebaseUserExist, new Function<Boolean, LiveData<List<MissionState>>>() {
            @Override
            public LiveData<List<MissionState>> apply(Boolean input) {
                return mFirebaseRepository.getMissionStateList();
            }
        });

        Result restoreProgressResult = new Result();
        mResultMediatorLiveData.addSource(mFirebaseMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                LogUtil.logE(TAG, "[resultMediatorLiveData] mFirebaseMissions size = " + userMissionList.size());
                restoreProgressResult.setFirebaseMissions(userMissionList);
                mResultMediatorLiveData.setValue(restoreProgressResult);
            }
        });

        mResultMediatorLiveData.addSource(mFirebaseMissionStates, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStateList) {
                LogUtil.logE(TAG, "[resultMediatorLiveData] mFirebaseMissionStates size = " + missionStateList.size());
                restoreProgressResult.setFirebaseMissionStates(missionStateList);
                mResultMediatorLiveData.setValue(restoreProgressResult);
            }
        });

        mResultMediatorLiveData.addSource(mRoomMissions, new Observer<List<UserMission>>() {
            @Override
            public void onChanged(List<UserMission> userMissionList) {
                LogUtil.logE(TAG, "[resultMediatorLiveData] mRoomMissions size = " + userMissionList.size());
                restoreProgressResult.setRoomMissions(userMissionList);
                mResultMediatorLiveData.setValue(restoreProgressResult);
            }
        });

        mResultMediatorLiveData.addSource(mRoomMissionState, new Observer<List<MissionState>>() {
            @Override
            public void onChanged(List<MissionState> missionStateList) {
                LogUtil.logE(TAG, "[resultMediatorLiveData] mRoomMissionState size = " + missionStateList.size());
                restoreProgressResult.setRoomMissionStates(missionStateList);
                mResultMediatorLiveData.setValue(restoreProgressResult);
            }
        });
    }

    public MediatorLiveData<Result> getResultMediatorLiveData() {
        return mResultMediatorLiveData;
    }

    public void setFirebaseUserExist(boolean exist) {
        mFirebaseUserExist.postValue(exist);
    }

    public void syncFirebaseMissionsToRoom() {
        for (UserMission userMission : mFirebaseMissions.getValue()) {
            if (!mRoomMissions.getValue().contains(userMission)) {
                UserMission insertUserMission = userMission;
                insertUserMission.setId(0);

                disposables.add(mRoomRepository.addMissionAndGetId(insertUserMission)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull String id) {

                                if (mFirebaseMissionStates.getValue().isEmpty()) {
                                    mNavigateToHome.postValue(true);
                                    return;
                                }

                                syncMissionState(id,insertUserMission.getFirebaseMissionId());
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            }
                        }));
            }
        }
        mNavigateToHome.postValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposables != null && !disposables.isDisposed()){
            disposables.dispose();
        }
    }

    public void syncMissionState(String missionId, String firebaseMissionId) {
        for (MissionState missionState : mFirebaseMissionStates.getValue()) {
            if (missionState.getMissionId().equals(firebaseMissionId)) {
                MissionState insertMissionState = missionState;
                insertMissionState.setMissionId(missionId);
                if (!mRoomMissionState.getValue().contains(insertMissionState)) {
                    insertMissionState.setId(0);
                    mRoomRepository.saveMissionState(insertMissionState.getMissionId(),insertMissionState);
                }
            }
        }
        mNavigateToHome.postValue(true);
    }

    public LiveData<Boolean> getNavigateToHome() {
        return mNavigateToHome;
    }

    public class Result {
        private List<UserMission> firebaseMissions = null;
        private List<MissionState> firebaseMissionStates = null;
        private List<UserMission> roomMissions = null;
        private List<MissionState> roomMissionStates = null;

        public void setFirebaseMissions(List<UserMission> list) {
            firebaseMissions = list;
        }

        public List<UserMission> getFirebaseMissions() {
            return firebaseMissions;
        }

        public void setFirebaseMissionStates(List<MissionState> list) {
            firebaseMissionStates = list;
        }

        public List<MissionState> getFirebaseMissionStates() {
            return firebaseMissionStates;
        }

        public void setRoomMissions(List<UserMission> list) {
            roomMissions = list;
        }

        public List<UserMission> getRoomMissions() {
            return roomMissions;
        }

        public void setRoomMissionStates(List<MissionState> list) {
            roomMissionStates = list;
        }

        public List<MissionState> getRoomMissionStates() {
            return roomMissionStates;
        }

        public boolean isComplete() {
            LogUtil.logE(TAG, "[isComplete] RETURN = " +
                    (firebaseMissions != null && firebaseMissionStates != null && roomMissions != null && roomMissionStates != null));
            return firebaseMissions != null && firebaseMissionStates != null && roomMissions != null && roomMissionStates != null;
        }
    }
}
