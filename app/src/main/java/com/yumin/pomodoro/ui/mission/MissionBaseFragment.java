package com.yumin.pomodoro.ui.mission;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.base.DataBindingConfig;
import com.yumin.pomodoro.base.DataBindingFragment;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.ui.SharedViewModel;
import com.yumin.pomodoro.customize.ItemDateView;
import com.yumin.pomodoro.customize.ItemListView;
import com.yumin.pomodoro.utils.LogUtil;

public abstract class MissionBaseFragment extends DataBindingFragment implements ItemListView.RepeatTypeListener, ItemDateView.OperateDayChanged {
    public static final String REPEAT_START = "repeat_start";
    public static final String REPEAT_END = "repeat_end";
    public static final String MISSION_OPERATE_DAY = "mission_operate_day";
    private final String TAG = MissionBaseFragment.class.getSimpleName();
    protected long mLatestRepeatStart = -1L;
    protected long mLatestRepeatEnd = -1L;
    protected long mOperateDay = -1L;
    protected long mRepeatStart = -1L;
    protected long mRepeatEnd = -1L;
    protected SharedViewModel mSharedViewModel;

    protected abstract UserMission getMission();
    protected abstract void updateItemOperateUI(long time);
    protected abstract void setRangeCalenderId();
    protected abstract void updateEditMissionRepeatStart(long time);
    protected abstract void updateEditMissionRepeatEnd(long time);

    @Override
    protected void initViewModel() {
        mSharedViewModel = getApplicationScopeViewModel(SharedViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSharedViewModel.getRepeatStart().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                updateEditMissionRepeatStart(time);
                mLatestRepeatStart = time;
            }
        });

        mSharedViewModel.getRepeatEnd().observeInFragment(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                updateEditMissionRepeatEnd(time);
                mLatestRepeatEnd = time;
            }
        });
    }

    @Override
    public void onOperateChanged(long time) {
        UserMission userMission = getMission();
        if (userMission != null && userMission.getRepeat() == UserMission.TYPE_DEFINE &&
                userMission.getRepeatStart() != -1L && userMission.getRepeatEnd() != -1L) {
            if (time > userMission.getRepeatStart() || time > userMission.getRepeatEnd()) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.notice_choose_operate_day)
                        .setMessage(R.string.notice_clear_repeat_range)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSharedViewModel.setRepeatStart(-1); // clear
                                mSharedViewModel.setRepeatEnd(-1);
                                updateItemOperateUI(time);
                                mOperateDay = time;
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                updateItemOperateUI(time);
                mOperateDay = time;
            }
        } else {
            updateItemOperateUI(time);
            mOperateDay = time;
        }
    }

    @Override
    public void chooseRepeatDefine() {
        LogUtil.logE(TAG,"[chooseRepeatDefine]");
        setRangeCalenderId();
        Bundle bundle = new Bundle();
        bundle.putLong(REPEAT_START, (mLatestRepeatStart != -1L) ? mLatestRepeatStart : mRepeatStart);
        bundle.putLong(REPEAT_END, (mLatestRepeatEnd != -1L) ? mLatestRepeatEnd : mRepeatEnd);
        bundle.putLong(MISSION_OPERATE_DAY, mOperateDay);
        NavHostFragment.findNavController(this).navigate(R.id.fragment_range_calender,bundle);
    }

    @Override
    public void chooseRepeatNonDefine() {
    }

    protected void navigateUp(){
        NavHostFragment.findNavController(this).navigateUp();
    }
}
