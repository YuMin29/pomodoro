package com.yumin.pomodoro.ui.login;

import android.app.AlertDialog;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.MissionState;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.data.repository.firebase.FirebaseApiServiceImpl;
import com.yumin.pomodoro.data.repository.firebase.FirebaseRepository;
import com.yumin.pomodoro.data.repository.firebase.User;
import com.yumin.pomodoro.data.repository.room.RoomApiServiceImpl;
import com.yumin.pomodoro.data.repository.room.RoomRepository;
import com.yumin.pomodoro.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
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
    private final CompositeDisposable disposables = new CompositeDisposable();

    private LoginNavigator mLoginNavigator;
    private FirebaseAuth mFirebaseAuth;
    public MutableLiveData<Boolean> loginEnabled = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRoomRepository = new RoomRepository(new RoomApiServiceImpl(application));
        mFirebaseRepository = new FirebaseRepository(new FirebaseApiServiceImpl(application));
        mRoomMissions = mRoomRepository.getMissions();
        mRoomMissionState = mRoomRepository.getMissionStateList();
        mFirebaseMissions = mFirebaseRepository.getMissions();
        mFirebaseMissionStates = mFirebaseRepository.getMissionStateList();

        mFirebaseMissions = Transformations.switchMap(mFirebaseUserExist, input -> mFirebaseRepository.getMissions());
        mFirebaseMissionStates = Transformations.switchMap(mFirebaseUserExist, input -> mFirebaseRepository.getMissionStateList());

        Result restoreProgressResult = new Result();
        mResultMediatorLiveData.addSource(mFirebaseMissions, (Observer<List<UserMission>>) userMissionList -> {
            LogUtil.logE(TAG, "[resultMediatorLiveData] mFirebaseMissions size = " + userMissionList.size());
            restoreProgressResult.setFirebaseMissions(userMissionList);
            mResultMediatorLiveData.setValue(restoreProgressResult);
        });

        mResultMediatorLiveData.addSource(mFirebaseMissionStates, (Observer<List<MissionState>>) missionStateList -> {
            LogUtil.logE(TAG, "[resultMediatorLiveData] mFirebaseMissionStates size = " + missionStateList.size());
            restoreProgressResult.setFirebaseMissionStates(missionStateList);
            mResultMediatorLiveData.setValue(restoreProgressResult);
        });

        mResultMediatorLiveData.addSource(mRoomMissions, (Observer<List<UserMission>>) userMissionList -> {
            LogUtil.logE(TAG, "[resultMediatorLiveData] mRoomMissions size = " + userMissionList.size());
            restoreProgressResult.setRoomMissions(userMissionList);
            mResultMediatorLiveData.setValue(restoreProgressResult);
        });

        mResultMediatorLiveData.addSource(mRoomMissionState, (Observer<List<MissionState>>) missionStateList -> {
            LogUtil.logE(TAG, "[resultMediatorLiveData] mRoomMissionState size = " + missionStateList.size());
            restoreProgressResult.setRoomMissionStates(missionStateList);
            mResultMediatorLiveData.setValue(restoreProgressResult);
        });
    }

    public void init(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        loginEnabled.setValue(true);
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AlertDialog alertDialog = mLoginNavigator.createProgressBarDialog();
        alertDialog.show();
        loginEnabled.setValue(false);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener(alertDialog));
    }

    @NotNull
    private OnCompleteListener<AuthResult> getOnCompleteListener(AlertDialog progressBarDialog) {
        return task -> {
            progressBarDialog.dismiss();

            if (task.isSuccessful()) {
                singInSuccess();
            } else {
                try {
                    throw task.getException();
                } catch (Exception e) {
                    LogUtil.logD(TAG, "[signInWithCredential] exception = " + e.getStackTrace());
                    if (e instanceof FirebaseAuthInvalidUserException)
                        mLoginNavigator.showToast(getApplication().getString(R.string.invalid_user_exception));
                    if (e instanceof FirebaseAuthInvalidCredentialsException)
                        mLoginNavigator.showToast(getApplication().getString(R.string.invalid_credential_exception));
                    if (e instanceof FirebaseAuthUserCollisionException)
                        mLoginNavigator.showToast(getApplication().getString(R.string.user_collision_exception));
                }
            }
            loginEnabled.setValue(true);
        };
    }

    private void singInSuccess() {
        LogUtil.logD(TAG, "[singInSuccess]");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            addUserToFirebase(user);
            setFirebaseUserExist(true);
        }
    }

    private void addUserToFirebase(FirebaseUser firebaseUser){
        User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        LogUtil.logE(TAG, "[addUserToFirebase] getUid = " + firebaseUser.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    LogUtil.logE(TAG, "[addUserToFirebase] getUid = " + firebaseUser.getUid() + " doesn't exist!");
                    LogUtil.logE(TAG, "[addUserToFirebase] set value name = " + user.getUserName() + " ,mail = " + user.getUserMail());
                    // The child doesn't exist
                    databaseReference.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void loginWithAccount(String email, String password){
        AlertDialog alertDialog = mLoginNavigator.createProgressBarDialog();
        alertDialog.show();
        loginEnabled.setValue(false);
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            singInSuccess();
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                LogUtil.logD(TAG, "[signInWithEmailAndPassword] exception : " + e.getStackTrace());
                                if (e instanceof FirebaseAuthInvalidUserException)
                                    mLoginNavigator.showToast(getApplication().getString(R.string.sign_in_email_exception));
                                if (e instanceof FirebaseAuthInvalidCredentialsException)
                                    mLoginNavigator.showToast(getApplication().getString(R.string.sign_in_password_exception));
                            }
                        }
                        loginEnabled.setValue(true);
                    }
                });
    }

    void handleGoogleSignIn(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        AlertDialog progressBarDialog = mLoginNavigator.createProgressBarDialog();
        progressBarDialog.show();
        loginEnabled.setValue(false);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getOnCompleteListener(progressBarDialog));
    }

    void setLoginNavigator(LoginNavigator loginNavigator){
        mLoginNavigator = loginNavigator;
    }

    public MediatorLiveData<Result> getResultMediatorLiveData() {
        return mResultMediatorLiveData;
    }

    private void setFirebaseUserExist(boolean exist) {
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
                                    mLoginNavigator.navigateToHome();
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
        mLoginNavigator.navigateToHome();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposables != null && !disposables.isDisposed()){
            disposables.dispose();
        }
    }

    void syncMissionState(String missionId, String firebaseMissionId) {
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
        mLoginNavigator.navigateToHome();
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

        public void setRoomMissions(List<UserMission> list) {
            roomMissions = list;
        }

        public void setRoomMissionStates(List<MissionState> list) {
            roomMissionStates = list;
        }

        public boolean isComplete() {
            LogUtil.logE(TAG, "[isComplete] RETURN = " +
                    (firebaseMissions != null && firebaseMissionStates != null && roomMissions != null && roomMissionStates != null));
            return firebaseMissions != null && firebaseMissionStates != null && roomMissions != null && roomMissionStates != null;
        }
    }
}
