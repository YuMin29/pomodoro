package com.yumin.pomodoro.ui.main.clickhandler;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;


import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionClickHandler {
    private static final String TAG = "[AddMissionEventHandler]";
    private TextWatcher textWatcher;
    private AddMissionViewModel mAddMissionViewModel;
    private Context context;

    public AddMissionClickHandler(Context context,AddMissionViewModel addMissionViewModel) {
        this.context = context;
        mAddMissionViewModel = addMissionViewModel;
        this.textWatcher = getTextWatcherInstance();
    }

    private TextWatcher getTextWatcherInstance() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do some thing
                LogUtil.logD(TAG, "[beforeTextChanged] S = " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do some thing
                LogUtil.logD(TAG, "[onTextChanged] S = " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do some thing
                LogUtil.logD(TAG, "[afterTextChanged] S = " + s.toString());
//                mAddMissionViewModel.setMissionName(s.toString());
            }
        };
    }

    public void onSaveButtonClick(){
        LogUtil.logD(TAG,"[onSaveButtonClick]");
        mAddMissionViewModel.saveMission();

    }

    public void onCancelButtonClick(){
        LogUtil.logD(TAG,"[onCancelButtonClick]");
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }
}
