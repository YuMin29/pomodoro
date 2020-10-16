package com.yumin.pomodoro.ui.home;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;

import com.yumin.pomodoro.utils.CountView;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionEventHandler {
    private static final String TAG = "[AddMissionEventHandler]";
    private TextWatcher textWatcher;
    private AddMissionViewModel mAddMissionViewModel;
    public CountView.CountViewListener countViewListener;

    public AddMissionEventHandler(AddMissionViewModel addMissionViewModel) {
        mAddMissionViewModel = addMissionViewModel;
        this.textWatcher = getTextWatcherIns();
        countViewListener = new CountView.CountViewListener() {
            @Override
            public View.OnClickListener onAddButtonClick() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.logD(TAG, "[onAddButtonClick] id = " + v.getId());
                    }
                };
            }

            @Override
            public View.OnClickListener onMinusButtonClock() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.logD(TAG, "[onMinusButtonClock] id = " + v.getId());
                    }
                };
            }
        };
    }

    public void onCountViewClick(View view){
        LogUtil.logD(TAG, "[onCountViewClick] id = " + view.getId());
    }

    private TextWatcher getTextWatcherIns() {
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
                mAddMissionViewModel.setMissionTitle(s.toString());
            }
        };
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    @BindingAdapter("textChangedListener")
    public static void bindTextWatcher(EditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
}
